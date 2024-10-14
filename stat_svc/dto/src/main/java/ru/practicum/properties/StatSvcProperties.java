package ru.practicum.properties;

import java.time.format.DateTimeFormatter;

public abstract class StatSvcProperties {
    public static final String API_PREFIX_HIT = "/hit";
    public static final String API_PREFIX_STATS = "/stats";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
