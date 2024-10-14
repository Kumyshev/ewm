package ru.practicum.etc;

import java.time.format.DateTimeFormatter;

public abstract class TemplateSettings {

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
}
