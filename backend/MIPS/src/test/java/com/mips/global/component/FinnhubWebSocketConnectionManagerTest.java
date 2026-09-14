package com.mips.global.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mips.domain.stock.service.SubscribeManagerService;
import com.mips.global.config.properties.FinnhubProperties;
import com.mips.global.event.FinnhubDisconnectedEvent;
import com.mips.global.handler.FinnhubWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.WebSocketClient;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FinnhubWebSocketConnectionManagerTest {
    private final WebSocketClient client = mock(WebSocketClient.class);
    private final ThreadPoolTaskScheduler scheduler = mock(ThreadPoolTaskScheduler.class);
    private final ScheduledFuture<?> scheduled = mock(ScheduledFuture.class);
    private final SubscribeManagerService subscribeManagerService = mock(SubscribeManagerService.class);
    private final List<Runnable> retries = new ArrayList<>();
    private final List<Instant> deadlines = new ArrayList<>();
    private FinnhubWebSocketConnectionManager manager;

    @BeforeEach
    void setup() {
        FinnhubProperties properties = new FinnhubProperties();
        properties.setWebsocketUrl("wss://example.invalid");
        properties.setSecret("test-only");
        properties.setSubscribeSymbols(List.of("AAPL", "MSFT"));
        manager = new FinnhubWebSocketConnectionManager(client,
                mock(FinnhubWebSocketHandler.class), properties, new ObjectMapper(), subscribeManagerService, scheduler);
        doAnswer(invocation -> {
            retries.add(invocation.getArgument(0));
            deadlines.add(invocation.getArgument(1));
            return scheduled;
        }).when(scheduler).schedule(any(Runnable.class), any(Instant.class));
    }

    private WebSocketSession session(String id) {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn(id);
        when(session.isOpen()).thenReturn(true);
        return session;
    }

    @Test
    void subscribesEveryTickerAgainAfterDisconnectAndIgnoresOldEvents() throws Exception {
        WebSocketSession first = session("first");
        WebSocketSession second = session("second");
        when(client.execute(any(), isNull(), any(URI.class)))
                .thenReturn(CompletableFuture.completedFuture(first),
                        CompletableFuture.completedFuture(second));
        manager.connect();
        verify(first, times(2)).sendMessage(any(TextMessage.class));
        manager.onDisconnected(new FinnhubDisconnectedEvent("first"));
        manager.onDisconnected(new FinnhubDisconnectedEvent("first"));
        assertEquals(1, retries.size());
        retries.get(0).run();
        verify(second, times(2)).sendMessage(any(TextMessage.class));
        manager.onDisconnected(new FinnhubDisconnectedEvent("first"));
        assertEquals(1, retries.size());
    }

    @Test
    void preventsParallelConnectionAttempts() {
        when(client.execute(any(), isNull(), any(URI.class))).thenReturn(new CompletableFuture<>());
        manager.connect();
        manager.connect();
        verify(client).execute(any(), isNull(), any(URI.class));
    }

    @Test
    void connectionFailuresBackOffUpToThirtySeconds() {
        when(client.execute(any(), isNull(), any(URI.class)))
                .thenAnswer(invocation -> CompletableFuture.failedFuture(new IllegalStateException()));
        manager.connect();
        long[] delays = {1, 2, 4, 8, 16, 30, 30};
        for (int i = 0; i < delays.length; i++) {
            long remaining = java.time.Duration.between(Instant.now(), deadlines.get(i)).toMillis();
            assertTrue(remaining <= delays[i] * 1000 && remaining > (delays[i] - 1) * 1000);
            if (i + 1 < delays.length) retries.get(i).run();
        }
    }

    @Test
    void failedSubscriptionClosesSessionAndSchedulesReconnect() throws Exception {
        WebSocketSession first = session("first");
        doThrow(new java.io.IOException()).when(first).sendMessage(any());
        when(client.execute(any(), isNull(), any(URI.class)))
                .thenReturn(CompletableFuture.completedFuture(first));
        manager.connect();
        verify(first).close();
        assertEquals(1, retries.size());
    }

    @Test
    void shutdownCancelsRetryAndPreventsReconnect() {
        when(client.execute(any(), isNull(), any(URI.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException()));
        manager.connect();
        manager.shutdown();
        retries.get(0).run();
        verify(scheduled).cancel(false);
        verify(client).execute(any(), isNull(), any(URI.class));
    }

    @Test
    void connectionCompletingAfterShutdownIsClosedWithoutSubscription() throws Exception {
        CompletableFuture<WebSocketSession> connecting = new CompletableFuture<>();
        when(client.execute(any(), isNull(), any(URI.class))).thenReturn(connecting);
        manager.connect();
        manager.shutdown();
        WebSocketSession late = session("late");
        connecting.complete(late);
        verify(late).close();
        verify(late, never()).sendMessage(any());
        assertTrue(retries.isEmpty());
    }
}
