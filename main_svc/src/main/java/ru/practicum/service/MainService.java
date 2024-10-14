package ru.practicum.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainService {
    
    private final StatService statService;

    public void postHit(HttpServletRequest httpServletRequest){
        statService.postHit(httpServletRequest);
    }
}
