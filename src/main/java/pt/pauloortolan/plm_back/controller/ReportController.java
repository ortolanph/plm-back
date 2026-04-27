package pt.pauloortolan.plm_back.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pt.pauloortolan.plm_back.service.ReportService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/csv/transactions/{lenderId}")
    public ResponseEntity<byte[]> getTransactionCsv(@PathVariable UUID lenderId) {
        log.info("ReportController::getTransactionCsv(lenderId={})", lenderId);
        byte[] csvContent = reportService.generateTransactionCsv(lenderId);
        String fileName = reportService.getTransactionFileName(lenderId);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csvContent);
    }

    @GetMapping("/csv/history/{lenderId}")
    public ResponseEntity<byte[]> getHistoryCsv(@PathVariable UUID lenderId) {
        log.info("ReportController::getHistoryCsv(lenderId={})", lenderId);
        byte[] csvContent = reportService.generateHistoryCsv(lenderId);
        String fileName = reportService.getHistoryFileName(lenderId);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csvContent);
    }

    @GetMapping("/csv/all/{lenderId}")
    public ResponseEntity<byte[]> getAllReports(@PathVariable UUID lenderId) {
        log.info("ReportController::getAllReports(lenderId={})", lenderId);
        byte[] zipContent = reportService.generateZipReport(lenderId);
        String fileName = reportService.getZipFileName(lenderId);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.parseMediaType("application/zip"))
            .body(zipContent);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getPdfReport(
            @RequestParam(required = false, defaultValue = "false") Boolean word,
            @RequestParam(required = false, defaultValue = "false") Boolean opendoc) {
        log.info("ReportController::getPdfReport(word={}, opendoc={})", word, opendoc);
        
        byte[] content;
        String fileName;
        String contentType;
        
        if (Boolean.TRUE.equals(word)) {
            content = reportService.generateDocxReport();
            fileName = reportService.getDocxFileName();
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (Boolean.TRUE.equals(opendoc)) {
            content = reportService.generateOdtReport();
            fileName = reportService.getOdtFileName();
            contentType = "application/vnd.oasis.opendocument.text";
        } else {
            content = reportService.generatePdfReport();
            fileName = reportService.getPdfFileName();
            contentType = "application/pdf";
        }
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.parseMediaType(contentType))
            .body(content);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> getExcelReport(@RequestParam(required = false, defaultValue = "false") Boolean openDoc) {
        log.info("ReportController::getExcelReport(openDoc={})", openDoc);
        
        byte[] excelContent;
        String fileName;
        
        if (Boolean.TRUE.equals(openDoc)) {
            excelContent = reportService.generateOdsReport();
            fileName = reportService.getOdsFileName();
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet"))
                .body(excelContent);
        } else {
            excelContent = reportService.generateExcelReport();
            fileName = reportService.getExcelFileName();
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
        }
    }
}