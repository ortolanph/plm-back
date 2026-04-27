package pt.pauloortolan.plm_back.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
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
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final LenderRepository lenderRepository;
    private final Configuration freemarkerConfig;

    public ReportService(TransactionRepository transactionRepository, TransactionHistoryRepository transactionHistoryRepository, LenderRepository lenderRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.lenderRepository = lenderRepository;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_34);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "templates");
    }

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

    @Transactional(readOnly = true)
    public byte[] generateExcelReport() {
        log.info("ReportService::generateExcelReport()");

        List<Lender> lenders = new ArrayList<>(lenderRepository.findAll());
        if (lenders.isEmpty()) {
            throw new IllegalStateException("No lenders found");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleStyle.setFont(titleFont);

            for (Lender lender : lenders) {
                String safeSheetName = WorkbookUtil.createSafeSheetName(lender.getName());
                Sheet sheet = workbook.createSheet(safeSheetName);

                int rowNum = 0;

                Row titleRow = sheet.createRow(rowNum++);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("Personal Load Manager");
                titleCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

                rowNum++;

                Row basicDataHeader = sheet.createRow(rowNum++);
                Cell basicDataCell = basicDataHeader.createCell(0);
                basicDataCell.setCellValue("Basic Data");
                basicDataCell.setCellStyle(headerStyle);

                Row basicDataRow = sheet.createRow(rowNum++);
                basicDataRow.createCell(0).setCellValue("Lender");
                basicDataRow.createCell(1).setCellValue(lender.getName() != null ? lender.getName() : "");
                basicDataRow.createCell(2).setCellValue("Phone");
                basicDataRow.createCell(3).setCellValue(lender.getPhone() != null ? lender.getPhone() : "");

                Row bankDataRow = sheet.createRow(rowNum++);
                bankDataRow.createCell(0).setCellValue("Bank Data");
                bankDataRow.createCell(1).setCellValue(lender.getBankData() != null ? lender.getBankData() : "");
                bankDataRow.createCell(2).setCellValue("Address");
                bankDataRow.createCell(3).setCellValue(lender.getAddress() != null ? lender.getAddress() : "");

                rowNum++;

                List<Transaction> transactions = new ArrayList<>(transactionRepository.findByLenderId(lender.getId()));
                transactions.sort(Comparator.comparing(Transaction::getTransactionDate));

                Row transactionHeader = sheet.createRow(rowNum++);
                String[] txHeaders = {"Transaction", "Date", "Value", "Type", "Payment Type"};
                for (int i = 0; i < txHeaders.length; i++) {
                    Cell cell = transactionHeader.createCell(i);
                    cell.setCellValue(txHeaders[i]);
                    cell.setCellStyle(headerStyle);
                }

                for (Transaction tx : transactions) {
                    Row txRow = sheet.createRow(rowNum++);
                    txRow.createCell(0).setCellValue("Transaction");
                    txRow.createCell(1).setCellValue(tx.getTransactionDate() != null ? tx.getTransactionDate().format(DATE_FORMATTER) : "");
                    txRow.createCell(2).setCellValue(tx.getTransactionValue() != null ? BRAZILIAN_NUMBER_FORMAT.format(tx.getTransactionValue()) : "");
                    txRow.createCell(3).setCellValue(mapTransactionTypeToCsv(tx.getTransactionType()));
                    txRow.createCell(4).setCellValue(tx.getTransactionPaymentType() != null ? tx.getTransactionPaymentType().name() : "");
                }

                rowNum++;

                List<TransactionHistory> historyList = new ArrayList<>(transactionHistoryRepository.findByLenderId(lender.getId()));
                historyList.sort(Comparator.comparing(TransactionHistory::getHistoryDate));

                Row historyHeader = sheet.createRow(rowNum++);
                String[] histHeaders = {"History", "Date", "Value", "Type", "Reason"};
                for (int i = 0; i < histHeaders.length; i++) {
                    Cell cell = historyHeader.createCell(i);
                    cell.setCellValue(histHeaders[i]);
                    cell.setCellStyle(headerStyle);
                }

                for (TransactionHistory h : historyList) {
                    Row histRow = sheet.createRow(rowNum++);
                    histRow.createCell(0).setCellValue("History");
                    histRow.createCell(1).setCellValue(h.getHistoryDate() != null ? h.getHistoryDate().format(DATE_FORMATTER) : "");
                    histRow.createCell(2).setCellValue(h.getTransactionValue() != null ? BRAZILIAN_NUMBER_FORMAT.format(h.getTransactionValue()) : "");
                    histRow.createCell(3).setCellValue(mapTransactionTypeToCsv(h.getTransactionType()));
                    histRow.createCell(4).setCellValue(h.getHistoryType() != null ? h.getHistoryType().name() : "");
                }

                for (int i = 0; i < 5; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateOdsReport() {
        log.info("ReportService::generateOdsReport()");
        return generateExcelReport();
    }

    public String getExcelFileName() {
        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        return "personal_load_manager_" + timestamp + ".xlsx";
    }

    public String getOdsFileName() {
        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        return "personal_load_manager_" + timestamp + ".ods";
    }

    @Transactional(readOnly = true)
    public byte[] generatePdfReport() {
        log.info("ReportService::generatePdfReport()");
        return generatePdfReportFromTemplate("report.ftl");
    }

    private byte[] generatePdfReportFromTemplate(String templateName) {
        try {
            Map<String, Object> data = prepareReportData();
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(
                freemarkerConfig.getTemplate(templateName), data);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateDocxReport() {
        log.info("ReportService::generateDocxReport()");
        return generateDocxReportFromTemplate("report.ftl");
    }

    private byte[] generateDocxReportFromTemplate(String templateName) {
        try {
            Map<String, Object> data = prepareReportData();
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(
                freemarkerConfig.getTemplate(templateName), data);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.itextpdf.html2pdf.HtmlConverter.convertToPdf(htmlContent,
                new com.itextpdf.kernel.pdf.PdfWriter(baos));
            byte[] pdfBytes = baos.toByteArray();

            try (var docxOut = new ByteArrayOutputStream()) {
                var document = new org.apache.poi.xwpf.usermodel.XWPFDocument();
                document.createParagraph().createRun().setText("Converted from PDF");
                document.write(docxOut);
                return docxOut.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate DOCX report", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateOdtReport() {
        log.info("ReportService::generateOdtReport()");
        return generateOdtReportFromTemplate("report.ftl");
    }

    private byte[] generateOdtReportFromTemplate(String templateName) {
        try {
            Map<String, Object> data = prepareReportData();
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(
                freemarkerConfig.getTemplate(templateName), data);

            String odtContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<office:document-content xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\">" +
                "<office:body><office:text><text:p>" + htmlContent.replace("<", "&lt;").replace(">", "&gt;") +
                "</text:p></office:text></office:body></office:document-content>";

            return odtContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ODT report", e);
        }
    }

    private Map<String, Object> prepareReportData() {
        List<Lender> lenders = new ArrayList<>(lenderRepository.findAll());
        List<Map<String, Object>> lenderData = new ArrayList<>();

        for (Lender lender : lenders) {
            Map<String, Object> lenderMap = new HashMap<>();
            lenderMap.put("name", lender.getName());
            lenderMap.put("phone", lender.getPhone());
            lenderMap.put("bankData", lender.getBankData());
            lenderMap.put("address", lender.getAddress());

            List<Transaction> transactions = new ArrayList<>(transactionRepository.findByLenderId(lender.getId()));
            transactions.sort(Comparator.comparing(Transaction::getTransactionDate));
            List<Map<String, Object>> txList = new ArrayList<>();
            for (Transaction tx : transactions) {
                Map<String, Object> txMap = new HashMap<>();
                txMap.put("transactionDate", tx.getTransactionDate() != null ? tx.getTransactionDate().format(DATE_FORMATTER) : "");
                txMap.put("transactionValue", tx.getTransactionValue());
                txMap.put("transactionType", tx.getTransactionType());
                txMap.put("transactionPaymentType", tx.getTransactionPaymentType());
                txList.add(txMap);
            }
            lenderMap.put("transactions", txList);

            List<TransactionHistory> history = new ArrayList<>(transactionHistoryRepository.findByLenderId(lender.getId()));
            history.sort(Comparator.comparing(TransactionHistory::getHistoryDate));
            List<Map<String, Object>> histList = new ArrayList<>();
            for (TransactionHistory h : history) {
                Map<String, Object> hMap = new HashMap<>();
                hMap.put("historyDate", h.getHistoryDate() != null ? h.getHistoryDate().format(DATE_FORMATTER) : "");
                hMap.put("transactionDate", h.getTransactionDate() != null ? h.getTransactionDate().format(DATE_FORMATTER) : "");
                hMap.put("transactionValue", h.getTransactionValue());
                hMap.put("transactionType", h.getTransactionType());
                hMap.put("historyType", h.getHistoryType());
                histList.add(hMap);
            }
            lenderMap.put("history", histList);

            lenderData.add(lenderMap);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("lenders", lenderData);
        data.put("reportDate", LocalDate.now().format(DATE_FORMATTER));
        return data;
    }

    public String getPdfFileName() {
        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        return "personal_load_manager_" + timestamp + ".pdf";
    }

    public String getDocxFileName() {
        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        return "personal_load_manager_" + timestamp + ".docx";
    }

    public String getOdtFileName() {
        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
        return "personal_load_manager_" + timestamp + ".odt";
    }
}