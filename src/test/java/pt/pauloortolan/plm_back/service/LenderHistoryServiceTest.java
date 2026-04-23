package pt.pauloortolan.plm_back.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.model.*;
import pt.pauloortolan.plm_back.repository.LenderRepository;
import pt.pauloortolan.plm_back.repository.TransactionHistoryRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LenderHistoryServiceTest {

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private LenderRepository lenderRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(null, lenderRepository, transactionHistoryRepository, null);
    }

    @Test
    void queryHistory_byDate_returnsHistory() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        TransactionHistory h1 = TransactionHistory.builder()
                .id(UUID.randomUUID())
                .lenderName("John Doe")
                .historyDate(LocalDateTime.of(2025, 1, 15, 10, 0))
                .transactionValue(new BigDecimal("100.00"))
                .historyType(HistoryType.PAID_IN_FULL)
                .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByFilters(eq(lenderId), any(), any(), any(), any(), any()))
                .thenReturn(List.of(h1));

        HistoryQueryResponse response = transactionService.queryHistory(
                lenderId, "2025-01-01", "2025-12-31", null, null, null);

        assertNotNull(response);
        assertEquals(1, response.history().size());
        assertEquals("John Doe", response.lender());
    }

    @Test
    void queryHistory_byValueRange_returnsFilteredHistory() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        TransactionHistory h1 = TransactionHistory.builder()
                .id(UUID.randomUUID())
                .lenderName("John Doe")
                .historyDate(LocalDateTime.now())
                .transactionValue(new BigDecimal("50.00"))
                .historyType(HistoryType.PAID_IN_FULL)
                .build();

        TransactionHistory h2 = TransactionHistory.builder()
                .id(UUID.randomUUID())
                .lenderName("John Doe")
                .historyDate(LocalDateTime.now())
                .transactionValue(new BigDecimal("150.00"))
                .historyType(HistoryType.PAID_IN_FULL)
                .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByFilters(eq(lenderId), isNull(), isNull(), eq(new BigDecimal("50.00")), eq(new BigDecimal("150.00")), isNull()))
                .thenReturn(List.of(h1, h2));

        HistoryQueryResponse response = transactionService.queryHistory(
                lenderId, null, null, new BigDecimal("50.00"), new BigDecimal("150.00"), null);

        assertNotNull(response);
        assertEquals(2, response.history().size());
    }

    @Test
    void queryHistory_byType_returnsFilteredHistory() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        TransactionHistory h1 = TransactionHistory.builder()
                .id(UUID.randomUUID())
                .lenderName("John Doe")
                .historyDate(LocalDateTime.now())
                .transactionValue(new BigDecimal("100.00"))
                .historyType(HistoryType.PAID_IN_FULL)
                .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByFilters(eq(lenderId), isNull(), isNull(), isNull(), isNull(), eq("PAID_IN_FULL")))
                .thenReturn(List.of(h1));

        HistoryQueryResponse response = transactionService.queryHistory(
                lenderId, null, null, null, null, "PAID_IN_FULL");

        assertNotNull(response);
        assertEquals(1, response.history().size());
        assertEquals("PAID_IN_FULL", response.history().get(0).type());
    }

    @Test
    void queryHistory_noParameters_returnsAllHistory() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        TransactionHistory h1 = TransactionHistory.builder()
                .id(UUID.randomUUID())
                .lenderName("John Doe")
                .historyDate(LocalDateTime.now())
                .transactionValue(new BigDecimal("100.00"))
                .historyType(HistoryType.PAID_IN_FULL)
                .build();

        TransactionHistory h2 = TransactionHistory.builder()
                .id(UUID.randomUUID())
                .lenderName("John Doe")
                .historyDate(LocalDateTime.now())
                .transactionValue(new BigDecimal("200.00"))
                .historyType(HistoryType.FORGIVEN)
                .build();

        TransactionHistory h3 = TransactionHistory.builder()
                .id(UUID.randomUUID())
                .lenderName("John Doe")
                .historyDate(LocalDateTime.now())
                .transactionValue(new BigDecimal("300.00"))
                .historyType(HistoryType.PAID_IN_FULL)
                .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByFilters(eq(lenderId), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(h1, h2, h3));

        HistoryQueryResponse response = transactionService.queryHistory(
                lenderId, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(3, response.history().size());
        assertEquals("John Doe", response.lender());
    }

    @Test
    void queryHistory_lenderNotFound_throwsException() {
        UUID lenderId = UUID.randomUUID();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.queryHistory(
                lenderId, null, null, null, null, null));
    }

    @Test
    void queryHistory_emptyResult_returnsEmptyList() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByFilters(eq(lenderId), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        HistoryQueryResponse response = transactionService.queryHistory(
                lenderId, "2099-01-01", "2099-12-31", null, null, null);

        assertNotNull(response);
        assertTrue(response.history().isEmpty());
    }

    @Test
    void queryHistory_combinedFilters_returnsFilteredHistory() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        TransactionHistory h1 = TransactionHistory.builder()
                .id(UUID.randomUUID())
                .lenderName("John Doe")
                .historyDate(LocalDateTime.of(2025, 6, 15, 10, 0))
                .transactionValue(new BigDecimal("200.00"))
                .historyType(HistoryType.PAID_IN_FULL)
                .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByFilters(
                eq(lenderId),
                eq(java.time.LocalDate.parse("2025-01-01")),
                eq(java.time.LocalDate.parse("2025-12-31")),
                eq(new BigDecimal("100.00")),
                eq(new BigDecimal("500.00")),
                eq("PAID_IN_FULL")))
                .thenReturn(List.of(h1));

        HistoryQueryResponse response = transactionService.queryHistory(
                lenderId, "2025-01-01", "2025-12-31", new BigDecimal("100.00"), new BigDecimal("500.00"), "PAID_IN_FULL");

        assertNotNull(response);
        assertEquals(1, response.history().size());
    }
}