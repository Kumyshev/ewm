package ru.practicum.impl;

import java.util.List;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;

public interface IUserService {

    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserDto saveUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
