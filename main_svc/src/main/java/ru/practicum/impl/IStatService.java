package ru.practicum.impl;

import jakarta.servlet.http.HttpServletRequest;

public interface IStatService {

    void postHit(HttpServletRequest httpServletRequest);
}
