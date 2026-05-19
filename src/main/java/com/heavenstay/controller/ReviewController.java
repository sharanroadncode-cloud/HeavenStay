package com.heavenstay.controller;

import com.heavenstay.models.Review;
import com.heavenstay.models.Room;
import com.heavenstay.models.User;
import com.heavenstay.service.ReviewService;
import com.heavenstay.service.RoomService;
import com.heavenstay.utils.SessionUtils;
import com.heavenstay.utils.ValidationUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired private ReviewService reviewService;
    @Autowired private RoomService   roomService;

    @GetMapping
    public String allReviews(
            HttpSession session,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String rating,
            Model model) {

        if (!SessionUtils.isStaff(session)) return "redirect:/login";

        User user = SessionUtils.getUser(session);
        List<Review> reviews;

        if (ValidationUtils.isNotEmpty(roomNumber)) {
            reviews = reviewService.getReviewsByRoom(roomNumber.trim());
        } else {
            reviews = reviewService.getAllReviews();
        }

        if (ValidationUtils.isNotEmpty(rating)) {
            int r = ValidationUtils.parseInt(rating, 0);
            if (r > 0) {
                List<Review> filtered = new ArrayList<>();
                for (Review rv : reviews) {
                    if (rv.getRating() == r) filtered.add(rv);
                }
                reviews = filtered;
            }
        }

        Map<String, Double>  roomAvgMap   = new LinkedHashMap<>();
        Map<String, Integer> roomCountMap = new LinkedHashMap<>();
        for (Room room : roomService.getAllRooms()) {
            String rn = room.getRoomNumber();
            roomAvgMap.put(rn,   reviewService.getAverageRating(rn));
            roomCountMap.put(rn, reviewService.getReviewsByRoom(rn).size());
        }

        long[] starCounts = new long[6];
        for (Review rv : reviewService.getAllReviews()) {
            if (rv.getRating() >= 1 && rv.getRating() <= 5) {
                starCounts[rv.getRating()]++;
            }
        }

        model.addAttribute("user",         user);
        model.addAttribute("reviews",      reviews);
        model.addAttribute("roomAvgMap",   roomAvgMap);
        model.addAttribute("roomCountMap", roomCountMap);
        model.addAttribute("starCounts",   starCounts);
        model.addAttribute("allRooms",     roomService.getAllRooms());
        model.addAttribute("filterRoom",   roomNumber);
        model.addAttribute("filterRating", rating);
        model.addAttribute("isAdmin",      SessionUtils.isAdmin(session));
        return "review/list";
    }

    @GetMapping("/room/{roomNumber}")
    public String reviewsByRoom(
            HttpSession session,
            @PathVariable String roomNumber,
            Model model) {

        if (!SessionUtils.isLoggedIn(session)) return "redirect:/login";

        Room room = roomService.getRoomByNumber(roomNumber);
        if (room == null) return "redirect:/reviews";

        List<Review> reviews   = reviewService.getReviewsByRoom(roomNumber);
        double       avgRating = reviewService.getAverageRating(roomNumber);

        long[] starCounts = new long[6];
        for (Review r : reviews) {
            if (r.getRating() >= 1 && r.getRating() <= 5) {
                starCounts[r.getRating()]++;
            }
        }

        model.addAttribute("user",       SessionUtils.getUser(session));
        model.addAttribute("room",       room);
        model.addAttribute("reviews",    reviews);
        model.addAttribute("avgRating",  avgRating);
        model.addAttribute("starCounts", starCounts);
        model.addAttribute("isAdmin",    SessionUtils.isAdmin(session));
        return "review/room-reviews";
    }

    @GetMapping("/submit")
    public String submitReviewForm(
            HttpSession session,
            @RequestParam(required = false) String bookingId,
            Model model) {

        if (!SessionUtils.isCustomer(session)) return "redirect:/login";

        model.addAttribute("user",      SessionUtils.getUser(session));
        model.addAttribute("bookingId", bookingId);
        return "review/submit";
    }

    @PostMapping("/submit")
    public String submitReview(
            HttpSession session,
            @RequestParam String bookingId,
            @RequestParam int    rating,
            @RequestParam String comment,
            RedirectAttributes ra) {

        if (!SessionUtils.isCustomer(session)) return "redirect:/login";
        User user = SessionUtils.getUser(session);

        if (!ValidationUtils.isNotEmpty(bookingId)) {
            ra.addFlashAttribute("error", "Booking ID is required.");
            return "redirect:/reviews/submit";
        }
        if (!ValidationUtils.isValidRating(rating)) {
            ra.addFlashAttribute("error", "Rating must be between 1 and 5.");
            return "redirect:/reviews/submit";
        }
        if (!ValidationUtils.isNotEmpty(comment)) {
            ra.addFlashAttribute("error", "Comment cannot be empty.");
            return "redirect:/reviews/submit";
        }

        Review review = reviewService.submitReview(
                user.getUserId(), bookingId.trim(), rating, comment.trim()
        );

        if (review == null) {
            ra.addFlashAttribute("error",
                    "Could not submit review. Booking must be completed and not already reviewed.");
            return "redirect:/reviews/submit?bookingId=" + bookingId;
        }

        ra.addFlashAttribute("success",
                "Review submitted! Room " + review.getRoomNumber() +
                        " — " + review.getStarRating());
        return "redirect:/customer/reviews";
    }

    @GetMapping("/admin")
    public String adminReviews(HttpSession session, Model model) {
        if (!SessionUtils.isAdmin(session)) return "redirect:/login";

        User admin = SessionUtils.getUser(session);
        List<Review> allReviews = reviewService.getAllReviews();

        Map<String, Double>  roomAvgMap   = new LinkedHashMap<>();
        Map<String, Integer> roomCountMap = new LinkedHashMap<>();
        for (Room room : roomService.getAllRooms()) {
            String rn = room.getRoomNumber();
            roomAvgMap.put(rn,   reviewService.getAverageRating(rn));
            roomCountMap.put(rn, reviewService.getReviewsByRoom(rn).size());
        }

        double overallAvg = 0;
        if (!allReviews.isEmpty()) {
            int total = 0;
            for (Review r : allReviews) total += r.getRating();
            overallAvg = (double) total / allReviews.size();
        }

        long[] starCounts = new long[6];
        String mostReviewedRoom = "";
        String highestRatedRoom = "";
        int    maxCount = 0;
        double maxAvg   = 0;

        for (Review r : allReviews) {
            if (r.getRating() >= 1 && r.getRating() <= 5) {
                starCounts[r.getRating()]++;
            }
        }
        for (Map.Entry<String, Integer> e : roomCountMap.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount        = e.getValue();
                mostReviewedRoom = e.getKey();
            }
        }
        for (Map.Entry<String, Double> e : roomAvgMap.entrySet()) {
            if (e.getValue() > maxAvg) {
                maxAvg           = e.getValue();
                highestRatedRoom = e.getKey();
            }
        }

        model.addAttribute("admin",            admin);
        model.addAttribute("allReviews",       allReviews);
        model.addAttribute("roomAvgMap",       roomAvgMap);
        model.addAttribute("roomCountMap",     roomCountMap);
        model.addAttribute("overallAvg",       overallAvg);
        model.addAttribute("starCounts",       starCounts);
        model.addAttribute("totalReviews",     allReviews.size());
        model.addAttribute("mostReviewedRoom", mostReviewedRoom);
        model.addAttribute("highestRatedRoom", highestRatedRoom);
        model.addAttribute("allRooms",         roomService.getAllRooms());
        return "review/admin-reviews";
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(
            HttpSession session,
            @PathVariable String reviewId,
            @RequestParam(required = false) String returnTo,
            RedirectAttributes ra) {

        if (!SessionUtils.isAdmin(session)) return "redirect:/login";

        boolean deleted = reviewService.deleteReview(reviewId);
        ra.addFlashAttribute(
                deleted ? "success" : "error",
                deleted ? "Review " + reviewId + " deleted."
                        : "Could not delete review: " + reviewId
        );

        if ("admin".equals(returnTo))           return "redirect:/reviews/admin";
        if (returnTo != null && returnTo.startsWith("room-")) {
            return "redirect:/reviews/room/" + returnTo.replace("room-", "");
        }
        return "redirect:/reviews";
    }

    @GetMapping("/{reviewId}")
    public String reviewDetail(
            HttpSession session,
            @PathVariable String reviewId,
            Model model) {

        if (!SessionUtils.isStaff(session)) return "redirect:/login";

        Review review = null;
        for (Review r : reviewService.getAllReviews()) {
            if (r.getReviewId().equals(reviewId)) {
                review = r;
                break;
            }
        }
        if (review == null) return "redirect:/reviews";

        Room room = roomService.getRoomByNumber(review.getRoomNumber());

        model.addAttribute("user",      SessionUtils.getUser(session));
        model.addAttribute("review",    review);
        model.addAttribute("room",      room);
        model.addAttribute("avgRating", reviewService.getAverageRating(review.getRoomNumber()));
        model.addAttribute("isAdmin",   SessionUtils.isAdmin(session));
        return "review/detail";
    }
}