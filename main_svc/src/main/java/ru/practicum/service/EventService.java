package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
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
import ru.practicum.enums.RequestStatus;
import ru.practicum.enums.UpdateEventAdminState;
import ru.practicum.enums.UpdateEventUserState;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.impl.IEventService;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.properties.StatSvcProperties;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.specification.EventSpecification;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    private final HttpClientService clientService;

    @Value(value = "${app.name}")
    private String appName;

    @Override
    public List<EventShortDto> findEventsByUser(Long userId, Integer from, Integer size) {
        return eventRepository.findByInitiator_Id(userId, PageRequest.of(from, size)).stream()
                .map(eventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto saveEventByUser(Long userId, NewEventDto newEventDto) {
        Long catId = newEventDto.getCategory();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        LocalDateTime eventDate = newEventDto.getEventDate();

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("");
        }

        Event event = eventMapper.toEvent(newEventDto);

        event.setCategory(category);
        event.setInitiator(user);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findEventByUser(Long userId, Long eventId) {
        return eventMapper.toEventFullDto(eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found")));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("");
        }
        LocalDateTime eventDate = event.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("");
        }
        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("");
        }
        Category category = null;
        if (updateEventUserRequest.getCategory() != null) {
            Long catId = updateEventUserRequest.getCategory();
            category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        }

        if (updateEventUserRequest.getStateAction() == null) {
            eventMapper.toUpdate(updateEventUserRequest, event);
            if (category != null)
                event.setCategory(category);
        } else {
            if (event.getState().equals(EventState.PENDING)
                    && updateEventUserRequest.getStateAction().equals(UpdateEventUserState.CANCEL_REVIEW)) {
                eventMapper.toUpdate(updateEventUserRequest, event);
                if (category != null)
                    event.setCategory(category);
                event.setState(EventState.CANCELED);
            }
            if ((event.getState().equals(EventState.CANCELED))
                    && updateEventUserRequest.getStateAction().equals(UpdateEventUserState.SEND_TO_REVIEW)) {
                eventMapper.toUpdate(updateEventUserRequest, event);
                if (category != null)
                    event.setCategory(category);
                event.setState(EventState.PENDING);
            }
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> findRequestsByUser(Long userId, Long eventId) {
        eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return requestRepository.findByEvent_Id(eventId).stream()
                .map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestByUser(Long userId, Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        Integer confirmed = event.getConfirmedRequests();
        Integer limit = event.getParticipantLimit();
        List<Request> requests = requestRepository.findByIdInAndStatus(eventRequestStatusUpdateRequest.getRequestIds(),
                RequestStatus.PENDING);

        if (requests.isEmpty() || limit.equals(confirmed))
            throw new BadRequestException("");

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            if (confirmed == null)
                confirmed = 0;
            if (!confirmed.equals(limit)
                    && eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {

                requests.get(i).setStatus(RequestStatus.CONFIRMED);
                ParticipationRequestDto requestDto = requestMapper.toParticipationRequestDto(requests.get(i));
                confirmedRequests.add(requestDto);
                event.setConfirmedRequests(++confirmed);
            } else if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)
                    || confirmed.equals(limit)) {

                requests.get(i).setStatus(RequestStatus.REJECTED);
                ParticipationRequestDto requestDto = requestMapper.toParticipationRequestDto(requests.get(i));
                rejectedRequests.add(requestDto);
            }
            requestRepository.save(requests.get(i));
            eventRepository.save(event);
        }
        eventRequestStatusUpdateResult.setConfirmedRequests(confirmedRequests);
        eventRequestStatusUpdateResult.setRejectedRequests(rejectedRequests);
        return eventRequestStatusUpdateResult;
    }

    @Override
    public List<EventFullDto> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        if ((rangeEnd != null && rangeStart != null) && rangeEnd.isBefore(rangeStart))
            throw new ForbiddenException("");

        Specification<Event> specification = Specification.where(EventSpecification.inUsers(users))
                .and(EventSpecification.inStates(states))
                .and(EventSpecification.inCategories(categories))
                .and(EventSpecification.isAfter(rangeStart))
                .and(EventSpecification.isBefore(rangeEnd));

        return eventRepository.findAll(specification, PageRequest.of(from, size)).stream()
                .map(eventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        LocalDateTime eventDate = event.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenException("The participant limit has been reached");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadRequestException("Request must have status PENDING");
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            eventMapper.toUpdate(updateEventAdminRequest, event);
            if (updateEventAdminRequest.getStateAction().equals(UpdateEventAdminState.PUBLISH_EVENT)) {
                LocalDateTime publishedOn = LocalDateTime.now();
                event.setPublishedOn(publishedOn);
                event.setState(EventState.PUBLISHED);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> findEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
            LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortWay sort, Integer from, Integer size,
            HttpServletRequest request) {

        if ((rangeEnd != null && rangeStart != null) && rangeEnd.isBefore(rangeStart))
            throw new ForbiddenException("");

        Specification<Event> specification = Specification
                .where(EventSpecification.inStates(Arrays.asList(EventState.PUBLISHED)))
                .and(EventSpecification.inAnnotationOrDescription(text))
                .and(EventSpecification.inCategories(categories))
                .and(EventSpecification.isPaid(paid))
                .and((EventSpecification.isAfter(rangeStart).and(EventSpecification.isBefore(rangeEnd))
                        .or(EventSpecification.isAfterCurrentDate())))
                .and(EventSpecification.isOnlyAvailable(onlyAvailable));

        List<EventShortDto> list = eventRepository.findAll(specification, PageRequest.of(from, size)).stream()
                .map(eventMapper::toEventShortDto).collect(Collectors.toList());
        List<Event> events = eventRepository.findAll(specification, PageRequest.of(from, size)).stream()
                .collect(Collectors.toList());

        LocalDateTime start = LocalDateTime.now().minusYears(10);
        LocalDateTime end = LocalDateTime.now();
        String uri = request.getRequestURI();
        List<String> uris = List.of(uri);
        @SuppressWarnings("unchecked")
        List<ViewStatsDto> views = (List<ViewStatsDto>) clientService.getStats(start, end, uris, false).getBody();

        for (Event event : events) {
            event.setViews(views.size());

            EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                    .app(appName)
                    .uri(uri)
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now().format(StatSvcProperties.DATE_TIME_FORMATTER)).build();

            clientService.postHit(endpointHitDto);
        }

        if (sort != null) {
            if (sort.equals(EventSortWay.EVENT_DATE)) {
                list.stream().sorted((e1, e2) -> e1.getEventDate().compareTo(e2.getEventDate()))
                        .collect(Collectors.toList());
                return list;
            } else {
                list.stream().sorted((e1, e2) -> e1.getViews().compareTo(e2.getViews()))
                        .collect(Collectors.toList());
                return list;
            }
        }

        return list;
    }

    @Override
    public EventFullDto findEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        LocalDateTime start = LocalDateTime.now().minusYears(10);
        LocalDateTime end = LocalDateTime.now();
        String uri = request.getRequestURI();
        List<String> uris = List.of(uri);
        @SuppressWarnings("unchecked")
        List<ViewStatsDto> views = (List<ViewStatsDto>) clientService.getStats(start, end, uris, false).getBody();
        event.setViews(views.size());

        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(uri)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(StatSvcProperties.DATE_TIME_FORMATTER)).build();

        clientService.postHit(endpointHitDto);

        return eventMapper.toEventFullDto(event);
    }

}
