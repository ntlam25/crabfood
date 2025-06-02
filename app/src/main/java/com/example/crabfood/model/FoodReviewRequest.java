package com.example.crabfood.model;

public class FoodReviewRequest {
    private Long foodId;
    private Double rating;
    private String comment;

    public FoodReviewRequest() {}

    public FoodReviewRequest(Long foodId, Double rating, String comment) {
        this.foodId = foodId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
