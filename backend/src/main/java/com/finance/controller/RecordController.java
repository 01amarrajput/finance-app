package com.finance.controller;

import com.finance.dto.*;
import com.finance.entity.*;
import com.finance.entity.FinancialRecord.RecordType;
import com.finance.service.RecordService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<Page<RecordResponse>> listRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date_from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date_to,
            @RequestParam(defaultValue = "0")    int    page,
            @RequestParam(defaultValue = "20")   int    size,
            @RequestParam(defaultValue = "date") String sort_by,
            @RequestParam(defaultValue = "desc") String sort_dir) {
        return ResponseEntity.ok(
                recordService.getRecords(type, category, date_from, date_to, page, size, sort_by, sort_dir));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<RecordResponse> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponse> createRecord(
            @Valid @RequestBody RecordRequest req,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.create(req, currentUser));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecordRequest req) {
        return ResponseEntity.ok(recordService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
