package br.com.daniel.stocksyncservice.service;

import br.com.daniel.stocksyncservice.event.AggregatedStock;
import br.com.daniel.stocksyncservice.event.SyncCompletedEvent;
import br.com.daniel.stocksyncservice.integration.inventory.commom.InventoryFacade;
import br.com.daniel.stocksyncservice.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventorySyncService {

    private final InventoryFacade inventoryFacade;
    private final ProductService productService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void syncOnce() {
        var snapshot = inventoryFacade.fetchAll();

        for (var s : snapshot) {
            var p = productService.findBySkuAndVendor(s.sku(), s.vendor()).orElseGet(Product::new);
            p.setSku(s.sku());
            p.setVendor(s.vendor());
            p.setName(s.name());
            p.setStockQuantity(s.stockQuantity());
            productService.save(p);
        }

        var bySku = new HashMap<String, AggregatedStock>();
        for (var s : snapshot) {
            bySku.compute(s.sku(), (k, cur) -> cur == null
                    ? new AggregatedStock(s.sku(), s.name(), s.stockQuantity())
                    : new AggregatedStock(cur.sku(), cur.name(), cur.totalQuantity() + s.stockQuantity()));
        }

        publisher.publishEvent(new SyncCompletedEvent(this, Instant.now(), List.copyOf(bySku.values())));
        log.info("Inventory sync completed. SKUs: {}", bySku.size());
    }
}
