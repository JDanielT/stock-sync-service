package br.com.daniel.stocksyncservice.response.dto;

import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        String sku,
        String name,
        int stockQuantity,
        String vendor
) {}
