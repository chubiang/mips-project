package com.mips.global.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.enums.Exchange;
import com.mips.domain.stock.service.SubscribeManagerService;
import com.mips.global.config.properties.FinnhubProperties;
import com.mips.global.event.FinnhubDisconnectedEvent;
import com.mips.global.handler.FinnhubWebSocketHandler;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
public class FinnhubWebSocketConnectionManager {
    private final WebSocketClient client;
    private final FinnhubWebSocketHandler handler;
    private final FinnhubProperties properties;
    private final ObjectMapper objectMapper;
    private final SubscribeManagerService subscribeManagerService;
    private final ThreadPoolTaskScheduler reconnectScheduler;
//    private final Set<String> targetTickers;
    private WebSocketSession session;
    private ScheduledFuture<?> retry;
    private boolean connecting;
    private boolean stopping;
    private long retrySeconds = 1;

    @Autowired
    public FinnhubWebSocketConnectionManager(WebSocketClient client,
                                             FinnhubWebSocketHandler handler, FinnhubProperties properties,
                                             ObjectMapper objectMapper, SubscribeManagerService subscribeManagerService) {
        this(client, handler, properties, objectMapper, subscribeManagerService, createScheduler());
    }

    // 별도 스케줄러를 사용해 기존 Batch 스케줄과 분리한다.
    FinnhubWebSocketConnectionManager(WebSocketClient client,
                                      FinnhubWebSocketHandler handler, FinnhubProperties properties,
                                      ObjectMapper objectMapper, SubscribeManagerService subscribeManagerService, ThreadPoolTaskScheduler scheduler) {
        this.client = client;
        this.handler = handler;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.subscribeManagerService = subscribeManagerService;
        this.reconnectScheduler = scheduler;
//        this.targetTickers = new LinkedHashSet<>(properties.getSubscribeSymbols());
    }

    private static ThreadPoolTaskScheduler createScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("finnhub-reconnect-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void connect() {
        if (stopping || connecting || retry != null || session != null) {
            return;
        }
        URI uri = UriComponentsBuilder.fromUriString(properties.getWebsocketUrl())
                .queryParam("token", properties.getSecret()).build().encode().toUri();
        connecting = true;
        try {
            client.execute(handler, null, uri).whenComplete(this::onConnected);
        } catch (RuntimeException e) {
            onConnected(null, e);
        }
    }

    private synchronized void onConnected(WebSocketSession connected, Throwable failure) {
        connecting = false;
        if (stopping) {
            close(connected);
            return;
        }
        if (failure != null || connected == null || !connected.isOpen()) {
            close(connected);
            // 예외 메시지에 인증 token을 포함한 URI가 들어갈 수 있다.
            log.warn("Finnhub WebSocket 연결 실패");
            scheduleReconnect();
            return;
        }
        session = connected;
        try {
            List<SecurityMaster> targetTickers =  subscribeManagerService.getTickerList(Exchange.NASDAQ);
            int cnt = 0;
            for (SecurityMaster securityMaster : targetTickers) {
                if (cnt >= 5) break;
                sendSubscription(securityMaster.getTicker());
                cnt++;
            }
            retrySeconds = 1;
            log.info("Finnhub WebSocket 연결 및 구독 요청 완료: {}", targetTickers);
        } catch (IOException | RuntimeException e) {
            disconnectAndRetry();
        }
    }

    private void sendSubscription(String ticker) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                Map.of("type", "subscribe", "symbol", ticker))));
    }

    @EventListener
    public synchronized void onDisconnected(FinnhubDisconnectedEvent event) {
        if (stopping || session == null || !session.getId().equals(event.sessionId())) {
            return;
        }
        disconnectAndRetry();
    }

    private void disconnectAndRetry() {
        WebSocketSession previous = session;
        session = null; // close가 종료 이벤트를 다시 보내도 중복 처리하지 않는다.
        close(previous);
        scheduleReconnect();
    }

    private void scheduleReconnect() {
        if (stopping || retry != null) {
            return;
        }
        long delay = retrySeconds;
        retrySeconds = Math.min(retrySeconds * 2, 30);
        log.info("Finnhub WebSocket {}초 후 재연결 예약", delay);
        retry = reconnectScheduler.schedule(this::runReconnect, Instant.now().plusSeconds(delay));
    }

    private synchronized void runReconnect() {
        retry = null;
        connect();
    }

    private void close(WebSocketSession target) {
        if (target != null && target.isOpen()) {
            try {
                target.close();
            } catch (IOException e) {
                log.warn("Finnhub WebSocket 세션 종료 실패: {}", target.getId());
            }
        }
    }

    @PreDestroy
    public synchronized void shutdown() {
        stopping = true;
        if (retry != null) {
            retry.cancel(false);
            retry = null;
        }
        WebSocketSession previous = session;
        session = null;
        close(previous);
        reconnectScheduler.shutdown();
    }
}
