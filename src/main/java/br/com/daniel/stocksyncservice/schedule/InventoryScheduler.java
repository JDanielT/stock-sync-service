package br.com.daniel.stocksyncservice.schedule;

import br.com.daniel.stocksyncservice.service.InventorySyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryScheduler {

    private final InventorySyncService sync;

    @Scheduled(cron = "0 */5 * * * *")
    public void run() {
        sync.syncOnce();
    }

}