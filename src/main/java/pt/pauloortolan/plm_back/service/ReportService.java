package pt.pauloortolan.plm_back.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.pauloortolan.plm_back.model.*;
import pt.pauloortolan.plm_back.repository.*;

import java.io.*;
import java.math.*;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final LenderRepository lenderRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final NumberFormat BRAZILIAN_NUMBER_FORMAT = new java.text.DecimalFormat("#,##0.00", new java.text.DecimalFormatSymbols(new Locale("pt", "BR")));

    @Transactional(readOnly = true)
    public byte[] generateTransactionCsv(UUID lenderId) {
        log.info("ReportService::generateTransactionCsv(lenderId={})", lenderId);

        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));

        List<Transaction> transactions = new ArrayList<>(transactionRepository.findByLenderId(lenderId));
        transactions.sort(Comparator.comparing(Transaction::getTransactionDate));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            csvWriter.writeNext(new String[]{"lender_name", "transaction_date", "transaction_type", "transaction_value", "transaction_payment_type"});

            for (Transaction t : transactions) {
                csvWriter.writeNext(new String[]{
                    lender.getName(),
                    t.getTransactionDate().format(DATE_FORMATTER),
                    mapTransactionTypeToCsv(t.getTransactionType()),
                    BRAZILIAN_NUMBER_FORMAT.format(t.getTransactionValue()),
                    t.getTransactionPaymentType() != null ? t.getTransactionPaymentType().name() : ""
                });
            }

            csvWriter.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate transaction CSV", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateHistoryCsv(UUID lenderId) {
        log.info("ReportService::generateHistoryCsv(lenderId={})", lenderId);

        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));

        List<TransactionHistory> historyList = new ArrayList<>(transactionHistoryRepository.findByLenderId(lenderId));
        historyList.sort(Comparator.comparing(TransactionHistory::getHistoryDate));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            csvWriter.writeNext(new String[]{"lender_name", "history_date", "transaction_date", "transaction_type", "transaction_value", "transaction_payment_type", "history_type"});

            for (TransactionHistory h : historyList) {
                csvWriter.writeNext(new String[]{
                    lender.getName(),
                    h.getHistoryDate().format(DATE_FORMATTER),
                    h.getTransactionDate() != null ? h.getTransactionDate().format(DATE_FORMATTER) : "",
                    mapTransactionTypeToCsv(h.getTransactionType()),
                    h.getTransactionValue() != null ? BRAZILIAN_NUMBER_FORMAT.format(h.getTransactionValue()) : "",
                    h.getTransactionPaymentType() != null ? h.getTransactionPaymentType().name() : "",
                    h.getHistoryType() != null ? h.getHistoryType().name() : ""
                });
            }

            csvWriter.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate history CSV", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateZipReport(UUID lenderId) {
        log.info("ReportService::generateZipReport(lenderId={})", lenderId);

        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));

        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        String safeName = lender.getName().replaceAll("[^a-zA-Z0-9]", "_");

        byte[] transactionCsv = generateTransactionCsv(lenderId);
        byte[] historyCsv = generateHistoryCsv(lenderId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            ZipEntry transactionEntry = new ZipEntry("transactions_" + timestamp + "_" + safeName + ".csv");
            zos.putNextEntry(transactionEntry);
            zos.write(transactionCsv);
            zos.closeEntry();

            ZipEntry historyEntry = new ZipEntry("history_" + timestamp + "_" + safeName + ".csv");
            zos.putNextEntry(historyEntry);
            zos.write(historyCsv);
            zos.closeEntry();

            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate ZIP report", e);
        }
    }

    public String getTransactionFileName(UUID lenderId) {
        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));
        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        String safeName = lender.getName().replaceAll("[^a-zA-Z0-9]", "_");
        return "transactions_" + timestamp + "_" + safeName + ".csv";
    }

    public String getHistoryFileName(UUID lenderId) {
        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));
        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        String safeName = lender.getName().replaceAll("[^a-zA-Z0-9]", "_");
        return "history_" + timestamp + "_" + safeName + ".csv";
    }

    public String getZipFileName(UUID lenderId) {
        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + lenderId));
        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        String safeName = lender.getName().replaceAll("[^a-zA-Z0-9]", "_");
        return "plm_csv_reports_" + timestamp + "_" + safeName + ".zip";
    }

    private String mapTransactionTypeToCsv(TransactionType type) {
        if (type == null) return "";
        return switch (type) {
            case BORROWED -> "BORROWED";
            case PAYMENT -> "PAYED";
            case CANCELLED -> "CANCELLED";
        };
    }
}