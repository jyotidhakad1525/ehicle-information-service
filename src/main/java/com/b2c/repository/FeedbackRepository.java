package com.b2c.repository;

import com.b2c.model.Feedback;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FeedbackRepository extends CrudRepository<Feedback, Integer> {

    List<Feedback> findAll();

    List<Feedback> findAllByCustomerId(String customerId);

}
