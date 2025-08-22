package br.com.daniel.stocksyncservice.controller;

import br.com.daniel.stocksyncservice.model.Product;
import br.com.daniel.stocksyncservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        var a = new Product();
        a.setSku("ABC123");
        a.setName("Product A");
        a.setVendor("vendorA");
        a.setStockQuantity(5);
        productRepository.save(a);

        var b = new Product();
        b.setSku("ABC123");
        b.setName("Product A");
        b.setVendor("vendorB");
        b.setStockQuantity(0);
        productRepository.save(b);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void getProducts_returns_paginated_response_first_page() throws Exception {
        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "sku,asc")
                        .param("sort", "vendor,asc"))
                .andExpect(status().isOk())
                // envelope
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                // mapped DTO (first item should be vendorA due to sort)
                .andExpect(jsonPath("$.content[0].sku").value("ABC123"))
                .andExpect(jsonPath("$.content[0].vendor").value("vendorA"))
                .andExpect(jsonPath("$.content[0].name").value("Product A"))
                .andExpect(jsonPath("$.content[0].stockQuantity").value(5));
    }

    @Test
    void getProducts_returns_paginated_response_second_page() throws Exception {
        mockMvc.perform(get("/products")
                        .param("page", "1")
                        .param("size", "1")
                        .param("sort", "sku,asc")
                        .param("sort", "vendor,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                // second item should be vendorB
                .andExpect(jsonPath("$.content[0].vendor").value("vendorB"))
                .andExpect(jsonPath("$.content[0].sku").value("ABC123"))
                .andExpect(jsonPath("$.content[0].stockQuantity").value(0));
    }

    @Test
    void getProducts_defaults_work() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.size").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }
}
