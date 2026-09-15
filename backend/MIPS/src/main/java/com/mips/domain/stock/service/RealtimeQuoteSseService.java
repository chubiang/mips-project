package com.mips.domain.stock.service;

import com.mips.domain.stock.dto.RealtimeQuoteCache;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RealtimeQuoteSseService {

    private static final long SSE_TIMEOUT = 30L * 60L * 1000L;
    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(error -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(emitter);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void publish(RealtimeQuoteCache quote) {
        emitters.removeIf(emitter -> !send(emitter, quote));
    }

    private boolean send(SseEmitter emitter, RealtimeQuoteCache quote) {
        try {
            emitter.send(SseEmitter.event()
                    .name("quote")
                    .id(quote.ticker() + ":" + quote.quotedAt().toEpochMilli())
                    .data(quote));
            return true;
        } catch (IOException | IllegalStateException e) {
            emitter.complete();
            return false;
        }
    }
}
