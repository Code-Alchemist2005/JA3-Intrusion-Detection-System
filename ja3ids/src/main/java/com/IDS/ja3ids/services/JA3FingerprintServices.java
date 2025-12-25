package com.IDS.ja3ids.services;

import com.IDS.ja3ids.DTO.MIRequest;
import com.IDS.ja3ids.entity.JA3Fingerprint;
import com.IDS.ja3ids.repository.JA3FingerprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JA3FingerprintServices {

    @Autowired
    private JA3FingerprintRepository repository;

    private final Map<String, Integer> frequencyMap = new HashMap<>();
    private static final int DEFAULT_THRESHOLD = 5;

    public JA3Fingerprint saveFingerprint(JA3Fingerprint fp) {
        return repository.save(fp);
    }

    public boolean existsByJa3Hash(String ja3Hash) {
        return repository.findByJa3Hash(ja3Hash) != null;
    }

    public List<JA3Fingerprint> getAllFingerprints() {
        return repository.findAll();
    }

    public Optional<JA3Fingerprint> getByHash(String ja3Hash) {
        return Optional.ofNullable(repository.findByJa3Hash(ja3Hash));
    }

    public boolean isMalicious(String ja3Hash) {
        JA3Fingerprint fp = repository.findByJa3Hash(ja3Hash);
        return fp != null && Boolean.TRUE.equals(fp.getMalicious());
    }

    public int getThreatLevel(String ja3Hash) {
        JA3Fingerprint fp = repository.findByJa3Hash(ja3Hash);
        return fp != null ? fp.getThreatLevel() : 0;
    }

    public int incrementFrequency(String ja3Hash) {
        int hits = frequencyMap.getOrDefault(ja3Hash, 0) + 1;
        frequencyMap.put(ja3Hash, hits);
        return hits;
    }

    // 🔥 FIXED JA3 HASH
    public String computeJA3Hash(MIRequest req) {
        try {
            String ja3Raw =
                    req.getTls_version() + "," +
                            req.getCipher_count() + "," +
                            req.getExtension_count() + "," +
                            req.getElliptic_curve_count() + "," +
                            req.getEc_point_format_count() + "," +
                            req.getSource_port() + "," +
                            req.getDest_port();

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(ja3Raw.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to compute JA3 hash", e);
        }
    }

    public Map<String, Object> buildIDSResponse(String ja3Hash) {

        Map<String, Object> response = new HashMap<>();
        response.put("ja3Hash", ja3Hash);

        int hits = incrementFrequency(ja3Hash);
        response.put("hits", hits);

        boolean malicious = isMalicious(ja3Hash);
        int threatLevel = getThreatLevel(ja3Hash);

        response.put("malicious", malicious);
        response.put("threatLevel", threatLevel);

        if (malicious) {
            response.put("status", "BLOCKED");
            response.put("message", "Malicious JA3 hash detected");
        } else if (hits > DEFAULT_THRESHOLD) {
            response.put("status", "WARNING");
            response.put("message", "High-frequency suspicious traffic detected");
        } else {
            response.put("status", "OK");
            response.put("message", "Traffic normal");
        }

        return response;
    }
}
