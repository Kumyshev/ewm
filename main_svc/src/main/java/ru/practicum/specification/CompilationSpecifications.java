package ru.practicum.specification;

import org.springframework.data.jpa.domain.Specification;

import ru.practicum.model.Compilation;

public class CompilationSpecifications {

    public static Specification<Compilation> hasPinned(Boolean pinned) {
        return (root, query, cb) -> cb.equal(root.get("pinned"), pinned);
    }
}
