package com.IDS.ja3ids.services;

import com.IDS.ja3ids.DTO.MIRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MLClientService {

    private static final String ML_URL = "http://127.0.0.1:5000/predict";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getMLPrediction(MIRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MIRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                ML_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        return response.getBody();
    }
}
