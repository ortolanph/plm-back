package pt.pauloortolan.plm_back.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.mapper.TransactionMapper;
import pt.pauloortolan.plm_back.model.*;
import pt.pauloortolan.plm_back.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final LenderRepository lenderRepository;
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
}