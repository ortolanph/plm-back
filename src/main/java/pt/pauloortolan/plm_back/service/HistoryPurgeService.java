package pt.pauloortolan.plm_back.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.pauloortolan.plm_back.repository.TransactionHistoryRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
public class HistoryPurgeService {

    private final TransactionHistoryRepository historyRepository;

    public HistoryPurgeService(TransactionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void purgeExpiredHistory() {
        log.info("HistoryPurgeService::purgeExpiredHistory()::at={}", LocalDateTime.now());
        LocalDateTime cutoff = LocalDateTime.now().minusYears(1);
        historyRepository.deleteByHistoryDateBefore(cutoff);
        log.info("Purged history records older than {}", cutoff);
    }
}
