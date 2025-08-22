package br.com.daniel.stocksyncservice.service;

import br.com.daniel.stocksyncservice.event.SyncCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ZeroStockService {

    @EventListener
    public void onSyncCompleted(SyncCompletedEvent evt) {
        evt.items().stream()
                .filter(i -> i.totalQuantity() == 0)
                .forEach(i -> log.warn("Zero stock: sku={} name={} at={}", i.sku(), i.name(), evt.at()));
    }

}
