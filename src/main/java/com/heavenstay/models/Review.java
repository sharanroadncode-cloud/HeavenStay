package com.heavenstay.models;

public class Review {
    private String reviewId;
    private String customerId;
    private String roomNumber;
    private String bookingId;
    private int rating;
    private String comment;
    private String reviewDate;

    public Review(String reviewId, String customerId, String roomNumber,
                  String bookingId, int rating, String comment, String reviewDate) {
        this.reviewId = reviewId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public String getReviewId() { return reviewId; }
    public String getCustomerId() { return customerId; }
    public String getRoomNumber() { return roomNumber; }
    public String getBookingId() { return bookingId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getReviewDate() { return reviewDate; }

    public void setRating(int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStarRating() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= rating ? "★" : "☆");
        }
        return stars.toString();
    }

    public String toFileString() {
        return reviewId + "|" + customerId + "|" + roomNumber + "|"
                + bookingId + "|" + rating + "|" + comment + "|" + reviewDate;
    }

    public static Review fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length < 7) throw new IllegalArgumentException("Invalid review line: " + line);
        return new Review(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), p[5], p[6]);
    }

    @Override
    public String toString() {
        return "Review " + reviewId + " | Room " + roomNumber
                + " | " + getStarRating() + " | " + comment;
    }
}