package ru.practicum.controller.closed;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.impl.IRequestService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {

    private final IRequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> findRequests(@PathVariable Long userId) {
        return requestService.findRequests(userId);
    }

    @PostMapping
    public ParticipationRequestDto saveRequest(
            @PathVariable Long userId,
            @RequestParam(name = "eventId") Long eventId,
            HttpServletRequest httpServletRequest) {
        return requestService.saveRequest(userId, eventId, httpServletRequest);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        return requestService.updateRequest(userId, requestId);
    }
}
