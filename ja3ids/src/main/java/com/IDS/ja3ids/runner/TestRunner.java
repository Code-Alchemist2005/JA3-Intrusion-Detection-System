package com.IDS.ja3ids.runner;

import com.IDS.ja3ids.entity.TestEntity;
import com.IDS.ja3ids.repository.TestEntityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private final TestEntityRepository repo;

    public TestRunner(TestEntityRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        TestEntity t = new TestEntity();
        t.setName("Spring Boot Test");
        repo.save(t);
        System.out.println("Saved entity with ID: " + t.getId());
    }
}
