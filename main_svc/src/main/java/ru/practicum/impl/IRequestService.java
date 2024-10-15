package ru.practicum.impl;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;

public interface IRequestService {

    List<ParticipationRequestDto> findRequests(Long userId);

    ParticipationRequestDto saveRequest(Long userId, Long eventId, HttpServletRequest httpServletRequest);

    ParticipationRequestDto updateRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
