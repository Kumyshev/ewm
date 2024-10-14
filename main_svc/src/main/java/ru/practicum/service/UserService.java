package ru.practicum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.impl.IUserService;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.specification.UserSpecifications;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        List<UserDto> userDtos = new ArrayList<>();
        Specification<User> specification = Specification.where(UserSpecifications.hasIds(ids));
        if (ids != null)
            userDtos.addAll(userRepository.findAll(specification).stream().map(userMapper::toUserDto).toList());
        else
            userDtos.addAll(
                    userRepository.findAll(PageRequest.of(from, size)).stream().map(userMapper::toUserDto).toList());
        return userDtos;
    }

    @Override
    public UserDto saveUser(NewUserRequest newUserRequest) {
        User user = userMapper.toUser(newUserRequest);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.delete(user);
    }

}
