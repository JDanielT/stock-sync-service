package br.com.daniel.stocksyncservice.integration.inventory.vendorB;

import br.com.daniel.stocksyncservice.exception.VendorCsvAccessException;
import br.com.daniel.stocksyncservice.integration.inventory.commom.InventoryProvider;
import br.com.daniel.stocksyncservice.integration.inventory.commom.ProductStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VendorBCsvService implements InventoryProvider {

    private final VendorBCsvMockWriter writer;
    private final Random rng = new Random();

    public VendorBCsvService(VendorBCsvMockWriter writer) {
        this.writer = writer;
    }

    @Override
    public List<ProductStock> fetchStock() {
        try {
            writer.writeSampleCsv(List.of(
                    new ProductStock("ABC123", "Product A", rng.nextInt(0, 5)),
                    new ProductStock("XYZ456", "Product B", rng.nextInt(0, 5))
            ));

            try (var lines = Files.lines(writer.getPath())) {
                return lines
                        .skip(1)
                        .map(line -> line.split(","))
                        .map(parts -> new ProductStock(
                                parts[0],
                                parts[1],
                                Integer.parseInt(parts[2])
                        ))
                        .collect(Collectors.toList());
            }

        } catch (IOException ex) {
            throw new VendorCsvAccessException("Failed to fetch stock from Vendor B CSV", ex);
        }
    }

    @Override
    public String vendor() {
        return "vendorB";
    }
}