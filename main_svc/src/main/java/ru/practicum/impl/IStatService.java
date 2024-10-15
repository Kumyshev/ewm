package ru.practicum.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import jakarta.servlet.http.HttpServletRequest;

public interface IStatService {

    void postHit(HttpServletRequest httpServletRequest);

    ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris,
            @Nullable Boolean unique);
}
