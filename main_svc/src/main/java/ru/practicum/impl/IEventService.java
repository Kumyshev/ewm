package ru.practicum.impl;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.enums.EventSortWay;
import ru.practicum.enums.EventState;

public interface IEventService {

        List<EventShortDto> findEventsByUser(Long userId, Integer from, Integer size);

        EventFullDto saveEventByUser(Long userId, NewEventDto newEventDto);

        EventFullDto findEventByUser(Long userId, Long eventId);

        EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

        List<ParticipationRequestDto> findRequestsByUser(Long userId, Long eventId);

        EventRequestStatusUpdateResult updateRequestByUser(Long userId, Long eventId,
                        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

        List<EventFullDto> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

        EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

        List<EventShortDto> findEvents(String text, List<Long> categories, Boolean paid,
                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                        EventSortWay sort, Integer from, Integer size, HttpServletRequest request);

        EventFullDto findEvent(Long eventId, HttpServletRequest request);
}
