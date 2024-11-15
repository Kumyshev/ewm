package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.model.Event;
import ru.practicum.model.Location;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "lat", expression = "java(newEventDto.getLocation().getLat())")
    @Mapping(target = "lon", expression = "java(newEventDto.getLocation().getLon())")
    Event toEvent(NewEventDto newEventDto);

    @Mapping(source = "event", target = "location", qualifiedByName = "convertToLocation")
    EventFullDto toEventFullDto(Event event);

    @Named("convertToLocation")
    default Location toLocation(Event event) {
        return Location.builder()
                .lat(event.getLat())
                .lon(event.getLon())
                .build();
    }

    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", ignore = true)
    void toUpdate(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event);
}
