package ru.practicum.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
