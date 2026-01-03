package com.IDS.ja3ids.services;

import com.IDS.ja3ids.entity.JA3Fingerprint;
import com.IDS.ja3ids.repository.JA3FingerprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JA3FingerprintServices {

    @Autowired
    private JA3FingerprintRepository repository;

    private final Map<String, Integer> frequencyMap = new HashMap<>();

    // 🔥 STORE LATEST IDS RESULT
    private Map<String, Object> latestIDSResult = new HashMap<>();

    public synchronized void updateLatestIDSResult(Map<String, Object> result) {
        this.latestIDSResult = result;
    }

    public synchronized Map<String, Object> getLatestIDSResult() {
        return latestIDSResult;
    }

    public int incrementFrequency(String ja3Hash) {
        int hits = frequencyMap.getOrDefault(ja3Hash, 0) + 1;
        frequencyMap.put(ja3Hash, hits);
        return hits;
    }

    public Map<String, Object> buildIDSResponse(String ja3Hash) {

        int hits = incrementFrequency(ja3Hash);
        JA3Fingerprint fp = repository.findByJa3Hash(ja3Hash);

        boolean malicious = fp != null && Boolean.TRUE.equals(fp.getMalicious());
        int threatLevel = malicious ? 5 : Math.min(hits, 3);

        String status = malicious ? "BLOCKED" : "OK";
        String message = malicious
                ? "Malicious JA3 hash detected"
                : "Traffic normal";

        Map<String, Object> response = new HashMap<>();
        response.put("ja3Hash", ja3Hash);
        response.put("hits", hits);
        response.put("malicious", malicious);
        response.put("threatLevel", threatLevel);
        response.put("status", status);
        response.put("message", message);

        // 🔥 STORE FOR DASHBOARD
        updateLatestIDSResult(response);

        return response;
    }



    // =========================
    // FOR DASHBOARD
    // =========================

    }

