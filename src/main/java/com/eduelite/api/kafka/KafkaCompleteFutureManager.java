package com.eduelite.api.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


import org.springframework.stereotype.Component;

@Component
public class KafkaCompleteFutureManager {
	
	private final Map<String, CompletableFuture<Object>> requestFutures = new ConcurrentHashMap<>();
	
	public void addRequestFuture(String requestId, CompletableFuture<Object> future) {
        requestFutures.put(requestId, future);
    }

    public void removeRequestFuture(String requestId) {
        requestFutures.remove(requestId);
    }
    
    public CompletableFuture<Object> getRequestFuture(String requestId) {
        return requestFutures.get(requestId);
    }

}
