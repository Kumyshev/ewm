package ru.practicum.controller.open;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.enums.AvailableValues;
import ru.practicum.impl.IEventService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/events")
@RequiredArgsConstructor
public class EventPublicController {

    private final IEventService eventService;

    @GetMapping
    public List<EventShortDto> findEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) AvailableValues sort,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            HttpServletRequest httpServletRequest) {
        return eventService.findEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                httpServletRequest);
    }

    @GetMapping("/{id}")
    public EventFullDto findEvent(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        return eventService.findEvent(id, httpServletRequest);
    }

}
