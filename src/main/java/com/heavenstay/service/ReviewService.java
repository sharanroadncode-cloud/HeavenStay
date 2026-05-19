package com.heavenstay.service;

import com.heavenstay.filehandler.BookingFileHandler;
import com.heavenstay.filehandler.ReviewFileHandler;
import com.heavenstay.models.Booking;
import com.heavenstay.models.Review;
import com.heavenstay.utils.DateUtils;
import com.heavenstay.utils.IDGenerator;
import com.heavenstay.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    public Review submitReview(String customerId, String bookingId,
                               int rating, String comment) {
        if (!ValidationUtils.isNotEmpty(customerId)) return null;
        if (!ValidationUtils.isNotEmpty(bookingId)) return null;
        if (!ValidationUtils.isValidRating(rating)) return null;
        if (!ValidationUtils.isNotEmpty(comment)) return null;

        Booking booking = BookingFileHandler.findById(bookingId.trim());
        if (booking == null) return null;
        if (!booking.getCustomerId().equals(customerId.trim())) return null;
        if (!Booking.STATUS_COMPLETED.equals(booking.getStatus())) return null;
        if (alreadyReviewed(customerId.trim(), bookingId.trim())) return null;

        String id = IDGenerator.generateReviewId();
        Review review = new Review(
                id,
                customerId.trim(),
                booking.getRoomNumber(),
                bookingId.trim(),
                rating,
                ValidationUtils.sanitize(comment),
                DateUtils.today()
        );

        boolean saved = ReviewFileHandler.appendReview(review);
        return saved ? review : null;
    }

    public List<Review> getAllReviews() {
        return ReviewFileHandler.loadAllReviews();
    }

    public List<Review> getReviewsByRoom(String roomNumber) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return new ArrayList<>();
        return ReviewFileHandler.findByRoomNumber(roomNumber.trim());
    }

    public List<Review> getReviewsByCustomer(String customerId) {
        if (!ValidationUtils.isNotEmpty(customerId)) return new ArrayList<>();
        return ReviewFileHandler.findByCustomerId(customerId.trim());
    }

    public double getAverageRating(String roomNumber) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return 0.0;
        return ReviewFileHandler.getAverageRating(roomNumber.trim());
    }

    public boolean deleteReview(String reviewId) {
        if (!ValidationUtils.isNotEmpty(reviewId)) return false;
        return ReviewFileHandler.deleteReview(reviewId.trim());
    }

    private boolean alreadyReviewed(String customerId, String bookingId) {
        for (Review r : ReviewFileHandler.findByCustomerId(customerId)) {
            if (r.getBookingId().equals(bookingId)) return true;
        }
        return false;
    }
}