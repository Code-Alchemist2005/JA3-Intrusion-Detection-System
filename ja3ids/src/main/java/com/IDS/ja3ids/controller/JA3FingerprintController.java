package com.IDS.ja3ids.controller;

import com.IDS.ja3ids.entity.JA3Fingerprint;
import com.IDS.ja3ids.services.JA3FingerprintServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ja3")
public class JA3FingerprintController {

    @Autowired
    private JA3FingerprintServices service;

    // ---------------------------
    // Add fingerprint with duplicate check
    // ---------------------------
    @PostMapping("/add")
    public ResponseEntity<?> addFingerprint(@RequestBody JA3Fingerprint fp) {
        if (service.existsByJa3Hash(fp.getJa3Hash())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "JA3 hash already exists in database");
            response.put("ja3Hash", fp.getJa3Hash());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        JA3Fingerprint saved = service.saveFingerprint(fp);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------------------------
    // Get all fingerprints
    // ---------------------------
    @GetMapping("/all")
    public ResponseEntity<List<JA3Fingerprint>> getAllFingerprints() {
        return ResponseEntity.ok(service.getAllFingerprints());
    }

    // ---------------------------
    // Get fingerprint by JA3 hash
    // ---------------------------
    @GetMapping("/hash/{ja3Hash}")
    public ResponseEntity<?> getByHash(@PathVariable String ja3Hash) {
        Optional<JA3Fingerprint> fp = service.getByHash(ja3Hash);
        if (fp.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "JA3 hash not found in database");
            response.put("ja3Hash", ja3Hash);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(fp.get());
    }

    // ---------------------------
    // Check malicious & threat level
    // ---------------------------
    @GetMapping("/check/{ja3Hash}")
    public ResponseEntity<Map<String, Object>> checkJa3(@PathVariable String ja3Hash) {
        Map<String, Object> result = new HashMap<>();
        result.put("ja3Hash", ja3Hash);
        result.put("malicious", service.isMalicious(ja3Hash));
        result.put("threatLevel", service.getThreatLevel(ja3Hash));
        return ResponseEntity.ok(result);
    }

    // ---------------------------
    // Process with IDS logic (frequency + malicious detection)
    // ---------------------------
    @GetMapping("/process/{ja3Hash}")
    public ResponseEntity<Map<String, Object>> processJa3(@PathVariable String ja3Hash) {
        Map<String, Object> response = new HashMap<>();
        response.put("ja3Hash", ja3Hash);

        int hits = service.incrementFrequency(ja3Hash);
        response.put("hits", hits);

        if (service.isMalicious(ja3Hash)) {
            response.put("status", "BLOCKED");
            response.put("threatLevel", service.getThreatLevel(ja3Hash));
        } else if (hits > service.getFrequencyThreshold()) {
            response.put("status", "WARNING");
            response.put("message", "High-frequency suspicious traffic detected");
        } else {
            response.put("status", "OK");
        }

        return ResponseEntity.ok(response);
    }
}
