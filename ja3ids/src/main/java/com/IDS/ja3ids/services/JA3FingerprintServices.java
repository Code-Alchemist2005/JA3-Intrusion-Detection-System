package com.IDS.ja3ids.services;

import com.IDS.ja3ids.entity.JA3Fingerprint;
import com.IDS.ja3ids.repository.JA3FingerprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JA3FingerprintServices {

    @Autowired
    private JA3FingerprintRepository repository;

    private final Map<String, Integer> frequencyMap = new HashMap<>();
    private final int DEFAULT_THRESHOLD = 5; // hits threshold for warning

    // ---------------------------
    // Save fingerprint
    // ---------------------------
    public JA3Fingerprint saveFingerprint(JA3Fingerprint fp) {
        return repository.save(fp);
    }

    // ---------------------------
    // Check if hash already exists
    // ---------------------------
    public boolean existsByJa3Hash(String ja3Hash) {
        return repository.findByJa3Hash(ja3Hash) != null;
    }

    // ---------------------------
    // Get all fingerprints
    // ---------------------------
    public List<JA3Fingerprint> getAllFingerprints() {
        return repository.findAll();
    }

    // ---------------------------
    // Get fingerprint by hash
    // ---------------------------
    public Optional<JA3Fingerprint> getByHash(String ja3Hash) {
        return Optional.ofNullable(repository.findByJa3Hash(ja3Hash));
    }

    // ---------------------------
    // Malicious check
    // ---------------------------
    public boolean isMalicious(String ja3Hash) {
        JA3Fingerprint fp = repository.findByJa3Hash(ja3Hash);
        return fp != null && Boolean.TRUE.equals(fp.getMalicious());
    }

    // ---------------------------
    // Threat level check
    // ---------------------------
    public int getThreatLevel(String ja3Hash) {
        JA3Fingerprint fp = repository.findByJa3Hash(ja3Hash);
        return fp != null ? fp.getThreatLevel() : 0;
    }

    // ---------------------------
    // Increment frequency (IDS logic)
    // ---------------------------
    public int incrementFrequency(String ja3Hash) {
        int hits = frequencyMap.getOrDefault(ja3Hash, 0) + 1;
        frequencyMap.put(ja3Hash, hits);
        return hits;
    }

    // ---------------------------
    // Frequency threshold
    // ---------------------------
    public int getFrequencyThreshold() {
        return DEFAULT_THRESHOLD;
    }
}
