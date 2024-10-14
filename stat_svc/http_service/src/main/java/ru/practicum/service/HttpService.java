package ru.practicum.service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.impl.IHttpService;
import ru.practicum.mapper.HitsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.HitsRepository;

@Service
@RequiredArgsConstructor
public class HttpService implements IHttpService {
    private final HitsRepository repository;
    private final HitsMapper mapper;

    @Override
    public EndpointHitDto add(EndpointHitDto dto) {
        EndpointHit hit = mapper.toEndpointHit(dto);
        return mapper.toDto(repository.save(hit));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris,
            @Nullable Boolean unique) {
        if (end.isBefore(start)) {
            throw new InvalidParameterException("Неверно указана дата!!!");
        }

        if (uris == null) {
            if (unique) {
                return repository.getUniqueStats(start, end);
            } else {
                return repository.getStats(start, end);
            }
        } else {
            if (unique) {
                return repository.getUniqueStatsByUris(start, end, uris);
            } else {
                return repository.getStatsByUris(start, end, uris);
            }
        }
    }

}
