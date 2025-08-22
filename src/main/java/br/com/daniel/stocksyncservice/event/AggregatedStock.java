package br.com.daniel.stocksyncservice.event;

public record AggregatedStock(String sku, String name, int totalQuantity) {}
