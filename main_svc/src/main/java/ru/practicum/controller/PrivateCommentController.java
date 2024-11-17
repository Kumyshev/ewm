package ru.practicum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.impl.ICommentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping(value = "/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final ICommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto saveComment(@PathVariable Long userId,
            @RequestParam(name = "eventId") Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.saveCommentByUser(userId, eventId, newCommentDto);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long userId,
            @RequestParam(name = "eventId") Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.updateCommentByUser(userId, eventId, newCommentDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
            @RequestParam(name = "eventId") Long eventId) {
        commentService.deleteCommentByUser(userId, eventId);
    }
}
