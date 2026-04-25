package pt.pauloortolan.plm_back.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.mapper.*;
import pt.pauloortolan.plm_back.model.*;
import pt.pauloortolan.plm_back.repository.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LenderServiceTest {

    @Mock
    private LenderRepository repository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private LenderMapper mapper;

    private LenderService lenderService;

    @BeforeEach
    void setUp() {
        lenderService = new LenderService(repository, transactionRepository, transactionHistoryRepository, mapper);
    }

    @Test
    void create_success_returnsLenderResponse() {
        CreateLenderRequest request = new CreateLenderRequest("John Doe", "+1234567890", "IBAN123", "123 Main St");

        Lender savedLender = Lender.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .build();
        savedLender.setCreatedAt(LocalDateTime.now());
        savedLender.setUpdatedAt(LocalDateTime.now());

        LenderResponse expectedResponse = new LenderResponse(
            savedLender.getId(), "John Doe", "+1234567890", "IBAN123", "123 Main St",
            savedLender.getCreatedAt(), savedLender.getUpdatedAt());

        when(mapper.toEntity(request)).thenReturn(savedLender);
        when(repository.save(any(Lender.class))).thenReturn(savedLender);
        when(mapper.toResponse(savedLender)).thenReturn(expectedResponse);

        LenderResponse response = lenderService.create(request);

        assertNotNull(response);
        assertEquals("John Doe", response.name());
    }

    @Test
    void update_success_updatesLender() {
        UUID lenderId = UUID.randomUUID();
        UpdateLenderRequest request = new UpdateLenderRequest("New Name", "+2222222222", null, null);
        Lender existingLender = Lender.builder().id(lenderId).name("Old Name").build();
        existingLender.setCreatedAt(LocalDateTime.now());
        existingLender.setUpdatedAt(LocalDateTime.now());

        LenderResponse expectedResponse = new LenderResponse(lenderId, "New Name", "+2222222222", null, null,
            existingLender.getCreatedAt(), LocalDateTime.now());

        when(repository.findById(lenderId)).thenReturn(Optional.of(existingLender));
        when(repository.save(any(Lender.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Lender.class))).thenReturn(expectedResponse);

        LenderResponse response = lenderService.update(lenderId, request);

        assertNotNull(response);
        assertEquals("New Name", response.name());
    }

    @Test
    void update_notFound_throwsException() {
        UUID lenderId = UUID.randomUUID();
        UpdateLenderRequest request = new UpdateLenderRequest("New Name", null, null, null);

        when(repository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lenderService.update(lenderId, request));
    }

    @Test
    void query_byName_returnsMatchingLenders() {
        String nameFilter = "John";
        Lender lender = Lender.builder().id(UUID.randomUUID()).name("John Doe").build();
        LenderResponse expectedResponse = new LenderResponse(
            lender.getId(), "John Doe", null, null, null, LocalDateTime.now(), LocalDateTime.now());

        when(repository.findByNameContaining(nameFilter)).thenReturn(List.of(lender));
        when(mapper.toResponse(lender)).thenReturn(expectedResponse);

        List<LenderResponse> results = lenderService.query(nameFilter, null);

        assertEquals(1, results.size());
    }

    @Test
    void query_byPhone_returnsMatchingLenders() {
        String phoneFilter = "+1234567890";
        Lender lender = Lender.builder().id(UUID.randomUUID()).name("John Doe").phone(phoneFilter).build();
        LenderResponse expectedResponse = new LenderResponse(
            lender.getId(), "John Doe", phoneFilter, null, null, LocalDateTime.now(), LocalDateTime.now());

        when(repository.findByPhoneContaining(phoneFilter)).thenReturn(List.of(lender));
        when(mapper.toResponse(lender)).thenReturn(expectedResponse);

        List<LenderResponse> results = lenderService.query(null, phoneFilter);

        assertEquals(1, results.size());
    }

    @Test
    void query_noFilters_returnsAllLenders() {
        Lender lender1 = Lender.builder().id(UUID.randomUUID()).name("John").build();
        Lender lender2 = Lender.builder().id(UUID.randomUUID()).name("Jane").build();
        LenderResponse response = new LenderResponse(
            UUID.randomUUID(), "Test", null, null, null, LocalDateTime.now(), LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(lender1, lender2));
        when(mapper.toResponse(any(Lender.class))).thenReturn(response);

        List<LenderResponse> results = lenderService.query(null, null);

        assertEquals(2, results.size());
    }

    @Test
    void getById_success_returnsLender() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();
        lender.setCreatedAt(LocalDateTime.now());
        lender.setUpdatedAt(LocalDateTime.now());

        LenderResponse expectedResponse = new LenderResponse(lenderId, "John Doe", null, null, null,
            lender.getCreatedAt(), lender.getUpdatedAt());

        when(repository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(mapper.toResponse(lender)).thenReturn(expectedResponse);

        LenderResponse response = lenderService.getById(lenderId);

        assertNotNull(response);
        assertEquals(lenderId, response.id());
    }

    @Test
    void getById_notFound_throwsException() {
        UUID lenderId = UUID.randomUUID();
        when(repository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lenderService.getById(lenderId));
    }

    @Test
    void query_byPhonePartial_returnsMatchingLenders() {
        String phoneFilter = "+1234";
        Lender lender1 = Lender.builder().id(UUID.randomUUID()).name("John Doe").phone("+1234567890").build();
        LenderResponse response1 = new LenderResponse(
            lender1.getId(), "John Doe", "+1234567890", null, null, LocalDateTime.now(), LocalDateTime.now());

        when(repository.findByPhoneContaining(phoneFilter)).thenReturn(List.of(lender1));
        when(mapper.toResponse(lender1)).thenReturn(response1);

        List<LenderResponse> results = lenderService.query(null, phoneFilter);

        assertEquals(1, results.size());
    }

    @Test
    void query_withPhonePartialInFilter_combinesWithName() {
        String nameFilter = "John";
        String phoneFilter = "+1234";

        when(repository.findByFilters(nameFilter, phoneFilter)).thenReturn(List.of());

        lenderService.query(nameFilter, phoneFilter);

        verify(repository).findByFilters(nameFilter, phoneFilter);
    }

    @Test
    void settleLender_success_movesTransactionsToHistory() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder()
            .id(lenderId)
            .name("John Doe")
            .phone("+1234567890")
            .bankData("IBAN123")
            .build();

        Transaction borrowedTx = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("100.00"))
            .transactionType(TransactionType.BORROWED)
            .build();

        Transaction paymentTx = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("30.00"))
            .transactionType(TransactionType.PAYMENT)
            .transactionPaymentType(TransactionPaymentType.MONEY)
            .build();

        SettleLenderRequest request = new SettleLenderRequest(
            lenderId, HistoryType.PAID_IN_FULL, TransactionPaymentType.MONEY);

        when(repository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(borrowedTx, paymentTx));

        lenderService.settleLender(request);

        verify(transactionHistoryRepository, times(3)).save(any(TransactionHistory.class));
        verify(transactionRepository).deleteAll(List.of(borrowedTx, paymentTx));
    }

    @Test
    void settleLender_notFound_throwsException() {
        UUID lenderId = UUID.randomUUID();
        SettleLenderRequest request = new SettleLenderRequest(
            lenderId, HistoryType.PAID_IN_FULL, TransactionPaymentType.MONEY);

        when(repository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lenderService.settleLender(request));
    }

    @Test
    void deleteLender_success_deletesLender() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        when(repository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(Collections.emptyList());

        lenderService.deleteLender(lenderId);

        verify(repository).delete(lender);
    }

    @Test
    void deleteLender_withOpenTransactions_throwsException() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        Transaction borrowedTx = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionType(TransactionType.BORROWED)
            .transactionValue(new BigDecimal("100.00"))
            .build();

        when(repository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(borrowedTx));

        assertThrows(IllegalStateException.class, () -> lenderService.deleteLender(lenderId));
        verify(repository, never()).delete(any(Lender.class));
    }

    @Test
    void deleteLender_notFound_throwsException() {
        UUID lenderId = UUID.randomUUID();

        when(repository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lenderService.deleteLender(lenderId));
    }

    @Test
    void getSummary_success_returnsSummary() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder()
            .id(lenderId)
            .name("John Doe")
            .build();

        Transaction borrowedTx = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("100.00"))
            .transactionType(TransactionType.BORROWED)
            .build();

        Transaction paymentTx = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("50.00"))
            .transactionType(TransactionType.PAYMENT)
            .build();

        TransactionHistory history = TransactionHistory.builder()
            .id(UUID.randomUUID())
            .lenderId(lenderId)
            .historyDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("100.00"))
            .historyType(HistoryType.PAID_IN_FULL)
            .build();

        when(repository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(borrowedTx, paymentTx));
        when(transactionHistoryRepository.findAll()).thenReturn(List.of(history));

        LenderSummaryResponse response = lenderService.getSummary(lenderId);

        assertNotNull(response);
        assertEquals("John Doe", response.lender());
        assertEquals(new BigDecimal("50.00"), response.total());
        assertEquals(2, response.transactions().size());
        assertEquals(1, response.history().size());
    }

    @Test
    void getSummary_notFound_throwsException() {
        UUID lenderId = UUID.randomUUID();

        when(repository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lenderService.getSummary(lenderId));
    }

    @Test
    void getDebts_success_returnsDebtsReport() {
        UUID lenderId1 = UUID.randomUUID();
        UUID lenderId2 = UUID.randomUUID();

        Lender lender1 = Lender.builder()
            .id(lenderId1)
            .name("John Doe")
            .build();

        Lender lender2 = Lender.builder()
            .id(lenderId2)
            .name("Jane Doe")
            .build();

        Transaction borrowedTx1 = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender1)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("3000.00"))
            .transactionType(TransactionType.BORROWED)
            .build();

        Transaction paymentTx1 = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender1)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("500.00"))
            .transactionType(TransactionType.PAYMENT)
            .build();

        Transaction borrowedTx2 = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender2)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("2000.00"))
            .transactionType(TransactionType.BORROWED)
            .build();

        when(repository.findAll()).thenReturn(List.of(lender1, lender2));
        when(transactionRepository.findAll()).thenReturn(List.of(borrowedTx1, paymentTx1, borrowedTx2));

        DebtsResponse response = lenderService.getDebts();

        assertNotNull(response);
        assertEquals(new BigDecimal("4500.00"), response.totalDebt());
        assertEquals(2, response.details().size());
        assertEquals("John Doe", response.details().getFirst().lender());
        assertEquals(new BigDecimal("2500.00"), response.details().getFirst().total());
    }

    @Test
    void getDebts_noDebts_returnsEmptyDetails() {
        Lender lender = Lender.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .build();

        when(repository.findAll()).thenReturn(List.of(lender));
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        DebtsResponse response = lenderService.getDebts();

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.totalDebt());
        assertTrue(response.details().isEmpty());
    }

    @Test
    void getDebts_lenderWithOnlyPayments_returnsNoDebt() {
        UUID lenderId = UUID.randomUUID();

        Lender lender = Lender.builder()
            .id(lenderId)
            .name("John Doe")
            .build();

        Transaction paymentTx = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("1000.00"))
            .transactionType(TransactionType.PAYMENT)
            .build();

        when(repository.findAll()).thenReturn(List.of(lender));
        when(transactionRepository.findAll()).thenReturn(List.of(paymentTx));

        DebtsResponse response = lenderService.getDebts();

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.totalDebt());
        assertTrue(response.details().isEmpty());
    }
}