package ru.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.impl.IStatService;

@Service
@RequiredArgsConstructor
public class StatService implements IStatService {

    private static final String URL = "http://127.0.0.1:9090";
    private final RestTemplate restTemplate;

    @Override
    public void postHit(HttpServletRequest httpServletRequest) {
    }
}
