package ru.practicum.impl;

import java.util.List;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;

public interface ICommentService {

    CommentDto saveCommentByUser(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateCommentByUser(Long userId, Long eventId, NewCommentDto newCommentDto);

    void deleteCommentByUser(Long userId, Long eventId);

    CommentDto findCommentByAdmin(Long commentId);

    List<CommentDto> findCommentsByAdmin(String text, Integer from, Integer size);

    void deleteCommentByAdmin(Long commentId);
}
