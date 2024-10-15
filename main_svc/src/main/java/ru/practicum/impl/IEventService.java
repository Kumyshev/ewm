package ru.practicum.impl;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.enums.AvailableValues;

public interface IEventService {

        List<EventShortDto> findEvents(Long userId, Integer from, Integer size);

        EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

        EventFullDto findEvent(Long userId, Long eventId);

        EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

        List<EventFullDto> findEvents(List<Long> usres, List<String> states, List<Long> categories,
                        String rangeStart, String rangeEnd, Integer from, Integer size);

        EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

        List<EventShortDto> findEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                        String rangeEnd, Boolean onlyAvailable, AvailableValues sort, Integer from, Integer size,
                        HttpServletRequest httpServletRequest);

        EventFullDto findEvent(Long id, HttpServletRequest httpServletRequest);
}
