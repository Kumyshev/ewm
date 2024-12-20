package ru.practicum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ru.practicum.enums.EventState;
import ru.practicum.model.Event;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByInitiator_Id(Long userId, Pageable pageable);

    Optional<Event> findByInitiator_IdAndId(Long userId, Long eventId);

    Optional<Event> findByIdAndState(Long eventId, EventState state);
}
