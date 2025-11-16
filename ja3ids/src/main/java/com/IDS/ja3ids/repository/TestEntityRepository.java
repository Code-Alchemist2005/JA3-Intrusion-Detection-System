package com.IDS.ja3ids.repository;

import com.IDS.ja3ids.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
}
