package br.com.daniel.stocksyncservice.event;

import java.time.Instant;
import java.util.List;

public record SyncCompletedEvent(Object source, Instant at, List<AggregatedStock> items) {}
