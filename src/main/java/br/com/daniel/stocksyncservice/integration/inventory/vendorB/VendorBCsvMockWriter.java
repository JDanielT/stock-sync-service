package br.com.daniel.stocksyncservice.integration.inventory.vendorB;

import br.com.daniel.stocksyncservice.integration.inventory.commom.ProductStock;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class VendorBCsvMockWriter {

    private static final Path CSV_PATH = Paths.get("/tmp/vendor-b/stock.csv");

    public void writeSampleCsv(List<ProductStock> products) throws IOException {
        Files.createDirectories(CSV_PATH.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(
                CSV_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write("sku,name,stockQuantity\n");
            for (ProductStock p : products) {
                writer.write("%s,%s,%d%n".formatted(p.sku(), p.name(), p.stockQuantity()));
            }
        }
    }

    public Path getPath() { return CSV_PATH; }

}
