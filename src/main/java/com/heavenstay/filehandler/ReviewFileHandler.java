package com.heavenstay.filehandler;

import com.heavenstay.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewFileHandler {

    private static final String FILE_PATH = "data/reviews.txt";

    public static List<Review> loadAllReviews() {
        List<String> lines = FileManager.readLines(FILE_PATH);
        List<Review> reviews = new ArrayList<>();
        for (String line : lines) {
            try {
                reviews.add(Review.fromFileString(line));
            } catch (Exception e) {
                System.out.println("[WARN] Skipping review line: " + line);
            }
        }
        return reviews;
    }

    public static boolean saveAllReviews(List<Review> reviews) {
        List<String> lines = new ArrayList<>();
        for (Review r : reviews) lines.add(r.toFileString());
        return FileManager.writeLines(FILE_PATH, lines);
    }

    public static boolean appendReview(Review review) {
        if (review == null) return false;
        return FileManager.appendLine(FILE_PATH, review.toFileString());
    }

    public static Review findById(String reviewId) {
        if (reviewId == null || reviewId.trim().isEmpty()) return null;
        for (Review r : loadAllReviews()) {
            if (r.getReviewId().equals(reviewId)) return r;
        }
        return null;
    }

    public static List<Review> findByRoomNumber(String roomNumber) {
        List<Review> result = new ArrayList<>();
        if (roomNumber == null || roomNumber.trim().isEmpty()) return result;
        for (Review r : loadAllReviews()) {
            if (r.getRoomNumber().equals(roomNumber)) result.add(r);
        }
        return result;
    }

    public static List<Review> findByCustomerId(String customerId) {
        List<Review> result = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty()) return result;
        for (Review r : loadAllReviews()) {
            if (r.getCustomerId().equals(customerId)) result.add(r);
        }
        return result;
    }

    public static double getAverageRating(String roomNumber) {
        List<Review> reviews = findByRoomNumber(roomNumber);
        if (reviews.isEmpty()) return 0.0;
        int total = 0;
        for (Review r : reviews) total += r.getRating();
        return (double) total / reviews.size();
    }

    public static boolean deleteReview(String reviewId) {
        if (reviewId == null || reviewId.trim().isEmpty()) return false;
        List<Review> reviews = loadAllReviews();
        boolean removed = reviews.removeIf(r -> r.getReviewId().equals(reviewId));
        if (!removed) return false;
        return saveAllReviews(reviews);
    }
}