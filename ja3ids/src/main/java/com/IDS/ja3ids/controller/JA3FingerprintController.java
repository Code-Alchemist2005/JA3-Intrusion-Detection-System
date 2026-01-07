package com.IDS.ja3ids.controller;

import com.IDS.ja3ids.services.JA3FingerprintServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ja3")
@CrossOrigin(origins = "*") // 🔥 IMPORTANT: allow dashboard to fetch data
public class JA3FingerprintController {

    @Autowired
    private JA3FingerprintServices service;

    // =========================
    // 🔥 IDS PROCESS (SIMULATOR / REAL INPUT)
    // =========================
    @GetMapping("/process/{ja3Hash}")
    public ResponseEntity<Map<String, Object>> processJa3(
            @PathVariable String ja3Hash) {
        return ResponseEntity.ok(service.buildIDSResponse(ja3Hash));
    }

    // =========================
    // 🔥 DASHBOARD CARD (LATEST EVENT)
    // =========================
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> latestIDS() {
        return ResponseEntity.ok(service.getLatestIDSResult());
    }

    // =========================
    // 🔥 REAL IDS LOG FEED (EVENT HISTORY)
    // =========================
    @GetMapping("/logs")
    public ResponseEntity<List<Map<String, Object>>> getLogs() {
        return ResponseEntity.ok(service.getAllEvents());
    }
}
