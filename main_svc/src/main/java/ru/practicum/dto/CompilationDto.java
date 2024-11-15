package ru.practicum.dto;

import java.util.List;

import lombok.Data;

@Data
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
