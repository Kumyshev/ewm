package ru.practicum.dto;

import java.util.List;

import ru.practicum.enums.RequestStatus;
import lombok.Data;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
