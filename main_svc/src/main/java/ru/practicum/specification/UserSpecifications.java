package ru.practicum.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import ru.practicum.model.User;

public class UserSpecifications {

    public static Specification<User> hasIds(List<Long> ids) {
        return (root, query, cb) -> root.get("id").in(ids);
    }
}
