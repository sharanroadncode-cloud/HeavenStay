package group4.boooking_payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelRoomReservationSystemApplication {

    private static final Logger logger = LoggerFactory.getLogger(HotelRoomReservationSystemApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HotelRoomReservationSystemApplication.class, args);

        logger.info("╔══════════════════════════════════════════════╗");
        logger.info("║        Heaven Stay Hotel System              ║");
        logger.info("║  Server running → http://localhost:8080      ║");
        logger.info("║  No database needed — data stored in memory  ║");
        logger.info("╚══════════════════════════════════════════════╝");
    }

}
