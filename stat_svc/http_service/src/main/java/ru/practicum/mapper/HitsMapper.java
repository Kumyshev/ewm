package ru.practicum.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.properties.StatSvcProperties;

@Component
public class HitsMapper {

    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(LocalDateTime.parse(dto.getTimestamp(), StatSvcProperties.DATE_TIME_FORMATTER))
                .build();
    }

    public EndpointHitDto toDto(EndpointHit hit) {
        return EndpointHitDto.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp().toString())
                .build();
    }
}
