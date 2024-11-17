package ru.practicum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    List<Request> findAllByRequester_Id(Long userId);

    List<Request> findByEvent_Id(Long eventId);

    Optional<Request> findByRequester_IdAndId(Long userId, Long requesterId);

    List<Request> findByIdInAndStatus(List<Long> ids, RequestStatus status);
}
