package com.mips.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mips.domain.stock.dto.FinnhubTradeData;
import com.mips.domain.stock.dto.FinnhubTradeResponse;
import com.mips.domain.stock.service.FinnhubRealtimeQuoteService;
import com.mips.global.event.FinnhubDisconnectedEvent;
import org.springframework.context.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinnhubWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final FinnhubRealtimeQuoteService finnhubRealtimeQuoteService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("FinnhubWebsocketHandler afterConnectionEstablished {}", session.getId());
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("FinnhubWebsocketHandler handleTextMessage {}", message.getPayload());
        FinnhubTradeResponse response = objectMapper.readValue(message.getPayload(), FinnhubTradeResponse.class);
        if (response == null
                || !"trade".equals(response.type())
                || response.data() == null) {
            return;
        }

        response.data().stream()
                .filter(data ->
                        data != null
                                && data.s() != null
                                && data.t() != null
                )
                .collect(Collectors.toMap(
                        FinnhubTradeData::s,
                        Function.identity(),
                        BinaryOperator.maxBy(
                                Comparator.comparing(FinnhubTradeData::t)
                        )
                ))
                .values()
                .forEach(finnhubRealtimeQuoteService::saveLatestPrice);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("FinnhubWebsocketHandler afterConnectionClosed {}",status.getCode());
        eventPublisher.publishEvent(new FinnhubDisconnectedEvent(session.getId()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("Finnhub WebSocket 전송 오류: session={}", session.getId());
        eventPublisher.publishEvent(new FinnhubDisconnectedEvent(session.getId()));
    }

}
