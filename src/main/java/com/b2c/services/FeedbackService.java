package com.b2c.services;

import com.b2c.model.Feedback;
import com.b2c.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository repository;

    public Feedback submit(Feedback feedback) {
        return repository.save(feedback);
    }

    public List<Feedback> getAll() {
        return repository.findAll();
    }

    public List<Feedback> getAll(String customerId) {
        return repository.findAllByCustomerId(customerId);
    }
}
