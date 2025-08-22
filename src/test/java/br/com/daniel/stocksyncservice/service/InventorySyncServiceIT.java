package br.com.daniel.stocksyncservice.service;

import br.com.daniel.stocksyncservice.event.AggregatedStock;
import br.com.daniel.stocksyncservice.event.SyncCompletedEvent;
import br.com.daniel.stocksyncservice.model.Product;
import br.com.daniel.stocksyncservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
@TestPropertySource(properties = {
        "spring.task.scheduling.enabled=false"
})
class InventorySyncServiceIT {

    @Autowired
    private InventorySyncService syncService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ApplicationEvents events;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        try {
            Files.deleteIfExists(Path.of("/tmp/vendor-b/stock.csv"));
        } catch (Exception ignored) {
        }
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        try {
            Files.deleteIfExists(Path.of("/tmp/vendor-b/stock.csv"));
        } catch (Exception ignored) {
        }
    }

    @Test
    void persists_rows_from_vendorA_and_vendorB() {
        // when
        syncService.syncOnce();

        // then
        var all = productRepository.findAll();
        assertThat(all).isNotEmpty();

        // assert vendors present
        Set<String> vendors = all.stream().map(Product::getVendor).collect(Collectors.toSet());
        assertThat(vendors).contains("vendorA", "vendorB");

        // sanity: each row has SKU, name, non-negative quantity
        assertThat(all).allSatisfy(p -> {
            assertThat(p.getSku()).isNotBlank();
            assertThat(p.getName()).isNotBlank();
            assertThat(p.getStockQuantity()).isGreaterThanOrEqualTo(0);
        });
    }

    @Test
    void merge_aggregates_totals_per_sku_correctly() {
        // when
        syncService.syncOnce();

        // DB sums per SKU across vendors
        var all = productRepository.findAll();
        var dbSumBySku = all.stream().collect(Collectors.groupingBy(
                Product::getSku,
                Collectors.summingInt(Product::getStockQuantity)
        ));

        // the SyncCompletedEvent must exist
        var published = events.stream(SyncCompletedEvent.class).toList();
        assertThat(published).hasSize(1);

        var evt = published.get(0);
        assertThat(evt.items()).isNotEmpty();

        // for every aggregated item, totalQuantity == sum of vendor rows in DB
        for (AggregatedStock item : evt.items()) {
            assertThat(dbSumBySku.getOrDefault(item.sku(), 0))
                    .as("DB sum for sku %s equals event totalQuantity", item.sku())
                    .isEqualTo(item.totalQuantity());
        }

        // optional: 1 aggregated entry per distinct SKU
        assertThat(evt.items().stream().map(AggregatedStock::sku).collect(Collectors.toSet()).size())
                .isEqualTo(dbSumBySku.keySet().size());
    }
}
