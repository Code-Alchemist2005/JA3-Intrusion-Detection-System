package com.IDS.ja3ids.repository;

import com.IDS.ja3ids.entity.JA3Fingerprint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JA3FingerprintRepository extends JpaRepository<JA3Fingerprint, Long> {
    JA3Fingerprint findByJa3Hash(String ja3Hash);

    // Add this for recent fingerprints
    List<JA3Fingerprint> findTop50ByOrderByCreatedAtDesc();
}
