package ru.practicum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    Optional<Comment> findByUserAndEvent(User user, Event event);
}
