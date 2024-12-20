package ru.practicum.etc;

import java.time.format.DateTimeFormatter;

public abstract class TemplateSettings {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
}
