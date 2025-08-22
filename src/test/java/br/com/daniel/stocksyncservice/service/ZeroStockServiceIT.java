package br.com.daniel.stocksyncservice.service;

import br.com.daniel.stocksyncservice.event.SyncCompletedEvent;
import br.com.daniel.stocksyncservice.integration.inventory.commom.InventoryFacade;
import br.com.daniel.stocksyncservice.integration.inventory.commom.VendorStock; // adjust if your path differs
import br.com.daniel.stocksyncservice.model.Product;
import br.com.daniel.stocksyncservice.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ZeroStockServiceIT {

    @TestConfiguration
    static class FacadeTestConfig {
        @Bean
        @Primary
        InventoryFacade testInventoryFacade() {
            return new InventoryFacade(List.of()) {
                @Override
                public List<VendorStock> fetchAll() {
                    return List.of(
                            new VendorStock("ZERO1", "Zero Product", 0, "vendorA"),
                            new VendorStock("ZERO1", "Zero Product", 0, "vendorB"),
                            new VendorStock("NONZERO", "Ok Product", 3, "vendorA")
                    );
                }
            };
        }
    }

    @Autowired private InventorySyncService syncService;
    @Autowired private ProductRepository productRepository;

    @SpyBean private ZeroStockService zeroStockService;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        try { Files.deleteIfExists(Path.of("/tmp/vendor-b/stock.csv")); } catch (Exception ignored) {}
        Mockito.reset(zeroStockService);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        try { Files.deleteIfExists(Path.of("/tmp/vendor-b/stock.csv")); } catch (Exception ignored) {}
    }

    @Test
    void zeroStockService_receives_SyncCompletedEvent() {
        // when
        syncService.syncOnce();

        // then: listener method was called with the published event
        verify(zeroStockService, timeout(2000).times(1))
                .onSyncCompleted(any(SyncCompletedEvent.class));
    }

    @Test
    void logs_zero_stock_when_aggregated_total_is_zero(CapturedOutput output) {
        // when
        syncService.syncOnce();

        var all = productRepository.findAll();
        assertThat(all).extracting(Product::getSku).contains("ZERO1", "NONZERO");
        assertThat(all.stream().filter(p -> p.getSku().equals("ZERO1")).count()).isEqualTo(2);
        assertThat(all.stream().filter(p -> p.getSku().equals("NONZERO")).count()).isEqualTo(1);

        // log assertions
        var logs = output.getOut() + output.getErr();
        assertThat(logs).contains("Zero stock: sku=ZERO1 name=Zero Product");
        assertThat(logs).doesNotContain("Zero stock: sku=NONZERO");
    }
}
