package com.IDS.ja3ids.controller;

import com.IDS.ja3ids.entity.TestEntity;
import com.IDS.ja3ids.repository.TestEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestEntityRepository repository;

    // GET — fetch all rows
    @GetMapping
    public List<TestEntity> getAllTests() {
        return repository.findAll();
    }

    // POST — add new row
    @PostMapping
    public TestEntity createTest(@RequestBody TestEntity entity) {
        return repository.save(entity);
    }

    // GET by ID
    @GetMapping("/{id}")
    public TestEntity getTestById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    // DELETE by ID
    @DeleteMapping("/{id}")
    public String deleteTest(@PathVariable Long id) {
        repository.deleteById(id);
        return "Deleted test with ID: " + id;
    }
}
