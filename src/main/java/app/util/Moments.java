package app.util;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public interface Moments {

    default long epochMilli() {
        return System.currentTimeMillis();
    }

    default Date date() {
        return new Date();
    }

    default Timestamp timestamp() {
        return new Timestamp(epochMilli());
    }

    default Instant instant() {
        return Instant.now();
    }

    default Instant instant(Clock clock) {
        return Instant.now(clock);
    }

    default LocalDate localDate() {
        return LocalDate.now();
    }

    default LocalDate localDate(Clock clock) {
        return LocalDate.now(clock);
    }

    default LocalDate localDate(ZoneId zoneId) {
        return LocalDate.now(zoneId);
    }

    default LocalTime localTime() {
        return LocalTime.now();
    }

    default LocalTime localTime(Clock clock) {
        return LocalTime.now(clock);
    }

    default LocalTime localTime(ZoneId zoneId) {
        return LocalTime.now(zoneId);
    }

    default LocalDateTime localDateTime() {
        return LocalDateTime.now();
    }

    default LocalDateTime localDateTime(Clock clock) {
        return LocalDateTime.now(clock);
    }

    default LocalDateTime localDateTime(ZoneId zoneId) {
        return LocalDateTime.now(zoneId);
    }

}
