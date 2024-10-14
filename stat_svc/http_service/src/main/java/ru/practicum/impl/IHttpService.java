package ru.practicum.impl;

import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

public interface IHttpService {

    EndpointHitDto add(EndpointHitDto dto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
