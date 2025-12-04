package com.IDS.ja3ids.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ja3_fingerprints")
public class JA3Fingerprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ja3Hash;

    @Column(name = "malicious_flag")
    private Boolean malicious = false;

    @Column(name = "threat_level")
    private Integer threatLevel = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJa3Hash() { return ja3Hash; }
    public void setJa3Hash(String ja3Hash) { this.ja3Hash = ja3Hash; }

    // keep the boolean-style isX getter but also provide getX for frameworks/tools
    public Boolean isMalicious() { return malicious; }
    public Boolean getMalicious() { return malicious; } // additional conventional getter
    public void setMalicious(Boolean malicious) { this.malicious = malicious; }

    public Integer getThreatLevel() { return threatLevel; }
    public void setThreatLevel(Integer threatLevel) { this.threatLevel = threatLevel; }

    public Timestamp getCreatedAt() { return createdAt; }
}
