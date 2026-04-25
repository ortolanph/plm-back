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
public class LenderService {

    private final LenderRepository repository;
    private final TransactionRepository transactionRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final LenderMapper mapper;

    @Transactional
    public LenderResponse create(CreateLenderRequest request) {
        log.info("LenderService::create(name={})", request.name());
        Lender lender = mapper.toEntity(request);
        lender = repository.save(lender);
        return mapper.toResponse(lender);
    }

    @Transactional
    public LenderResponse update(UUID id, UpdateLenderRequest request) {
        log.info("LenderService::update(id={})", id);
        Lender lender = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + id));

        if (request.name() != null) lender.setName(request.name());
        if (request.phone() != null) lender.setPhone(request.phone());
        if (request.bankData() != null) lender.setBankData(request.bankData());
        if (request.address() != null) lender.setAddress(request.address());

        lender = repository.save(lender);
        return mapper.toResponse(lender);
    }

    @Transactional(readOnly = true)
    public List<LenderResponse> query(String name, String phone) {
        log.info("LenderService::query(name={}, phone={})", name, phone);

        if (name != null && phone != null) {
            return repository.findByFilters(name, phone).stream()
                .map(mapper::toResponse)
                .toList();
        }
        if (name != null) {
            return repository.findByNameContaining(name).stream()
                .map(mapper::toResponse)
                .toList();
        }
        if (phone != null) {
            return repository.findByPhoneContaining(phone).stream()
                .map(mapper::toResponse)
                .toList();
        }
        return repository.findAll().stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public LenderResponse getById(UUID id) {
        return repository.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + id));
    }

    @Transactional
    public void settleLender(SettleLenderRequest request) {
        log.info("LenderService::settleLender(lenderId={}, settlementType={})",
            request.lenderId(), request.settlementType());

        Lender lender = repository.findById(request.lenderId())
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + request.lenderId()));

        List<Transaction> transactions = transactionRepository.findByLenderId(request.lenderId());

        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getTransactionType() == TransactionType.BORROWED) {
                total = total.add(t.getTransactionValue());
            } else if (t.getTransactionType() == TransactionType.PAYMENT) {
                total = total.subtract(t.getTransactionValue());
            }

            TransactionHistory history = TransactionHistory.builder()
                .historyDate(LocalDateTime.now())
                .lenderName(lender.getName())
                .lenderPhone(lender.getPhone())
                .lenderBankData(lender.getBankData())
                .lenderId(lender.getId())
                .transactionDate(t.getTransactionDate())
                .transactionValue(t.getTransactionValue())
                .transactionType(t.getTransactionType())
                .transactionPaymentType(t.getTransactionPaymentType())
                .historyType(request.settlementType())
                .build();

            transactionHistoryRepository.save(history);
        }

        transactionRepository.deleteAll(transactions);

        TransactionHistory finalRecord = TransactionHistory.builder()
            .historyDate(LocalDateTime.now())
            .lenderName(lender.getName())
            .lenderPhone(lender.getPhone())
            .lenderBankData(lender.getBankData())
            .lenderId(lender.getId())
            .transactionDate(LocalDateTime.now())
            .transactionValue(total)
            .transactionType(TransactionType.BORROWED)
            .transactionPaymentType(request.paymentType())
            .historyType(request.settlementType())
            .build();

        transactionHistoryRepository.save(finalRecord);
    }

    @Transactional
    public void deleteLender(UUID id) {
        log.info("LenderService::deleteLender(id={})", id);

        Lender lender = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + id));

        List<Transaction> transactions = transactionRepository.findByLenderId(id);

        boolean hasOpenTransactions = transactions.stream()
            .anyMatch(t -> t.getTransactionType() == TransactionType.BORROWED);

        if (hasOpenTransactions) {
            throw new IllegalStateException("Cannot delete lender with open transactions");
        }

        repository.delete(lender);
    }

    @Transactional(readOnly = true)
    public LenderSummaryResponse getSummary(UUID lenderId) {
        log.info("LenderService::getSummary(lenderId={})", lenderId);

        Lender lender = repository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));

        List<Transaction> transactions = transactionRepository.findByLenderId(lenderId);
        List<TransactionHistory> historyList = transactionHistoryRepository.findAll();

        List<TransactionItem> transactionItems = transactions.stream()
            .map(t -> new TransactionItem(
                t.getTransactionDate(),
                t.getTransactionValue(),
                t.getTransactionType()))
            .toList();

        List<HistoryItem> historyItems = historyList.stream()
            .filter(h -> h.getLenderId().equals(lenderId))
            .map(h -> new HistoryItem(
                h.getHistoryDate().toLocalDate().toString(),
                h.getTransactionValue(),
                h.getHistoryType().name()))
            .toList();

        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getTransactionType() == TransactionType.BORROWED) {
                total = total.add(t.getTransactionValue());
            } else if (t.getTransactionType() == TransactionType.PAYMENT) {
                total = total.subtract(t.getTransactionValue());
            }
        }

        return new LenderSummaryResponse(
            total,
            lender.getName(),
            LocalDateTime.now().toString(),
            transactionItems,
            historyItems);
    }
}