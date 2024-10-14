package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.EndpointHitDto;
import ru.practicum.impl.BaseClient;
import ru.practicum.properties.StatSvcProperties;

@Service
public class HttpClientService extends BaseClient {
    public HttpClientService(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build());
    }

    public ResponseEntity<Object> postHit(EndpointHitDto dto) {
        return post(StatSvcProperties.API_PREFIX_HIT, dto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris,
            @Nullable Boolean unique) {
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
        return get(StatSvcProperties.API_PREFIX_STATS + path, params);
    }
}
