package ru.practicum.controller.open;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.dto.CategoryDto;
import ru.practicum.impl.ICategoryService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/categories")
@RequiredArgsConstructor
public class CategoryPublicController {

    private final ICategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findCategories(
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return categoryService.findCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findCategory(@PathVariable Long catId) {
        return categoryService.findCategory(catId);
    }

}
