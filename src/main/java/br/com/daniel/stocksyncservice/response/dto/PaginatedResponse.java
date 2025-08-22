package br.com.daniel.stocksyncservice.response.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.function.Function;

@Schema(name = "PaginatedResponse")
public record PaginatedResponse<T>(
        @Schema(description = "Current page content")
        List<T> content,
        @Schema(description = "Zero-based page index")
        int page,
        @Schema(description = "Page size")
        int size,
        @Schema(description = "Total number of elements")
        long totalElements,
        @Schema(description = "Total number of pages")
        int totalPages,
        @Schema(description = "Is this the first page?")
        boolean first,
        @Schema(description = "Is this the last page?")
        boolean last,
        @Schema(description = "Sort orders applied")
        List<SortOrder> sort
) {
    @Schema(name = "SortOrder")
    public record SortOrder(String property, String direction) {
    }

    public static <T> PaginatedResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSort().stream()
                        .map(o -> new SortOrder(o.getProperty(), o.isAscending() ? "ASC" : "DESC"))
                        .toList()
        );
    }

    public static <S, T> PaginatedResponse<T> from(org.springframework.data.domain.Page<S> page,
                                                   Function<S, T> mapper) {
        return new PaginatedResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSort().stream()
                        .map(o -> new SortOrder(o.getProperty(), o.isAscending() ? "ASC" : "DESC"))
                        .toList()
        );
    }
}
