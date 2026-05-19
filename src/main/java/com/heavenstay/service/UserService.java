package com.heavenstay.service;

import com.heavenstay.filehandler.UserFileHandler;
import com.heavenstay.models.Admin;
import com.heavenstay.models.Customer;
import com.heavenstay.models.Receptionist;
import com.heavenstay.models.User;
import com.heavenstay.utils.IDGenerator;
import com.heavenstay.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    public User login(String email, String password) {
        if (!ValidationUtils.isNotEmpty(email) ||
                !ValidationUtils.isNotEmpty(password)) return null;
        User user = UserFileHandler.findByEmail(email.trim());
        if (user == null) return null;
        if (!user.getPassword().equals(password)) return null;
        return user;
    }

    public Customer registerCustomer(String name, String email,
                                     String password, String phone) {
        if (!ValidationUtils.isNotEmpty(name)) return null;
        if (!ValidationUtils.isValidEmail(email)) return null;
        if (!ValidationUtils.isValidPassword(password)) return null;
        if (!ValidationUtils.isValidPhone(phone)) return null;
        if (UserFileHandler.emailExists(email)) return null;
        String id = IDGenerator.generateUserId();
        Customer customer = new Customer(
                id,
                ValidationUtils.sanitize(name),
                email.trim().toLowerCase(),
                password,
                ValidationUtils.sanitize(phone),
                0
        );
        boolean saved = UserFileHandler.appendUser(customer);
        return saved ? customer : null;
    }

    public Receptionist addReceptionist(String name, String email,
                                        String password, String shift) {
        if (!ValidationUtils.isNotEmpty(name)) return null;
        if (!ValidationUtils.isValidEmail(email)) return null;
        if (!ValidationUtils.isValidPassword(password)) return null;
        if (UserFileHandler.emailExists(email)) return null;
        String validShift = isValidShift(shift) ? shift.toUpperCase() : "MORNING";
        String id = IDGenerator.generateUserId();
        Receptionist receptionist = new Receptionist(
                id,
                ValidationUtils.sanitize(name),
                email.trim().toLowerCase(),
                password,
                validShift
        );
        boolean saved = UserFileHandler.appendUser(receptionist);
        return saved ? receptionist : null;
    }

    public Admin addAdmin(String name, String email,
                          String password, String level) {
        if (!ValidationUtils.isNotEmpty(name)) return null;
        if (!ValidationUtils.isValidEmail(email)) return null;
        if (!ValidationUtils.isValidPassword(password)) return null;
        if (UserFileHandler.emailExists(email)) return null;
        String validLevel = isValidAdminLevel(level) ? level.toUpperCase() : "REGULAR";
        String id = IDGenerator.generateUserId();
        Admin admin = new Admin(
                id,
                ValidationUtils.sanitize(name),
                email.trim().toLowerCase(),
                password,
                validLevel
        );
        boolean saved = UserFileHandler.appendUser(admin);
        return saved ? admin : null;
    }

    public List<User> getAllUsers() {
        return UserFileHandler.loadAllUsers();
    }

    public List<User> getAllCustomers() {
        List<User> result = new ArrayList<>();
        for (User u : UserFileHandler.loadAllUsers()) {
            if ("CUSTOMER".equals(u.getRole())) result.add(u);
        }
        return result;
    }

    public List<User> getAllStaff() {
        List<User> result = new ArrayList<>();
        for (User u : UserFileHandler.loadAllUsers()) {
            if (!"CUSTOMER".equals(u.getRole())) result.add(u);
        }
        return result;
    }

    public User findById(String userId) {
        if (!ValidationUtils.isNotEmpty(userId)) return null;
        return UserFileHandler.findById(userId);
    }

    public User findByEmail(String email) {
        if (!ValidationUtils.isNotEmpty(email)) return null;
        return UserFileHandler.findByEmail(email);
    }

    public boolean updateName(User user, String newName) {
        if (user == null || !ValidationUtils.isNotEmpty(newName)) return false;
        user.setName(ValidationUtils.sanitize(newName));
        return UserFileHandler.updateUser(user);
    }

    public boolean updatePassword(User user, String newPassword) {
        if (user == null || !ValidationUtils.isValidPassword(newPassword)) return false;
        user.setPassword(newPassword);
        return UserFileHandler.updateUser(user);
    }
    public boolean resetPasswordByEmail(String email, String newPassword) {
        if (!ValidationUtils.isValidEmail(email)) return false;
        if (!ValidationUtils.isValidPassword(newPassword)) return false;

        User user = UserFileHandler.findByEmail(email.trim());
        if (user == null) return false;

        user.setPassword(newPassword);
        return UserFileHandler.updateUser(user);
    }
    public boolean updateCustomerPhone(Customer customer, String phone) {
        if (customer == null || !ValidationUtils.isValidPhone(phone)) return false;
        customer.setPhoneNumber(ValidationUtils.sanitize(phone));
        return UserFileHandler.updateUser(customer);
    }

    public boolean updateProfile(User user, String newName, String newPassword) {
        if (user == null) return false;
        boolean updated = false;
        if (ValidationUtils.isNotEmpty(newName)) {
            user.setName(ValidationUtils.sanitize(newName));
            updated = true;
        }
        if (ValidationUtils.isValidPassword(newPassword)) {
            user.setPassword(newPassword);
            updated = true;
        }
        if (!updated) return false;
        return UserFileHandler.updateUser(user);
    }

    public boolean deleteUser(String userId) {
        if (!ValidationUtils.isNotEmpty(userId)) return false;
        return UserFileHandler.deleteUser(userId);
    }

    public boolean addLoyaltyPoints(String customerId, int points) {
        if (!ValidationUtils.isNotEmpty(customerId) || points <= 0) return false;
        User user = UserFileHandler.findById(customerId);
        if (!(user instanceof Customer)) return false;
        Customer customer = (Customer) user;
        customer.addLoyaltyPoints(points);
        return UserFileHandler.updateUser(customer);
    }


    public void seedDefaultAdmin() {
        if (!UserFileHandler.loadAllUsers().isEmpty()) return;
        Admin defaultAdmin = new Admin(
                "U001", "Super Admin",
                "heavenstayhotel2026@gmail.com",
                "admin123", "SUPER"
        );
        UserFileHandler.appendUser(defaultAdmin);
        System.out.println("[OK] Default admin: heavenstayhotel2026@gmail.com / admin123");
    }

    private boolean isValidShift(String shift) {
        if (shift == null) return false;
        switch (shift.toUpperCase()) {
            case "MORNING":
            case "EVENING":
            case "NIGHT":
                return true;
            default:
                return false;
        }
    }

    private boolean isValidAdminLevel(String level) {
        if (level == null) return false;
        switch (level.toUpperCase()) {
            case "SUPER":
            case "REGULAR":
                return true;
            default:
                return false;
        }
    }
}