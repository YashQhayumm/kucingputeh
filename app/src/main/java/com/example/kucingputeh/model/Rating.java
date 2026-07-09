package com.example.kucingputeh.model;

public class Rating {
    private int ride_id;
    private int reviewer_id;
    private int reviewee_id;
    private int score;
    private String comments;

    public Rating(int ride_id, int reviewer_id, int reviewee_id, int score, String comments) {
        this.ride_id = ride_id;
        this.reviewer_id = reviewer_id;
        this.reviewee_id = reviewee_id;
        this.score = score;
        this.comments = comments;
    }
}