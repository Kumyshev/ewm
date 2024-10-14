package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.enums.AvailableValues;
import ru.practicum.enums.State;
import ru.practicum.enums.StateAction;
import ru.practicum.etc.TemplateSettings;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.impl.IEventService;
import ru.practicum.impl.IStatService;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.specification.EventSpecifications;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final IStatService statService;

    @Override
    public List<EventShortDto> findEvents(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findByInitiator_Id(userId, PageRequest.of(from, size));
        return events.stream().map(eventMapper::toEventShortDto).toList();
    }

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Long catId = newEventDto.getCategory();
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), TemplateSettings.DATE_FORMATTER);

        if (!eventDate.isAfter(now.plusHours(2))) {
            throw new ForbiddenException(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + eventDate);
        }

        Event event = eventMapper.toEvent(newEventDto);
        event.setCreatedOn(now.format(TemplateSettings.DATE_FORMATTER));
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        event.setCategory(category);
        event.setInitiator(user);
        event.setState(State.PENDING);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " or userId=" + userId + " was not found"));

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " or userId=" + userId + " was not found"));

        Long catId = updateEventUserRequest.getCategory();
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDate = LocalDateTime.parse(updateEventUserRequest.getEventDate(),
                TemplateSettings.DATE_FORMATTER);

        if (!eventDate.isAfter(now.plusHours(2))) {
            throw new ForbiddenException(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + eventDate);
        }

        if (event.getState() == State.CANCELED || event.getState() == State.PENDING) {
            eventMapper.toUpdate(updateEventUserRequest, event);

            if (updateEventUserRequest.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            } else if (updateEventUserRequest.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(State.PUBLISHED);
            }

            event.setCategory(category);
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> findEvents(List<Long> users, List<String> states, List<Long> categories,
            String rangeStart, String rangeEnd, Integer from, Integer size) {

        Specification<Event> specification = EventSpecifications.hasUsers(users)
                .or(EventSpecifications.hasStates(states)
                        .or(EventSpecifications.hasCategories(categories))
                        .or(EventSpecifications.greaterThan(rangeStart))
                        .or(EventSpecifications.lessThan(rangeEnd)));
        return eventRepository.findAll(specification, PageRequest.of(from, size))
                .stream().map(eventMapper::toEventFullDto).toList();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " was not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newEventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(),
                TemplateSettings.DATE_FORMATTER);

        if (newEventDate != null && newEventDate.isAfter(now.plusHours(1))) {
            throw new ForbiddenException(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + newEventDate);
        }

        if (updateEventAdminRequest.getLocation() == null) {
            updateEventAdminRequest.getLocation().setLat(event.getLat());
            updateEventAdminRequest.getLocation().setLat(event.getLon());
        }

        if (event.getState() == State.PENDING
                && updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
            eventMapper.toUpdate(updateEventAdminRequest, event);
            event.setPublishedOn(TemplateSettings.DATE_FORMATTER.format(now));
            event.setState(State.PUBLISHED);
        } else if (event.getState() != State.PUBLISHED
                && updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT) {
            event.setState(State.CANCELED);
        }

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(event));
        if (eventFullDto.getLocation() == null) {
            eventFullDto.setLocation(updateEventAdminRequest.getLocation());
        }
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
            String rangeEnd, Boolean onlyAvailable, AvailableValues sort, Integer from, Integer size,
            HttpServletRequest httpServletRequest) {

        Specification<Event> specification = EventSpecifications.hasText(text)
                .or(EventSpecifications.hasCategories(categories))
                .or(EventSpecifications.hasPaid(paid))
                .or(EventSpecifications.greaterThan(rangeStart))
                .or(EventSpecifications.lessThan(rangeEnd))
                .or(EventSpecifications.hasOnlyAvailable(onlyAvailable));

        Page<Event> events = eventRepository.findAll(specification, PageRequest.of(from, size));

        if (sort != null) {
            if (sort == AvailableValues.EVENT_DATE) {
                return events.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .map(eventMapper::toEventShortDto).toList();
            } else {
                return events.stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .map(eventMapper::toEventShortDto).toList();
            }
        }

        statService.postHit(httpServletRequest);

        return events.stream().map(eventMapper::toEventShortDto).toList();
    }

    @Override
    public EventFullDto findEvent(Long id, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(id).orElseThrow();
        LocalDateTime start = LocalDateTime.now().minusYears(10);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of(httpServletRequest.getRequestURI());
        List<Object> views =(List<Object>) statService.getStats(start, end, uris, true).getBody();
        event.setViews(views.size());
        statService.postHit(httpServletRequest);

        return eventMapper.toEventFullDto(event);
    }

}
