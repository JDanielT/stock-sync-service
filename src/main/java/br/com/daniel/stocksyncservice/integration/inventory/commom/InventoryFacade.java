package br.com.daniel.stocksyncservice.integration.inventory.commom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryFacade {

    private final List<InventoryProvider> providers;

    public List<VendorStock> fetchAll() {
        return providers.stream()
                .flatMap(p -> p.fetchStock().stream()
                        .map(s -> new VendorStock(
                                s.sku(),
                                s.name(),
                                Math.max(0, s.stockQuantity()),
                                p.vendor()
                        ))
                )
                .toList();
    }

}
