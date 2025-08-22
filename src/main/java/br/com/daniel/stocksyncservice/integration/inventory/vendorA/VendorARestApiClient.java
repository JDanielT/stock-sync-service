package br.com.daniel.stocksyncservice.integration.inventory.vendorA;

import br.com.daniel.stocksyncservice.integration.inventory.commom.InventoryProvider;
import br.com.daniel.stocksyncservice.integration.inventory.commom.ProductStock;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class VendorARestApiClient implements InventoryProvider {

    private final RandomGenerator rng = new SecureRandom();

    @Override
    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2.0)
    )
    public List<ProductStock> fetchStock() {
        if (rng.nextDouble() < 0.35) throw new RuntimeException("VendorA 5xx/timeout");
        var q1 = rng.nextInt(0, 5);
        var q2 = rng.nextInt(0, 5);
        return List.of(
                new ProductStock("ABC123", "Product A", q1),
                new ProductStock("LMN789", "Product C", q2)
        );
    }

    @Override
    public String vendor() {
        return "vendorA";
    }

}
