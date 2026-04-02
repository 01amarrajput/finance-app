package com.finance.service;

import com.finance.dto.*;
import com.finance.entity.*;
import com.finance.entity.FinancialRecord.RecordType;
import com.finance.exception.*;
import com.finance.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RecordService {

    private final FinancialRecordRepository recordRepository;

    public RecordService(FinancialRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public Page<RecordResponse> getRecords(
            RecordType type, String category,
            LocalDate dateFrom, LocalDate dateTo,
            int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return recordRepository.findAll(
                RecordSpecification.filter(type, category, dateFrom, dateTo),
                pageable).map(RecordResponse::from);
    }

    public RecordResponse getById(Long id) {
        return RecordResponse.from(findOrThrow(id));
    }

    @Transactional
    public RecordResponse create(RecordRequest req, User creator) {
        FinancialRecord record = FinancialRecord.builder()
                .amount(req.getAmount())
                .type(req.getType())
                .category(req.getCategory().trim())
                .date(req.getDate())
                .notes(req.getNotes())
                .createdBy(creator)
                .build();
        return RecordResponse.from(recordRepository.save(record));
    }

    @Transactional
    public RecordResponse update(Long id, UpdateRecordRequest req) {
        FinancialRecord record = findOrThrow(id);

        if (req.getAmount()   != null) record.setAmount(req.getAmount());
        if (req.getType()     != null) record.setType(req.getType());
        if (req.getCategory() != null) record.setCategory(req.getCategory().trim());
        if (req.getDate()     != null) record.setDate(req.getDate());
        if (req.getNotes()    != null) record.setNotes(req.getNotes());

        return RecordResponse.from(recordRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        FinancialRecord record = findOrThrow(id);
        record.setDeleted(true);
        recordRepository.save(record);
    }

    private FinancialRecord findOrThrow(Long id) {
        return recordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));
    }
}
