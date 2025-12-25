package com.IDS.ja3ids.controller;

import com.IDS.ja3ids.DTO.MIDecisionResponse;
import com.IDS.ja3ids.DTO.MIRequest;
import com.IDS.ja3ids.entity.JA3Fingerprint;
import com.IDS.ja3ids.services.JA3FingerprintServices;
import com.IDS.ja3ids.services.MLClientService;
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

    @Autowired
    private MLClientService mlClientService;

    // ==========================================================
    // ADD JA3 FINGERPRINT (RULE-BASED DB)
    // ==========================================================
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

    // ==========================================================
    // GET ALL JA3 FINGERPRINTS
    // ==========================================================
    @GetMapping("/all")
    public ResponseEntity<List<JA3Fingerprint>> getAllFingerprints() {
        return ResponseEntity.ok(service.getAllFingerprints());
    }

    // ==========================================================
    // GET JA3 BY HASH
    // ==========================================================
    @GetMapping("/hash/{ja3Hash}")
    public ResponseEntity<?> getByHash(@PathVariable String ja3Hash) {

        Optional<JA3Fingerprint> fp = service.getByHash(ja3Hash);

        if (fp.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "JA3 hash not found");
            response.put("ja3Hash", ja3Hash);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(fp.get());
    }

    // ==========================================================
    // RULE-BASED IDS CHECK (NO ML)
    // ==========================================================
    @GetMapping("/process/{ja3Hash}")
    public ResponseEntity<Map<String, Object>> processJa3(@PathVariable String ja3Hash) {
        Map<String, Object> response = service.buildIDSResponse(ja3Hash);
        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // 🔥 HYBRID IDS ENDPOINT (ML + RULE-BASED)
    // ==========================================================
    @PostMapping("/hybrid/predict")
    public ResponseEntity<MIDecisionResponse> hybridPredict(@RequestBody MIRequest request) {

        // 1️⃣ Call ML Flask server
        Map<String, Object> mlResult = mlClientService.getMLPrediction(request);

        String prediction = (String) mlResult.get("prediction");

        // Safe handling of malicious field
        Object malObj = mlResult.get("malicious");
        int malicious;

        if (malObj instanceof Boolean) {
            malicious = (Boolean) malObj ? 1 : 0;
        } else {
            malicious = ((Number) malObj).intValue();
        }

        // 2️⃣ Compute JA3 hash + IDS frequency
        String ja3Hash = service.computeJA3Hash(request);
        int hits = service.incrementFrequency(ja3Hash);

        // 3️⃣ HYBRID DECISION LOGIC (FIXED)
        int threatLevel;
        String status;

        if (malicious == 1 && hits >= 3) {
            threatLevel = 5;
            status = "BLOCKED";
        } else if (hits >= 5) {
            threatLevel = 4;
            status = "BLOCKED";
        } else if (malicious == 1) {
            threatLevel = 3;
            status = "ALLOWED";
        } else {
            threatLevel = 2;
            status = "ALLOWED";
        }

        // 4️⃣ Build response
        MIDecisionResponse response = new MIDecisionResponse();
        response.setPrediction(prediction);
        response.setMalicious(malicious);
        response.setJa3Hash(ja3Hash);
        response.setHits(hits);
        response.setThreatLevel(threatLevel);
        response.setStatus(status);
        response.setMessage("Hybrid IDS decision applied");

        return ResponseEntity.ok(response);
    }
}
