package ru.practicum.impl;

import java.util.List;

import ru.practicum.dto.ParticipationRequestDto;

public interface IRequestService {

    List<ParticipationRequestDto> findRequestsByUser(Long userId);

    ParticipationRequestDto saveRequestByUser(Long userId, Long eventId);

    ParticipationRequestDto updateRequestByUser(Long userId, Long requestId);
}
