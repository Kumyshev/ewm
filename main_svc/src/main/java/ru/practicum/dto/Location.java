package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Location {
    private Float lat;
    private Float lon;
}
