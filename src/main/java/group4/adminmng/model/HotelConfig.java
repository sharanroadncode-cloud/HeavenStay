package group4.adminmng.model;

// ================================================================
// File    : HotelConfig.java
// Package : com.heavenstay.model
// Desc    : Hotel-wide system configuration. Loaded from and saved
//           to hotel_config.txt as a single pipe-delimited record.
//
// hotel_config.txt format (single line):
//   hotelName|checkInTime|checkOutTime|taxRate|cancellationPolicy|currency|maxFloors
// ================================================================

public class HotelConfig {

    private String hotelName;
    private String checkInTime;
    private String checkOutTime;
    private double taxRate;
    private String cancellationPolicy;
    private String currency;
    private int    maxFloors;

    // ── Constructor ──────────────────────────────────────────
    public HotelConfig(String hotelName, String checkInTime, String checkOutTime,
                       double taxRate, String cancellationPolicy,
                       String currency, int maxFloors) {
        this.hotelName           = hotelName;
        this.checkInTime         = checkInTime;
        this.checkOutTime        = checkOutTime;
        this.taxRate             = taxRate;
        this.cancellationPolicy  = cancellationPolicy;
        this.currency            = currency;
        this.maxFloors           = maxFloors;
    }

    /** Default config for first-time setup. */
    public HotelConfig() {
        this.hotelName          = "Heaven Stay";
        this.checkInTime        = "14:00";
        this.checkOutTime       = "12:00";
        this.taxRate            = 10.0;
        this.cancellationPolicy = "Free cancellation up to 24 hours before check-in.";
        this.currency           = "LKR";
        this.maxFloors          = 10;
    }

    // ── Validation ────────────────────────────────────────────
    public void validate() {
        validateHotelName();
        validateTaxRate();
        validateMaxFloors();
        validateTimeFormat(checkInTime, "Check-in");
        validateTimeFormat(checkOutTime, "Check-out");
    }

    private void validateHotelName() {
        if (hotelName == null || hotelName.isBlank())
            throw new IllegalArgumentException("Hotel name cannot be empty.");
    }

    private void validateTaxRate() {
        if (taxRate < 0 || taxRate > 100)
            throw new IllegalArgumentException("Tax rate must be between 0 and 100.");
    }

    private void validateMaxFloors() {
        if (maxFloors < 1 || maxFloors > 100)
            throw new IllegalArgumentException("Max floors must be between 1 and 100.");
    }

    private void validateTimeFormat(String time, String label) {
        if (time == null || !time.matches("\\d{2}:\\d{2}"))
            throw new IllegalArgumentException(label + " time must be in HH:mm format.");
    }

    // ── Serialisation ────────────────────────────────────────
    @Override
    public String toString() {
        return hotelName + "|" + checkInTime + "|" + checkOutTime + "|"
                + String.format("%.2f", taxRate) + "|"
                + (cancellationPolicy != null ? cancellationPolicy : "-") + "|"
                + (currency != null ? currency : "LKR") + "|" + maxFloors;
    }

    public static HotelConfig fromString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) return new HotelConfig();
        return new HotelConfig(
                p[0], p[1], p[2],
                Double.parseDouble(p[3]),
                p[4].equals("-") ? "" : p[4],
                p.length > 5 ? p[5] : "LKR",
                p.length > 6 ? Integer.parseInt(p[6]) : 10
        );
    }

    // ── Getters & Setters ────────────────────────────────────
    public String getHotelName()                         { return hotelName; }
    public void   setHotelName(String v)                 { hotelName = v; }
    public String getCheckInTime()                       { return checkInTime; }
    public void   setCheckInTime(String v)               { checkInTime = v; }
    public String getCheckOutTime()                      { return checkOutTime; }
    public void   setCheckOutTime(String v)              { checkOutTime = v; }
    public double getTaxRate()                           { return taxRate; }
    public void   setTaxRate(double v)                   { taxRate = v; }
    public String getCancellationPolicy()                { return cancellationPolicy; }
    public void   setCancellationPolicy(String v)        { cancellationPolicy = v; }
    public String getCurrency()                          { return currency; }
    public void   setCurrency(String v)                  { currency = v; }
    public int    getMaxFloors()                         { return maxFloors; }
    public void   setMaxFloors(int v)                    { maxFloors = v; }
}
