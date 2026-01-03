package com.IDS.ja3ids.controller;

import com.IDS.ja3ids.services.JA3FingerprintServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ja3")
public class JA3FingerprintController {

    @Autowired
    private JA3FingerprintServices service;

    // 🔥 EXISTING IDS PROCESS
    @GetMapping("/process/{ja3Hash}")
    public ResponseEntity<Map<String, Object>> processJa3(@PathVariable String ja3Hash) {
        return ResponseEntity.ok(service.buildIDSResponse(ja3Hash));
    }

    // 🔥 DASHBOARD ENDPOINT
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> latestIDS() {
        return ResponseEntity.ok(service.getLatestIDSResult());
    }



}
