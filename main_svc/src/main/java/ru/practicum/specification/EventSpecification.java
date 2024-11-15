package ru.practicum.specification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import ru.practicum.enums.EventState;
import ru.practicum.model.Event;

@Component
public class EventSpecification {

        public static Specification<Event> inUsers(List<Long> users) {
                return (root, query, criteriaBuilder) -> users == null || users.isEmpty()
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.in(root.get("initiator").get("id")).value(users);
        }

        public static Specification<Event> inStates(List<EventState> states) {
                return (root, query, criteriaBuilder) -> states == null || states.isEmpty()
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.in(root.get("state")).value(states);
        }

        public static Specification<Event> inCategories(List<Long> categories) {
                return (root, query, criteriaBuilder) -> categories == null || categories.isEmpty()
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.in(root.get("category").get("id")).value(categories);
        }

        public static Specification<Event> isAfter(LocalDateTime rangeStart) {
                return (root, query, criteriaBuilder) -> rangeStart == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                                                rangeStart);
        }

        public static Specification<Event> isBefore(LocalDateTime rangeEnd) {
                return (root, query, criteriaBuilder) -> rangeEnd == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
        }

        public static Specification<Event> inAnnotationOrDescription(String text) {
                return (root, query, criteriaBuilder) -> text == null || text.isEmpty()
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.or(
                                                criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                                                                "%" + text.toLowerCase() + "%"),
                                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                                                                "%" + text.toLowerCase() + "%"));
        }

        public static Specification<Event> isPaid(Boolean paid) {
                return (root, query, criteriaBuilder) -> paid == null
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.equal(root.get("paid"), paid);
        }

        public static Specification<Event> isOnlyAvailable(Boolean onlyAvailable) {
                return (root, query, criteriaBuilder) -> onlyAvailable == null || onlyAvailable == false
                                ? criteriaBuilder.conjunction()
                                : criteriaBuilder.lessThan(root.get("confirmedRequests"),
                                                root.get("participantLimit"));
        }

        public static Specification<Event> isAfterCurrentDate() {
                return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("eventDate"),
                                LocalDateTime.now());
        }
}
