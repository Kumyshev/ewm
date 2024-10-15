package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.enums.State;
import ru.practicum.enums.Status;
import ru.practicum.etc.TemplateSettings;
import ru.practicum.exception.NotFoundException;
import ru.practicum.impl.IRequestService;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RequestService implements IRequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> findRequests(Long userId) {
        return requestRepository.findByRequester_Id(userId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto saveRequest(Long userId, Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " was not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<Request> requests = requestRepository.findByEvent_Id(eventId);

        if (user.equals(event.getInitiator()) || event.getState() != State.PUBLISHED
                || event.getParticipantLimit() == requests.size()) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        Request request = Request.builder()
                .created(TemplateSettings.DATE_FORMATTER.format(now))
                .event(event)
                .requester(user)
                .build();
        if (event.getRequestModeration() == false) {
            request.setStatus(Status.CONFIRMED.name());
        }

        request.setStatus(State.PENDING.name());

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {

        Request request = requestRepository.findByRequester_IdAndId(userId, requestId);

        request.setStatus(Status.REJECTED.name());

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId) {
        eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " or userId=" + userId + " was not found"));
        return requestRepository.findByEvent_Id(eventId).stream()
                .map(requestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " or userId=" + userId + " was not found"));

        List<Request> requests = requestRepository.findByEvent_Id(eventId)
                .stream().filter(r -> r.getStatus().equals(State.PENDING.name())).toList();

        if (event.getParticipantLimit() == 0 || event.getRequestModeration() == false) {
            return null;
        }

        List<Request> confirmedRequests = null;
        List<Request> rejectedRequests = null;
        if (event.getParticipantLimit() < requests.size()) {
            confirmedRequests = requests.subList(0, event.getParticipantLimit());
            rejectedRequests = requests.subList(event.getParticipantLimit() + 1, requests.size());
        } else {
            confirmedRequests = new ArrayList<>(requests);
            rejectedRequests = new ArrayList<>();
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream().map(requestMapper::toParticipationRequestDto).toList())
                .rejectedRequests(rejectedRequests.stream().map(requestMapper::toParticipationRequestDto).toList())
                .build();
    }

}
