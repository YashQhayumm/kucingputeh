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

    public int getRideId() { return ride_id; }
    public void setRideId(int ride_id) { this.ride_id = ride_id; }

    public int getReviewerId() { return reviewer_id; }
    public void setReviewerId(int reviewer_id) { this.reviewer_id = reviewer_id; }

    public int getRevieweeId() { return reviewee_id; }
    public void setRevieweeId(int reviewee_id) { this.reviewee_id = reviewee_id; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
