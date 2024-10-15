package ru.practicum.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.practicum.dto.ApiError;
import ru.practicum.etc.TemplateSettings;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ApiError handleBadRequest(RuntimeException ex) {
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        List<String> errors = new ArrayList<>();
        errors.add(stacktrace);
        return ApiError.builder()
                .errors(errors)
                .message(ex.getMessage())
                .reason("Incorrectly made request.")
                .status("BAD_REQUEST")
                .timestamp(LocalDateTime.now().format(TemplateSettings.DATE_FORMATTER))
                .build();
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ApiError handleNotFound(RuntimeException ex) {
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        List<String> errors = new ArrayList<>();
        errors.add(stacktrace);
        return ApiError.builder()
                .errors(errors)
                .message(ex.getMessage())
                .reason("The required object was not found.")
                .status("NOT_FOUND")
                .timestamp(LocalDateTime.now().format(TemplateSettings.DATE_FORMATTER))
                .build();
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ApiError handleForbidden(RuntimeException ex) {
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        List<String> errors = new ArrayList<>();
        errors.add(stacktrace);
        return ApiError.builder()
                .errors(errors)
                .message(ex.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .status("FORBIDDEN")
                .timestamp(LocalDateTime.now().format(TemplateSettings.DATE_FORMATTER))
                .build();
    }

    @ExceptionHandler(value = ConflictException.class)
    public ApiError handleConflict(RuntimeException ex) {
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        List<String> errors = new ArrayList<>();
        errors.add(stacktrace);
        return ApiError.builder()
                .errors(errors)
                .message(ex.getMessage())
                .reason("Integrity constraint has been violated.")
                .status("CONFLICT")
                .timestamp(LocalDateTime.now().format(TemplateSettings.DATE_FORMATTER))
                .build();
    }
}
