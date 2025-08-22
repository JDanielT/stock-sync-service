package br.com.daniel.stocksyncservice.service;

import br.com.daniel.stocksyncservice.exception.FetchProductException;
import br.com.daniel.stocksyncservice.exception.ProductPersistenceException;
import br.com.daniel.stocksyncservice.model.Product;
import br.com.daniel.stocksyncservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public Page<Product> list(Pageable pageable) {
        try {
            return productRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Error fetching Products", e);
            throw new FetchProductException("Unable to fetch products", e);
        }
    }

    public Optional<Product> findBySkuAndVendor(String sku, String vendor) {
        try {
            return productRepository.findBySkuAndVendor(sku, vendor);
        } catch (Exception e) {
            log.error("Error fetching Product by sku={} and vendor={}", sku, vendor, e);
            throw new FetchProductException("Unable to fetch product [sku=%s, vendor=%s]".formatted(sku, vendor), e);
        }
    }

    public Product save(Product product) {
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            var message = "Unable to save product [sku=%s, vendor=%s]".formatted(product.getSku(), product.getVendor());
            throw new ProductPersistenceException(message, e);
        }
    }

}
