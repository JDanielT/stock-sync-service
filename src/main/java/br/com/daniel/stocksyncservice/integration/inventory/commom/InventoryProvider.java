package br.com.daniel.stocksyncservice.integration.inventory.commom;

import java.util.List;

public interface InventoryProvider {
    String vendor();

    List<ProductStock> fetchStock();
}
