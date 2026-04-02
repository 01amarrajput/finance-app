package com.finance.dto;

import com.finance.entity.FinancialRecord.RecordType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecordRequest {
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    @NotNull                     private RecordType type;
    @NotBlank @Size(max=60)      private String category;
    @NotNull                     private LocalDate date;
    @Size(max=500)               private String notes;

    public BigDecimal  getAmount()   { return amount; }
    public RecordType  getType()     { return type; }
    public String      getCategory() { return category; }
    public LocalDate   getDate()     { return date; }
    public String      getNotes()    { return notes; }
    public void setAmount(BigDecimal amount)    { this.amount   = amount; }
    public void setType(RecordType type)        { this.type     = type; }
    public void setCategory(String category)    { this.category = category; }
    public void setDate(LocalDate date)         { this.date     = date; }
    public void setNotes(String notes)          { this.notes    = notes; }
}
