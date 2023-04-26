package com.b2c.controller;

import com.b2c.model.Feedback;
import com.b2c.services.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@Tag(name = "Feedback API", description = "API for feedback")
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Feedback> submit(@RequestBody Feedback feedback) {
        return ResponseEntity.ok(service.submit(feedback));
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Feedback>> getAll(@PathVariable String customerId) {
        return ResponseEntity.ok(service.getAll(customerId));
    }

}
