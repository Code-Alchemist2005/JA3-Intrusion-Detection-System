package com.IDS.ja3ids.DTO;

public class MIDecisionResponse {

    private String prediction;
    private int malicious;
    private String ja3Hash;
    private int hits;
    private int threatLevel;
    private String status;
    private String message;

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public int getMalicious() {
        return malicious;
    }

    public void setMalicious(int malicious) {
        this.malicious = malicious;
    }

    public String getJa3Hash() {
        return ja3Hash;
    }

    public void setJa3Hash(String ja3Hash) {
        this.ja3Hash = ja3Hash;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(int threatLevel) {
        this.threatLevel = threatLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
