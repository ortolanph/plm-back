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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LenderRepository lenderRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private TransactionMapper mapper;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository, lenderRepository, transactionHistoryRepository, mapper);
    }

    @Test
    void create_borrowed_success_returnsTransactionResponse() {
        UUID lenderId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(
            lenderId,
            new BigDecimal("100.00"),
            TransactionType.BORROWED,
            TransactionPaymentType.MONEY,
            null);

        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();
        Transaction transaction = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("100.00"))
            .transactionType(TransactionType.BORROWED)
            .transactionPaymentType(TransactionPaymentType.MONEY)
            .build();

        TransactionResponse expectedResponse = new TransactionResponse(
            transaction.getId(),
            lenderId,
            transaction.getTransactionDate(),
            new BigDecimal("100.00"),
            TransactionType.BORROWED,
            TransactionPaymentType.MONEY);

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(mapper.toResponse(transaction)).thenReturn(expectedResponse);

        TransactionResponse response = transactionService.create(request);

        assertNotNull(response);
        assertEquals(TransactionType.BORROWED, response.transactionType());
        assertEquals(new BigDecimal("100.00"), response.transactionValue());
    }

    @Test
    void create_payment_success_returnsTransactionResponse() {
        UUID lenderId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(
            lenderId,
            new BigDecimal("50.00"),
            TransactionType.PAYMENT,
            TransactionPaymentType.WIRE_TRANSACTION,
            null);

        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();
        Transaction transaction = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("50.00"))
            .transactionType(TransactionType.PAYMENT)
            .transactionPaymentType(TransactionPaymentType.WIRE_TRANSACTION)
            .build();

        TransactionResponse expectedResponse = new TransactionResponse(
            transaction.getId(),
            lenderId,
            transaction.getTransactionDate(),
            new BigDecimal("50.00"),
            TransactionType.PAYMENT,
            TransactionPaymentType.WIRE_TRANSACTION);

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(mapper.toResponse(transaction)).thenReturn(expectedResponse);

        TransactionResponse response = transactionService.create(request);

        assertNotNull(response);
        assertEquals(TransactionType.PAYMENT, response.transactionType());
    }

    @Test
    void create_lenderNotFound_throwsException() {
        UUID lenderId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(
            lenderId,
            new BigDecimal("100.00"),
            TransactionType.BORROWED,
            null,
            null);

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.create(request));
    }

    @Test
    void create_withCancelTransactionId_createsCancelledTransaction() {
        UUID transactionId = UUID.randomUUID();
        UUID lenderId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(
            lenderId,
            null,
            null,
            null,
            transactionId);

        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();
        Transaction originalTransaction = Transaction.builder()
            .id(transactionId)
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("100.00"))
            .transactionType(TransactionType.BORROWED)
            .transactionPaymentType(TransactionPaymentType.MONEY)
            .build();

        Transaction cancelledTransaction = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("100.00"))
            .transactionType(TransactionType.CANCELLED)
            .transactionPaymentType(TransactionPaymentType.MONEY)
            .build();

        TransactionResponse expectedResponse = new TransactionResponse(
            cancelledTransaction.getId(),
            lenderId,
            cancelledTransaction.getTransactionDate(),
            new BigDecimal("100.00"),
            TransactionType.CANCELLED,
            TransactionPaymentType.MONEY);

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(originalTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(cancelledTransaction);
        when(mapper.toResponse(cancelledTransaction)).thenReturn(expectedResponse);

        TransactionResponse response = transactionService.create(request);

        assertNotNull(response);
        assertEquals(TransactionType.CANCELLED, response.transactionType());
    }

    @Test
    void create_withCancelTransactionId_alreadyCancelled_throwsException() {
        UUID transactionId = UUID.randomUUID();
        UUID lenderId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(
            lenderId,
            null,
            null,
            null,
            transactionId);

        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();
        Transaction alreadyCancelled = Transaction.builder()
            .id(transactionId)
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("100.00"))
            .transactionType(TransactionType.CANCELLED)
            .transactionPaymentType(TransactionPaymentType.MONEY)
            .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(alreadyCancelled));

        assertThrows(IllegalArgumentException.class, () -> transactionService.create(request));
    }

    @Test
    void create_withCancelTransactionId_notFound_throwsException() {
        UUID transactionId = UUID.randomUUID();
        UUID lenderId = UUID.randomUUID();
        CreateTransactionRequest request = new CreateTransactionRequest(
            lenderId,
            null,
            null,
            null,
            transactionId);

        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.create(request));
    }

    @Test
    void query_withFilters_returnsTransactions() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder().id(lenderId).name("John Doe").build();

        Transaction t1 = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("100.00"))
            .transactionType(TransactionType.BORROWED)
            .build();

        Transaction t2 = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(new BigDecimal("50.00"))
            .transactionType(TransactionType.PAYMENT)
            .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByFilters(eq(lenderId), any(), any(), any(), any(), any()))
            .thenReturn(List.of(t1, t2));

        TransactionQueryResponse response = transactionService.query(
            lenderId, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(2, response.transactions().size());
        assertEquals("John Doe", response.lender());
    }

    @Test
    void query_lenderNotFound_throwsException() {
        UUID lenderId = UUID.randomUUID();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.query(
            lenderId, null, null, null, null, null));
    }

    @Test
    void queryHistory_withFilters_returnsHistory() {
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

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByFilters(eq(lenderId), any(), any(), any(), any(), any()))
            .thenReturn(List.of(h1, h2));

        HistoryQueryResponse response = transactionService.queryHistory(
            lenderId, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(2, response.history().size());
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
    void queryHistory_noFilters_returnsAllHistory() {
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
        when(transactionHistoryRepository.findByFilters(eq(lenderId), isNull(), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(List.of(h1));

        HistoryQueryResponse response = transactionService.queryHistory(
            lenderId, null, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.history().size());
        assertEquals("PAID_IN_FULL", response.history().getFirst().type());
    }
}