package com.rentops.ai.intent;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a normalized booking intent parsed from user free-form text.
 */
public record BookingIntent(
        BookingAction action,
        String pickupLocation,
        String dropoffLocation,
        LocalDate pickupDate,
        LocalDate dropoffDate,
        Integer passengers,
        String vehicleType,
        String rawText,
        boolean usedLlm
        ) {

    public static BookingIntent empty(String raw) {
        return new BookingIntent(BookingAction.UNKNOWN, null, null, null, null, null, null, raw, false);
    }

    public Optional<LocalDate> pickupDateOpt() {
        return Optional.ofNullable(pickupDate);
    }

    public Optional<LocalDate> dropoffDateOpt() {
        return Optional.ofNullable(dropoffDate);
    }

    public Optional<Integer> passengersOpt() {
        return Optional.ofNullable(passengers);
    }
}
