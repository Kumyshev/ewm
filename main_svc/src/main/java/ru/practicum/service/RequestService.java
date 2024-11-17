package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.BadRequestException;
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
        public List<ParticipationRequestDto> findRequestsByUser(Long userId) {
                return requestRepository.findAllByRequester_Id(userId)
                                .stream().map(requestMapper::toParticipationRequestDto)
                                .collect(Collectors.toList());
        }

        @Override
        public ParticipationRequestDto saveRequestByUser(Long userId, Long eventId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Event with id=" + eventId + " was not found"));
                Long initiatorId = event.getInitiator().getId();
                if (initiatorId.equals(userId) || !event.getState().equals(EventState.PUBLISHED)
                                || (event.getParticipantLimit() != 0
                                                && event.getParticipantLimit().equals(event.getConfirmedRequests())))
                        throw new BadRequestException(
                                        "Failed to convert value of type java.lang.String to required type long; nested exception is java.lang.NumberFormatException: For input string: ad");

                Request request = new Request();
                request.setEvent(event);
                request.setRequester(user);
                int number = event.getConfirmedRequests();
                if (event.getRequestModeration() == false || event.getParticipantLimit() == 0) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        event.setConfirmedRequests(++number);
                }
                request.setCreated(LocalDateTime.now());
                return requestMapper.toParticipationRequestDto(requestRepository.save(request));
        }

        @Override
        public ParticipationRequestDto updateRequestByUser(Long userId, Long requestId) {
                Request request = requestRepository.findByRequester_IdAndId(userId, requestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Request with id=" + requestId + " was not found"));
                request.setStatus(RequestStatus.CANCELED);
                return requestMapper.toParticipationRequestDto(requestRepository.save(request));
        }
}
