package com.IDS.ja3ids.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
public class TrafficSimulatorService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();
    private static final String CHECK_URL = "http://localhost:8080/api/ja3/process/";

    // Sample JA3 hashes (mix of benign & malicious)
    private final String[] sampleHashes = {
            "72a589da586844d7f0818ce684948eea",
            "3e7d3f90b2bc3aef3ad17c9f123b2b39",
            "e7d705a3286e19ea42f587b344ee6865",
            "9a1f3a7c8dce09c9123dd876f4ab1231",
            "cd08e31494f9531f560d64c695473da9"
    };

    @Scheduled(fixedRate = 5000) // runs every 5 seconds
    public void simulateTraffic() {
        try {
            String randomHash = sampleHashes[random.nextInt(sampleHashes.length)];
            String url = CHECK_URL + randomHash;

            System.out.println("\n[+] Simulating traffic for JA3 hash: " + randomHash);
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("[IDS RESULT] " + response);

        } catch (Exception ex) {
            System.out.println("[ERROR] Traffic simulation failed: " + ex.getMessage());
        }
    }
}
