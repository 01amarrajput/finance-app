package com.finance.dto;

import com.finance.entity.FinancialRecord;
import com.finance.entity.FinancialRecord.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecordResponse {
    private Long          id;
    private BigDecimal    amount;
    private RecordType    type;
    private String        category;
    private LocalDate     date;
    private String        notes;
    private Long          createdById;
    private String        createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecordResponse() {}

    public static RecordResponse from(FinancialRecord r) {
        RecordResponse res = new RecordResponse();
        res.id            = r.getId();
        res.amount        = r.getAmount();
        res.type          = r.getType();
        res.category      = r.getCategory();
        res.date          = r.getDate();
        res.notes         = r.getNotes();
        res.createdById   = r.getCreatedBy().getId();
        res.createdByName = r.getCreatedBy().getName();
        res.createdAt     = r.getCreatedAt();
        res.updatedAt     = r.getUpdatedAt();
        return res;
    }

    public Long          getId()            { return id; }
    public BigDecimal    getAmount()        { return amount; }
    public RecordType    getType()          { return type; }
    public String        getCategory()      { return category; }
    public LocalDate     getDate()          { return date; }
    public String        getNotes()         { return notes; }
    public Long          getCreatedById()   { return createdById; }
    public String        getCreatedByName() { return createdByName; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
}
