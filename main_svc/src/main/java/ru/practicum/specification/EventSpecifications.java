package ru.practicum.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import ru.practicum.model.Event;

public class EventSpecifications {

    public static Specification<Event> hasUsers(List<Long> users) {
        return (root, query, criteriaBuilder) -> users == null ? criteriaBuilder.conjunction()
                : root.get("initiator").get("id").in(users);
    }

    public static Specification<Event> hasStates(List<String> states) {
        return (root, query, criteriaBuilder) -> states == null ? criteriaBuilder.conjunction()
                : root.get("state").in(states);
    }

    public static Specification<Event> hasCategories(List<Long> categories) {
        return (root, query, criteriaBuilder) -> categories == null ? criteriaBuilder.conjunction()
                : root.get("category").get("id").in(categories);
    }

    public static Specification<Event> greaterThan(String rangeStart) {
        return (root, query, criteriaBuilder) -> rangeStart == null ? criteriaBuilder.conjunction()
                : criteriaBuilder.greaterThan(root.get("eventDate"), rangeStart);
    }

    public static Specification<Event> lessThan(String rangeEnd) {
        return (root, query, criteriaBuilder) -> rangeEnd == null ? criteriaBuilder.conjunction()
                : criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd);
    }

    public static Specification<Event> hasText(String text) {
        return (root, query, criteriaBuilder) -> text == null ? criteriaBuilder.conjunction()
                : criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), text),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), text));
    }

    public static Specification<Event> hasOnlyAvailable(Boolean onlyAvailable) {
        return (root, query, criteriaBuilder) -> (onlyAvailable == null || onlyAvailable == false)
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.lessThan(root.get("confirmedRequests"),
                        root.get("participantLimit"));
    }

    public static Specification<Event> hasPaid(Boolean paid) {
        return (root, query, criteriaBuilder) -> paid == null ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("paid"), paid);
    }
}
