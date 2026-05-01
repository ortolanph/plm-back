package pt.pauloortolan.plm_back.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pauloortolan.plm_back.repository.TransactionHistoryRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryPurgeServiceTest {

    @Mock
    private TransactionHistoryRepository historyRepository;

    @InjectMocks
    private HistoryPurgeService historyPurgeService;

    @Test
    void purgeExpiredHistory_shouldDeleteRecordsOlderThanOneYear() {
        historyPurgeService.purgeExpiredHistory();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(historyRepository, times(1)).deleteByHistoryDateBefore(captor.capture());

        LocalDateTime cutoff = captor.getValue();
        LocalDateTime expectedCutoff = LocalDateTime.now().minusYears(1);
        assertTrue(cutoff.isBefore(expectedCutoff.plusSeconds(1)) && cutoff.isAfter(expectedCutoff.minusSeconds(1)));
    }
}
