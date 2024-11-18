package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.impl.ICommentService;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final EntityManager entityManager;

    @Override
    public CommentDto saveCommentByUser(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(eventId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setUser(user);
        comment.setEvent(event);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentByUser(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(eventId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        Comment comment = commentRepository.findByUserAndEvent(user, event)
                .orElseThrow();
        commentMapper.toUpdate(newCommentDto, comment);
        comment.setCreatedOn(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentByUser(Long userId, Long eventId) {
        User user = userRepository.findById(eventId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        Comment comment = commentRepository.findByUserAndEvent(user, event)
                .orElseThrow();
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto findCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow();
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> findCommentsByAdmin(String text, Integer from, Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> query = criteriaBuilder.createQuery(Comment.class);
        Predicate criteria = criteriaBuilder.conjunction();
        Root<Comment> root = query.from(Comment.class);

        if (text != null) {
            Predicate inText = criteriaBuilder.like(criteriaBuilder.lower(root.get("text")),
                    "%" + text.toLowerCase() + "%");
            criteria = criteriaBuilder.and(criteria, inText);
        }
        query.select(root).where(criteria);
        return entityManager.createQuery(query).setFirstResult(from).setMaxResults(size)
                .getResultList().stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
