package ru.practicum.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

public interface HitsRepository extends JpaRepository<EndpointHit, Long> {

        @Query("select new ru.practicum.ViewStatsDto(s.app, s.uri, count(distinct s.ip) as i) " +
                        "from hits s " +
                        "where s.timestamp between :start and :end " +
                        "group by s.app, s.uri " +
                        "order by i desc")
        List<ViewStatsDto> getUniqueStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query("select new ru.practicum.ViewStatsDto(s.app, s.uri, count(s.ip) as i) " +
                        "from hits s " +
                        "where s.timestamp between :start and :end " +
                        "group by s.app, s.uri " +
                        "order by i desc")
        List<ViewStatsDto> getStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query("select new ru.practicum.ViewStatsDto(s.app, s.uri, count(distinct s.ip) as i) " +
                        "from hits s " +
                        "where s.timestamp between :start and :end " +
                        "and s.uri in (:uris) " +
                        "group by s.app, s.uri " +
                        "order by i desc")
        List<ViewStatsDto> getUniqueStatsByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                        @Param("uris") List<String> uris);

        @Query("select new ru.practicum.ViewStatsDto(s.app, s.uri, count(s.ip) as i) " +
                        "from hits s " +
                        "where s.timestamp between :start and :end " +
                        "and s.uri in (:uris) " +
                        "group by s.app, s.uri " +
                        "order by i desc")
        List<ViewStatsDto> getStatsByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                        @Param("uris") List<String> uris);

}
