package ru.practicum.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.service.MainService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping
    public void get(HttpServletRequest httpServletRequest) {
        mainService.postHit(httpServletRequest);
    }

}
