package com.finance.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_records",
       indexes = {
           @Index(name = "idx_rec_date",     columnList = "date"),
           @Index(name = "idx_rec_type",     columnList = "type"),
           @Index(name = "idx_rec_category", columnList = "category"),
           @Index(name = "idx_rec_deleted",  columnList = "deleted")
       })
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public FinancialRecord() {}

    private FinancialRecord(Builder b) {
        this.amount    = b.amount;
        this.type      = b.type;
        this.category  = b.category;
        this.date      = b.date;
        this.notes     = b.notes;
        this.createdBy = b.createdBy;
        this.deleted   = false;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    // ── Builder ───────────────────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal amount;
        private RecordType type;
        private String category, notes;
        private LocalDate date;
        private User createdBy;

        public Builder amount(BigDecimal v)   { this.amount    = v; return this; }
        public Builder type(RecordType v)     { this.type      = v; return this; }
        public Builder category(String v)     { this.category  = v; return this; }
        public Builder date(LocalDate v)      { this.date      = v; return this; }
        public Builder notes(String v)        { this.notes     = v; return this; }
        public Builder createdBy(User v)      { this.createdBy = v; return this; }
        public FinancialRecord build()        { return new FinancialRecord(this); }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public Long          getId()        { return id; }
    public BigDecimal    getAmount()    { return amount; }
    public RecordType    getType()      { return type; }
    public String        getCategory()  { return category; }
    public LocalDate     getDate()      { return date; }
    public String        getNotes()     { return notes; }
    public boolean       isDeleted()    { return deleted; }
    public User          getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setAmount(BigDecimal amount)     { this.amount   = amount; }
    public void setType(RecordType type)         { this.type     = type; }
    public void setCategory(String category)     { this.category = category; }
    public void setDate(LocalDate date)          { this.date     = date; }
    public void setNotes(String notes)           { this.notes    = notes; }
    public void setDeleted(boolean deleted)      { this.deleted  = deleted; }

    public enum RecordType { INCOME, EXPENSE }
}
