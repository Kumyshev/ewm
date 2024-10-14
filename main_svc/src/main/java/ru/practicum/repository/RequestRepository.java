package ru.practicum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ru.practicum.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    List<Request> findByRequester_Id(Long requesterId);

    List<Request> findByEvent_Id(Long eventId);

    Request findByRequester_IdAndId(Long userId, Long Id);
}
