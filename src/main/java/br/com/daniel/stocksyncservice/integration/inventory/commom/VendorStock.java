package br.com.daniel.stocksyncservice.integration.inventory.commom;

public record VendorStock(String sku, String name, int stockQuantity, String vendor) {}

