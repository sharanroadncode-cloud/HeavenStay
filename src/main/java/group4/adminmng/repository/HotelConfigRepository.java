package group4.adminmng.repository;

// ================================================================
// File    : HotelConfigRepository.java
// Package : com.heavenstay.repository
// Desc    : Reads and writes the single-line hotel_config.txt.
//
// hotel_config.txt format (single line):
//   hotelName|checkInTime|checkOutTime|taxRate|cancellationPolicy|currency|maxFloors
// ================================================================

import group4.adminmng.model.HotelConfig;
import group4.adminmng.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class HotelConfigRepository {

    private static final String FILE = "data/hotel_config.txt";

    /** Load config from file; returns defaults if file is missing. */
    public HotelConfig load() {
        try {
            String line = FileUtil.readSingleLine(FILE);
            return (line != null) ? HotelConfig.fromString(line) : new HotelConfig();
        } catch (IOException e) {
            return new HotelConfig();
        }
    }

    /** Validate and overwrite the config file with one line. */
    public void save(HotelConfig config) {
        config.validate();
        try { FileUtil.writeSingleLine(FILE, config.toString()); }
        catch (IOException e) { throw new RuntimeException("Cannot save config: " + e.getMessage(), e); }
    }
}
