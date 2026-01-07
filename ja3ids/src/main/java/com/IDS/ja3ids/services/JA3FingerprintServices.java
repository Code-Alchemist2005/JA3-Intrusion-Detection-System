package com.IDS.ja3ids.services;

import com.IDS.ja3ids.entity.JA3Fingerprint;
import com.IDS.ja3ids.repository.JA3FingerprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class JA3FingerprintServices {

    @Autowired
    private JA3FingerprintRepository repository;

    // =========================
    // JA3 HIT COUNTER
    // =========================
    private final Map<String, Integer> frequencyMap = new HashMap<>();

    // =========================
    // LATEST IDS RESULT (CARD)
    // =========================
    private Map<String, Object> latestIDSResult = new LinkedHashMap<>();

    // =========================
    // IDS EVENT BUFFER (LOGS + GRAPHS)
    // =========================
    private final LinkedList<Map<String, Object>> eventBuffer = new LinkedList<>();
    private static final int MAX_EVENTS = 100;

    // =========================
    // GETTERS FOR DASHBOARD
    // =========================
    public synchronized Map<String, Object> getLatestIDSResult() {
        return latestIDSResult;
    }

    public synchronized List<Map<String, Object>> getAllEvents() {
        return eventBuffer;
    }

    // =========================
    // CORE IDS LOGIC
    // =========================
    private int incrementFrequency(String ja3Hash) {
        int hits = frequencyMap.getOrDefault(ja3Hash, 0) + 1;
        frequencyMap.put(ja3Hash, hits);
        return hits;
    }

    public synchronized Map<String, Object> buildIDSResponse(String ja3Hash) {

        int hits = incrementFrequency(ja3Hash);
        JA3Fingerprint fp = repository.findByJa3Hash(ja3Hash);

        boolean malicious = fp != null && Boolean.TRUE.equals(fp.getMalicious());

        // Hybrid logic:
        // malicious signature > hit-based heuristic
        int threatLevel = malicious ? 7 : Math.min(hits, 3);

        String status = malicious ? "WARNING" : "OK";
        String message = malicious
                ? "Malware signature detected"
                : "Traffic normal";

        // =========================
        // RESPONSE OBJECT (ORDERED)
        // =========================
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("hits", hits);
        response.put("ja3Hash", ja3Hash);
        response.put("malicious", malicious);
        response.put("message", message);
        response.put("threatLevel", threatLevel);
        response.put("status", status);
        response.put("timestamp", Instant.now().toString());

        // =========================
        // UPDATE DASHBOARD STATE
        // =========================
        latestIDSResult = response;

        // =========================
        // ADD TO EVENT BUFFER
        // =========================
        eventBuffer.addFirst(response);
        if (eventBuffer.size() > MAX_EVENTS) {
            eventBuffer.removeLast();
        }

        return response;
    }
}
