package ru.practicum.controller.open;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.dto.CompilationDto;
import ru.practicum.impl.ICompilationService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {

    private final ICompilationService compilationService;

    @GetMapping
    public List<CompilationDto> findCompilations(
            @RequestParam(name = "pinned") Boolean pinned,
            @RequestParam(name = "from") Integer from,
            @RequestParam(name = "size") Integer size) {
        return compilationService.findCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto findCompilation(@PathVariable Long compId) {
        return compilationService.findCompilation(compId);
    }

}
