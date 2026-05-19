package com.heavenstay.models;

import com.heavenstay.utils.DateUtils;
import java.time.LocalDate;

public class Booking {

    // ── Status constants ──────────────────────────────────────────────────────
    public static final String STATUS_PENDING   = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // ── Fields ────────────────────────────────────────────────────────────────
    private String    bookingId;
    private String    customerId;
    private String    roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int       nights;
    private double    totalPrice;
    private String    status;
    private LocalDate createdDate;

    // ── Constructors ──────────────────────────────────────────────────────────
    public Booking() {}

    public Booking(String bookingId,
                   String customerId,
                   String roomNumber,
                   LocalDate checkIn,
                   LocalDate checkOut,
                   double totalPrice,
                   String status) {
        this.bookingId   = bookingId;
        this.customerId  = customerId;
        this.roomNumber  = roomNumber;
        this.checkIn     = checkIn;
        this.checkOut    = checkOut;
        this.totalPrice  = totalPrice;
        this.status      = status;
    }

    public Booking(String bookingId,
                   String customerId,
                   String roomNumber,
                   String checkInStr,
                   String checkOutStr,
                   int nights,
                   double totalPrice,
                   String status,
                   String createdDateStr) {
        this.bookingId   = bookingId;
        this.customerId  = customerId;
        this.roomNumber  = roomNumber;
        this.checkIn     = DateUtils.parse(checkInStr);
        this.checkOut    = DateUtils.parse(checkOutStr);
        this.nights      = nights;
        this.totalPrice  = totalPrice;
        this.status      = status;
        this.createdDate = DateUtils.parse(createdDateStr);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String    getBookingId()  { return bookingId;  }
    public String    getCustomerId() { return customerId; }
    public String    getRoomNumber() { return roomNumber; }
    public LocalDate getCheckIn()    { return checkIn;    }
    public LocalDate getCheckOut()   { return checkOut;   }
    public int       getNights()     { return nights;     }
    public double    getTotalPrice() { return totalPrice; }
    public String    getStatus()     { return status;     }
    public LocalDate getCreatedDate() { return createdDate; }

    public String getCheckInDate()  { return DateUtils.format(checkIn); }
    public String getCheckOutDate() { return DateUtils.format(checkOut); }
    public String getCreatedDateStr() { return DateUtils.format(createdDate); }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setBookingId(String bookingId)    { this.bookingId  = bookingId;  }
    public void setCustomerId(String customerId)  { this.customerId = customerId; }
    public void setRoomNumber(String roomNumber)  { this.roomNumber = roomNumber; }
    public void setCheckIn(LocalDate checkIn)     { this.checkIn    = checkIn;    }
    public void setCheckOut(LocalDate checkOut)   { this.checkOut   = checkOut;   }
    public void setNights(int nights)             { this.nights     = nights;     }
    public void setTotalPrice(double totalPrice)  { this.totalPrice = totalPrice; }
    public void setStatus(String status)          { this.status     = status;     }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    // ── Helpers ───────────────────────────────────────────────────────────────
    public boolean isConfirmed() { return STATUS_CONFIRMED.equals(status); }
    public boolean isCompleted() { return STATUS_COMPLETED.equals(status); }
    public boolean isCancelled() { return STATUS_CANCELLED.equals(status); }
    public boolean isPending()   { return STATUS_PENDING.equals(status);   }

    public String toFileString() {
        return bookingId + "|" + customerId + "|" + roomNumber + "|"
                + getCheckInDate() + "|" + getCheckOutDate() + "|"
                + nights + "|" + totalPrice + "|" + status + "|"
                + getCreatedDateStr();
    }

    public static Booking fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length < 9) throw new IllegalArgumentException("Invalid booking line: " + line);
        return new Booking(
                p[0],
                p[1],
                p[2],
                p[3],
                p[4],
                Integer.parseInt(p[5]),
                Double.parseDouble(p[6]),
                p[7],
                p[8]
        );
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='"  + bookingId  + '\'' +
                ", customerId='" + customerId + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", checkIn="     + checkIn    +
                ", checkOut="    + checkOut   +
                ", nights="      + nights     +
                ", totalPrice="  + totalPrice +
                ", status='"     + status     + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}