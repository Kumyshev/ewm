package ru.practicum.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.impl.ICompilationService;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

@Service
@RequiredArgsConstructor
public class CompilationService implements ICompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        if (pinned == null)
            return compilationRepository.findAll(PageRequest.of(from, size)).stream()
                    .map(compilationMapper::toCompilationDto).collect(Collectors.toList());
        return compilationRepository.findByPinned(pinned, PageRequest.of(from, size)).stream()
                .map(compilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto findCompilation(Long compId) {
        return compilationMapper.toCompilationDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found")));
    }

    @Override
    public CompilationDto saveCompilationByAdmin(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null)
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        compilation.setEvents(events);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilationByAdmin(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilationByAdmin(Long compId, UpdateCompilationRequest updateCompilationByAdmin) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        List<Event> events = new ArrayList<>();
        if (updateCompilationByAdmin.getEvents() != null) {
            events = eventRepository.findAllById(updateCompilationByAdmin.getEvents());
        }
        compilationMapper.toUpdate(updateCompilationByAdmin, compilation);
        if (!events.isEmpty())
            compilation.setEvents(events);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

}
