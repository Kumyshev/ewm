package ru.practicum.impl;

import java.util.List;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

public interface ICategoryService {

    CategoryDto saveCategoryByAdmin(NewCategoryDto newCategoryDto);

    void deleteCategoryByAdmin(Long catId);

    CategoryDto updateCategoryByAdmin(Long catId, NewCategoryDto newCategoryDto);

    List<CategoryDto> findCategories(Integer from, Integer size);

    CategoryDto findCategory(Long catId);
}
