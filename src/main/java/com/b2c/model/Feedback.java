package com.b2c.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "feedback")
@NamedQuery(name = "Feedback.findAll", query = "SELECT f FROM Feedback f")
@Data
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Lob
    private String answer1;

    @Lob
    private String answer2;

    @Lob
    private String answer3;

    @Lob
    private String answer4;

    @Column(name = "customer_id")
    private String customerId;

    private String email;

    @Lob
    @Column(name = "feedback_of_product")
    private String feedbackOfProduct;

    private String mobile;

    private String name;

    @Lob
    @Column(name = "problems_faced_with_app")
    private String problemsFacedWithApp;

    @Lob
    private String question1;

    @Lob
    private String question2;

    @Lob
    private String question3;

    @Lob
    private String question4;

    @Column(name = "rating_about_product")
    private int ratingAboutProduct;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitted_date")
    private Date submittedDate;

    @Lob
    @Column(name = "suggestions_or_comments")
    private String suggestionsOrComments;


}