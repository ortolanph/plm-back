package pt.pauloortolan.plm_back.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.mapper.*;
import pt.pauloortolan.plm_back.model.*;
import pt.pauloortolan.plm_back.repository.*;

import java.math.*;
import java.time.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final LenderRepository lenderRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionMapper mapper;

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request) {
        log.info("TransactionService::create(idLender={}, type={}, cancelId={})",
            request.idLender(), request.transactionType(), request.cancelTransactionId());

        Lender lender = lenderRepository.findById(request.idLender())
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + request.idLender()));

        if (request.cancelTransactionId() != null) {
            log.info("TransactionService::create - cancelling transaction: {}", request.cancelTransactionId());

            Transaction originalTransaction = transactionRepository.findById(request.cancelTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + request.cancelTransactionId()));

            if (originalTransaction.getTransactionType() == TransactionType.CANCELLED) {
                throw new IllegalArgumentException("Transaction is already cancelled");
            }

            Transaction cancelledTransaction = Transaction.builder()
                .lender(lender)
                .transactionDate(LocalDateTime.now())
                .transactionValue(originalTransaction.getTransactionValue())
                .transactionType(TransactionType.CANCELLED)
                .transactionPaymentType(originalTransaction.getTransactionPaymentType())
                .build();

            cancelledTransaction = transactionRepository.save(cancelledTransaction);
            return mapper.toResponse(cancelledTransaction);
        }

        Transaction transaction = Transaction.builder()
            .lender(lender)
            .transactionDate(LocalDateTime.now())
            .transactionValue(request.transactionValue())
            .transactionType(request.transactionType())
            .transactionPaymentType(request.transactionPaymentType())
            .build();

        transaction = transactionRepository.save(transaction);
        return mapper.toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionQueryResponse query(UUID lenderId, String startDate, String endDate,
                                          BigDecimal minValue, BigDecimal maxValue, String type) {
        log.info("TransactionService::query(lenderId={})", lenderId);

        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));

        LocalDateTime startDateTime = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.parse(endDate) : null;
        TransactionType transactionType = type != null ? TransactionType.valueOf(type) : null;

        List<Transaction> transactions = transactionRepository.findByFilters(
            lenderId, startDateTime, endDateTime, minValue, maxValue, transactionType);

        List<TransactionItem> items = transactions.stream()
            .map(t -> new TransactionItem(
                t.getTransactionDate(),
                t.getTransactionValue(),
                t.getTransactionType()))
            .toList();

        return new TransactionQueryResponse(
            transactions.size(),
            lender.getName(),
            LocalDateTime.now(),
            items);
    }

    @Transactional(readOnly = true)
    public HistoryQueryResponse queryHistory(UUID lenderId, String startDate, String endDate,
                                             BigDecimal minValue, BigDecimal maxValue, String type) {
        log.info("TransactionService::queryHistory(lenderId={})", lenderId);

        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));

        LocalDateTime startLocalDateTime = startDate != null ? LocalDateTime.parse(startDate + "T00:00:00") : null;
        LocalDateTime endLocalDateTime = endDate != null ? LocalDateTime.parse(endDate + "T23:59:59") : null;
        HistoryType historyType = type != null ? HistoryType.valueOf(type) : null;

        List<TransactionHistory> historyList = transactionHistoryRepository.findByFilters(
            lenderId, startLocalDateTime, endLocalDateTime, minValue, maxValue, historyType);

        List<HistoryItem> items = historyList.stream()
            .map(h -> new HistoryItem(
                h.getHistoryDate().toLocalDate().toString(),
                h.getTransactionValue(),
                h.getHistoryType().name()))
            .toList();

        return new HistoryQueryResponse(
            lender.getName(),
            LocalDateTime.now().toString(),
            items);
    }
}