package com.example.demo.dto.response.product;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generic wrapper for paginated lists, containing data and metadata.")
public record PaginatedResponse<T>(
        @Schema(description = "The actual list of data items for the current page.") List<T> content,

        @Schema(description = "Current page index (0-based). First page is 0.", example = "0") int page,

        @Schema(description = "Number of items requested per page.", example = "10") int size,

        @Schema(description = "Total count of items across all pages.", example = "45") long totalElements,

        @Schema(description = "Total number of pages available based on current size.", example = "5") int totalPages) {

    public PaginatedResponse(Page<T> pageData) {
        this(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages());
    }

}
