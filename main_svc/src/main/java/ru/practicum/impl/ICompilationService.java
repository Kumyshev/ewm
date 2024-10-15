package ru.practicum.impl;

import java.util.List;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;

public interface ICompilationService {

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto);

    List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findCompilation(Long compId);
}
