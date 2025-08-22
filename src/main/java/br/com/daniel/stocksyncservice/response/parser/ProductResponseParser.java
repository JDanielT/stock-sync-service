package br.com.daniel.stocksyncservice.response.parser;

import br.com.daniel.stocksyncservice.model.Product;
import br.com.daniel.stocksyncservice.response.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseParser {

    public ProductResponse parse(Product product) {
        if (product == null) {
            return null;
        }
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .stockQuantity(product.getStockQuantity())
                .vendor(product.getVendor())
                .build();
    }
}
