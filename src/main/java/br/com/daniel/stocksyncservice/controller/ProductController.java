package br.com.daniel.stocksyncservice.controller;

import br.com.daniel.stocksyncservice.response.dto.PaginatedResponse;
import br.com.daniel.stocksyncservice.response.dto.ProductResponse;
import br.com.daniel.stocksyncservice.response.parser.ProductResponseParser;
import br.com.daniel.stocksyncservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductResponseParser productResponseParser;

    @GetMapping
    @Operation(
            summary = "List products (paginated)",
            description = "Use `page`, `size`, and `sort` query params."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paginated products",
            content = @Content(schema = @Schema(implementation = PaginatedResponse.class))
    )
    public ResponseEntity<PaginatedResponse<ProductResponse>> getProducts(
            @ParameterObject
            @PageableDefault(size = 20, sort = {"sku", "vendor"}) Pageable pageable
    ) {
        var page = productService.list(pageable);
        var body = PaginatedResponse.from(page, productResponseParser::parse);
        return ResponseEntity.ok(body);
    }
}
