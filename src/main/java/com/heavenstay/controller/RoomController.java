package com.heavenstay.controller;

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

import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired private RoomService   roomService;
    @Autowired private ReviewService reviewService;

    private User getStaff(HttpSession session) {
        if (SessionUtils.isStaff(session)) return SessionUtils.getUser(session);
        return null;
    }

    private boolean isAdmin(HttpSession session) {
        return SessionUtils.isAdmin(session);
    }

    @GetMapping
    public String listRooms(
            HttpSession session,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            Model model) {

        User staff = getStaff(session);
        if (staff == null) return "redirect:/login";

        List<Room> rooms;
        if (ValidationUtils.isNotEmpty(status)) {
            rooms = roomService.getAllRooms();
            rooms.removeIf(r -> !r.getStatus().equalsIgnoreCase(status));
        } else if (ValidationUtils.isNotEmpty(type)) {
            rooms = roomService.getRoomsByType(type.toUpperCase());
        } else {
            rooms = roomService.getAllRooms();
        }

        model.addAttribute("user",    staff);
        model.addAttribute("rooms",   rooms);
        model.addAttribute("isAdmin", isAdmin(session));
        model.addAttribute("filter",  ValidationUtils.isNotEmpty(status) ? status : type);
        return "room/list";
    }

    @GetMapping("/{roomNumber}")
    public String roomDetail(
            HttpSession session,
            @PathVariable String roomNumber,
            Model model) {

        User staff = getStaff(session);
        if (staff == null) return "redirect:/login";

        Room room = roomService.getRoomByNumber(roomNumber);
        if (room == null) return "redirect:/rooms";

        model.addAttribute("user",      staff);
        model.addAttribute("room",      room);
        model.addAttribute("reviews",   reviewService.getReviewsByRoom(roomNumber));
        model.addAttribute("avgRating", reviewService.getAverageRating(roomNumber));
        model.addAttribute("isAdmin",   isAdmin(session));
        return "room/detail";
    }

    @GetMapping("/add")
    public String addRoomForm(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/rooms";
        model.addAttribute("user", SessionUtils.getUser(session));
        return "room/add";
    }

    @PostMapping("/add")
    public String addRoom(
            HttpSession session,
            @RequestParam String roomNumber,
            @RequestParam String type,
            @RequestParam double pricePerNight,
            @RequestParam int    floorNumber,
            @RequestParam String description,
            RedirectAttributes ra) {

        if (!isAdmin(session)) return "redirect:/rooms";

        if (!ValidationUtils.isNotEmpty(roomNumber)) {
            ra.addFlashAttribute("error", "Room number is required.");
            return "redirect:/rooms/add";
        }
        if (!ValidationUtils.isValidRoomType(type)) {
            ra.addFlashAttribute("error", "Invalid room type.");
            return "redirect:/rooms/add";
        }

        Room room = roomService.addRoom(
                roomNumber.trim(), type.toUpperCase(),
                pricePerNight, floorNumber,
                description != null ? description.trim() : ""
        );

        if (room == null) {
            ra.addFlashAttribute("error",
                    "Room " + roomNumber + " already exists or is invalid.");
            return "redirect:/rooms/add";
        }

        ra.addFlashAttribute("success", "Room " + roomNumber + " added successfully.");
        return "redirect:/rooms";
    }

    @GetMapping("/edit/{roomNumber}")
    public String editRoomForm(
            HttpSession session,
            @PathVariable String roomNumber,
            Model model) {

        if (!isAdmin(session)) return "redirect:/rooms";

        Room room = roomService.getRoomByNumber(roomNumber);
        if (room == null) return "redirect:/rooms";

        model.addAttribute("user", SessionUtils.getUser(session));
        model.addAttribute("room", room);
        return "room/edit";
    }

    @PostMapping("/update")
    public String updateRoom(
            HttpSession session,
            @RequestParam String roomNumber,
            @RequestParam String type,
            @RequestParam double pricePerNight,
            @RequestParam int    floorNumber,
            @RequestParam String description,
            @RequestParam String status,
            RedirectAttributes ra) {

        if (!isAdmin(session)) return "redirect:/rooms";

        if (!ValidationUtils.isValidRoomType(type)) {
            ra.addFlashAttribute("error", "Invalid room type.");
            return "redirect:/rooms/edit/" + roomNumber;
        }
        if (!ValidationUtils.isValidRoomStatus(status)) {
            ra.addFlashAttribute("error", "Invalid room status.");
            return "redirect:/rooms/edit/" + roomNumber;
        }

        boolean updated = roomService.updateRoom(
                roomNumber, type.toUpperCase(),
                pricePerNight, floorNumber,
                description != null ? description.trim() : ""
        );

        if (updated) {
            roomService.updateRoomStatus(roomNumber, status.toUpperCase());
            ra.addFlashAttribute("success", "Room " + roomNumber + " updated.");
        } else {
            ra.addFlashAttribute("error", "Failed to update room " + roomNumber + ".");
        }

        return "redirect:/rooms/" + roomNumber;
    }

    @PostMapping("/delete")
    public String deleteRoom(
            HttpSession session,
            @RequestParam String roomNumber,
            RedirectAttributes ra) {

        if (!isAdmin(session)) return "redirect:/rooms";

        boolean deleted = roomService.deleteRoom(roomNumber);
        ra.addFlashAttribute(
                deleted ? "success" : "error",
                deleted ? "Room " + roomNumber + " deleted."
                        : "Cannot delete room " + roomNumber +
                          ". It may be currently BOOKED."
        );
        return "redirect:/rooms";
    }
}