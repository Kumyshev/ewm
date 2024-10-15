package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.EndpointHitDto;
import ru.practicum.etc.TemplateSettings;
import ru.practicum.impl.IStatService;
import ru.practicum.properties.StatSvcProperties;

@Service
@RequiredArgsConstructor
public class StatService implements IStatService {

    private static final String URL = "http://127.0.0.1:9090";
    private final RestTemplate restTemplate;

    @Override
    public void postHit(HttpServletRequest httpServletRequest) {

        EndpointHitDto dto = EndpointHitDto.builder()
                .app("main")
                .ip(httpServletRequest.getLocalAddr())
                .uri(httpServletRequest.getRequestURI())
                .timestamp(LocalDateTime.now().format(TemplateSettings.DATE_FORMATTER))
                .build();

        restTemplate.postForEntity(URL + StatSvcProperties.API_PREFIX_HIT, dto, EndpointHitDto.class);

    }

    @Override
    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder path = new StringBuilder("?start={start}&end={end}");
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        if (uris != null) {
            path.append("&uris={uris}");
            params.put("uris", String.join(",", uris));
        }
        if (unique != null) {
            path.append("&unique={unique}");
            params.put("unique", unique);
        }

        return restTemplate.getForEntity(URL + StatSvcProperties.API_PREFIX_STATS + path, Object.class, params);
    }

}
