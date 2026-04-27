package pt.pauloortolan.plm_back.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pauloortolan.plm_back.model.*;
import pt.pauloortolan.plm_back.repository.LenderRepository;
import pt.pauloortolan.plm_back.repository.TransactionHistoryRepository;
import pt.pauloortolan.plm_back.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private LenderRepository lenderRepository;

    @InjectMocks
    private ReportService reportService;

    private UUID lenderId;
    private Lender lender;
    private Transaction transaction1;
    private Transaction transaction2;
    private TransactionHistory history1;

    @BeforeEach
    void setUp() {
        lenderId = UUID.randomUUID();
        
        lender = Lender.builder()
            .id(lenderId)
            .name("John Doe")
            .phone("+123456789")
            .bankData("IBAN: PT000000")
            .address("123 Main St")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        transaction1 = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.of(2024, 1, 15, 10, 0))
            .transactionValue(new BigDecimal("100.50"))
            .transactionType(TransactionType.BORROWED)
            .transactionPaymentType(TransactionPaymentType.MONEY)
            .build();

        transaction2 = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.of(2024, 2, 20, 14, 30))
            .transactionValue(new BigDecimal("50.00"))
            .transactionType(TransactionType.PAYMENT)
            .transactionPaymentType(TransactionPaymentType.WIRE_TRANSACTION)
            .build();

        history1 = TransactionHistory.builder()
            .id(UUID.randomUUID())
            .historyDate(LocalDateTime.of(2024, 3, 10, 9, 0))
            .lenderName("John Doe")
            .transactionDate(LocalDateTime.of(2024, 1, 15, 10, 0))
            .transactionValue(new BigDecimal("100.50"))
            .transactionType(TransactionType.BORROWED)
            .transactionPaymentType(TransactionPaymentType.MONEY)
            .historyType(HistoryType.PAID_IN_FULL)
            .lenderId(lenderId)
            .build();
    }

    @Test
    void generateTransactionCsv_shouldReturnCorrectCsvFormat() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1, transaction2));

        byte[] csvContent = reportService.generateTransactionCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");

        String[] lines = csv.split("\n");
        
        assertTrue(lines[0].contains("lender_name"), "Header should contain lender_name");
        assertTrue(lines[1].contains("John Doe"));
        assertTrue(lines[1].contains("15/01/2024"));
        assertTrue(lines[1].contains("BORROWED"));
        assertTrue(lines[1].contains("100,50"));
        assertTrue(lines[1].contains("MONEY"));
        
        assertTrue(lines[2].contains("John Doe"));
        assertTrue(lines[2].contains("20/02/2024"));
        assertTrue(lines[2].contains("PAYED"));
        assertTrue(lines[2].contains("50,00"));
        assertTrue(lines[2].contains("WIRE_TRANSACTION"));
    }

    @Test
    void generateTransactionCsv_shouldSortByTransactionDateOldestToNewest() {
        Transaction olderTransaction = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.of(2023, 12, 1, 8, 0))
            .transactionValue(new BigDecimal("200.00"))
            .transactionType(TransactionType.BORROWED)
            .transactionPaymentType(TransactionPaymentType.MONEY)
            .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1, transaction2, olderTransaction));

        byte[] csvContent = reportService.generateTransactionCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");

        String[] lines = csv.split("\n");
        
        assertTrue(lines[0].contains("lender_name"), "Header should contain lender_name");
        assertTrue(lines[1].contains("01/12/2023"));
        assertTrue(lines[2].contains("15/01/2024"));
        assertTrue(lines[3].contains("20/02/2024"));
    }

    @Test
    void generateHistoryCsv_shouldSortByHistoryDateOldestToNewest() {
        TransactionHistory olderHistory = TransactionHistory.builder()
            .id(UUID.randomUUID())
            .historyDate(LocalDateTime.of(2023, 6, 1, 10, 0))
            .lenderName("John Doe")
            .transactionDate(LocalDateTime.of(2023, 5, 15, 10, 0))
            .transactionValue(new BigDecimal("300.00"))
            .transactionType(TransactionType.BORROWED)
            .historyType(HistoryType.FORGIVEN)
            .lenderId(lenderId)
            .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of(history1, olderHistory));

        byte[] csvContent = reportService.generateHistoryCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");
        
        String[] lines = csv.split("\n");
        
        assertTrue(lines[1].contains("01/06/2023"));
        assertTrue(lines[2].contains("10/03/2024"));
    }

    @Test
    void generateHistoryCsv_shouldThrowExceptionWhenLenderNotFound() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reportService.generateHistoryCsv(lenderId));
    }

    @Test
    void generateZipReport_shouldContainBothCsvFiles() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of(history1));

        byte[] zipContent = reportService.generateZipReport(lenderId);

        assertNotNull(zipContent);
        assertTrue(zipContent.length > 0);
        
        try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(zipContent))) {
            java.util.zip.ZipEntry entry;
            boolean hasTransactions = false;
            boolean hasHistory = false;
            
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("transactions_")) {
                    hasTransactions = true;
                }
                if (entry.getName().contains("history_")) {
                    hasHistory = true;
                }
                zis.closeEntry();
            }
            
            assertTrue(hasTransactions, "ZIP should contain transactions CSV");
            assertTrue(hasHistory, "ZIP should contain history CSV");
        } catch (Exception e) {
            fail("Failed to read ZIP content: " + e.getMessage());
        }
    }

    @Test
    void generateZipReport_shouldThrowExceptionWhenLenderNotFound() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reportService.generateZipReport(lenderId));
    }

    @Test
    void getTransactionFileName_shouldReturnCorrectFormat() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));

        String fileName = reportService.getTransactionFileName(lenderId);

        assertTrue(fileName.startsWith("transactions_"));
        assertTrue(fileName.endsWith("_John_Doe.csv"));
    }

    @Test
    void getHistoryFileName_shouldReturnCorrectFormat() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));

        String fileName = reportService.getHistoryFileName(lenderId);

        assertTrue(fileName.startsWith("history_"));
        assertTrue(fileName.endsWith("_John_Doe.csv"));
    }

    @Test
    void getZipFileName_shouldReturnCorrectFormat() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));

        String fileName = reportService.getZipFileName(lenderId);

        assertTrue(fileName.startsWith("plm_csv_reports_"));
        assertTrue(fileName.endsWith("_John_Doe.zip"));
    }

    @Test
    void generateTransactionCsv_shouldHandleEmptyTransactions() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of());

        byte[] csvContent = reportService.generateTransactionCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");

        String[] lines = csv.split("\n");
        
        assertTrue(lines[0].contains("lender_name"), "Header should contain lender_name");
        assertTrue(lines[0].contains("transaction_date"), "Header should contain transaction_date");
        assertTrue(lines.length >= 1, "Should have at least header line");
    }

    @Test
    void generateHistoryCsv_shouldHandleEmptyHistory() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of());

        byte[] csvContent = reportService.generateHistoryCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");

        String[] lines = csv.split("\n");
        
        assertTrue(lines[0].contains("lender_name"), "Header should contain lender_name");
        assertTrue(lines[0].contains("history_date"), "Header should contain history_date");
        assertTrue(lines.length >= 1, "Should have at least header line");
    }

    @Test
    void generateTransactionCsv_shouldHandleSpecialCharactersInLenderName() {
        Lender specialLender = Lender.builder()
            .id(lenderId)
            .name("José da Silva, Jr.")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(specialLender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1));

        byte[] csvContent = reportService.generateTransactionCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");

        assertTrue(csv.contains("\"José da Silva, Jr.\""));
    }

    @Test
    void generateTransactionCsv_shouldMapPaymentToPayed() {
        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction2));

        byte[] csvContent = reportService.generateTransactionCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");

        assertTrue(csv.contains("PAYED"));
        assertFalse(csv.contains("PAYMENT"));
    }

    @Test
    void generateTransactionCsv_shouldHandleNullPaymentType() {
        Transaction transactionWithoutPaymentType = Transaction.builder()
            .id(UUID.randomUUID())
            .lender(lender)
            .transactionDate(LocalDateTime.of(2024, 1, 15, 10, 0))
            .transactionValue(new BigDecimal("100.00"))
            .transactionType(TransactionType.BORROWED)
            .transactionPaymentType(null)
            .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transactionWithoutPaymentType));

        byte[] csvContent = reportService.generateTransactionCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");

        assertTrue(csv.contains("BORROWED"));
    }

    @Test
    void generateHistoryCsv_shouldHandleNullFields() {
        TransactionHistory historyWithNulls = TransactionHistory.builder()
            .id(UUID.randomUUID())
            .historyDate(LocalDateTime.of(2024, 3, 10, 9, 0))
            .lenderName("John Doe")
            .transactionDate(null)
            .transactionValue(null)
            .transactionType(TransactionType.BORROWED)
            .transactionPaymentType(null)
            .historyType(HistoryType.PAID_IN_FULL)
            .lenderId(lenderId)
            .build();

        when(lenderRepository.findById(lenderId)).thenReturn(Optional.of(lender));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of(historyWithNulls));

        byte[] csvContent = reportService.generateHistoryCsv(lenderId);
        String csv = new String(csvContent).replace("\r\n", "\n").replace("\r", "");

        String[] lines = csv.split("\n");
        assertTrue(lines[1].contains("BORROWED"));
        assertTrue(lines[1].contains("PAID_IN_FULL"));
    }

    @Test
    void generateExcelReport_shouldReturnValidExcelFile() {
        when(lenderRepository.findAll()).thenReturn(List.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of(history1));

        byte[] excelContent = reportService.generateExcelReport();

        assertNotNull(excelContent);
        assertTrue(excelContent.length > 0);
        assertTrue(excelContent[0] == 0x50);
        assertTrue(excelContent[1] == 0x4B);
    }

    @Test
    void generateExcelReport_shouldThrowExceptionWhenNoLenders() {
        when(lenderRepository.findAll()).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> reportService.generateExcelReport());
    }

    @Test
    void generateOdsReport_shouldReturnValidExcelSpreadsheet() {
        when(lenderRepository.findAll()).thenReturn(List.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of(history1));

        byte[] odsContent = reportService.generateOdsReport();

        assertNotNull(odsContent);
        assertTrue(odsContent.length > 0);
    }

    @Test
    void getExcelFileName_shouldReturnCorrectFormat() {
        String fileName = reportService.getExcelFileName();

        assertTrue(fileName.startsWith("personal_load_manager_"));
        assertTrue(fileName.endsWith(".xlsx"));
    }

    @Test
    void getOdsFileName_shouldReturnCorrectFormat() {
        String fileName = reportService.getOdsFileName();

        assertTrue(fileName.startsWith("personal_load_manager_"));
        assertTrue(fileName.endsWith(".ods"));
    }

    @Test
    void getPdfFileName_shouldReturnCorrectFormat() {
        String fileName = reportService.getPdfFileName();

        assertTrue(fileName.startsWith("personal_load_manager_"));
        assertTrue(fileName.endsWith(".pdf"));
    }

    @Test
    void getDocxFileName_shouldReturnCorrectFormat() {
        String fileName = reportService.getDocxFileName();

        assertTrue(fileName.startsWith("personal_load_manager_"));
        assertTrue(fileName.endsWith(".docx"));
    }

    @Test
    void getOdtFileName_shouldReturnCorrectFormat() {
        String fileName = reportService.getOdtFileName();

        assertTrue(fileName.startsWith("personal_load_manager_"));
        assertTrue(fileName.endsWith(".odt"));
    }

    @Test
    void generatePdfReport_shouldReturnValidPdf() {
        when(lenderRepository.findAll()).thenReturn(List.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of(history1));

        byte[] pdfContent = reportService.generatePdfReport();

        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);
        assertTrue(pdfContent[0] == 0x25);
        assertTrue(pdfContent[1] == 0x50);
    }

    @Test
    void generateDocxReport_shouldReturnValidDocx() {
        when(lenderRepository.findAll()).thenReturn(List.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of(history1));

        byte[] docxContent = reportService.generateDocxReport();

        assertNotNull(docxContent);
        assertTrue(docxContent.length > 0);
    }

    @Test
    void generateOdtReport_shouldReturnValidOdt() {
        when(lenderRepository.findAll()).thenReturn(List.of(lender));
        when(transactionRepository.findByLenderId(lenderId)).thenReturn(List.of(transaction1));
        when(transactionHistoryRepository.findByLenderId(lenderId)).thenReturn(List.of(history1));

        byte[] odtContent = reportService.generateOdtReport();

        assertNotNull(odtContent);
        assertTrue(odtContent.length > 0);
    }
}