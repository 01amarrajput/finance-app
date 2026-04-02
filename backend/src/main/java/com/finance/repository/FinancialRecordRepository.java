package com.finance.repository;

import com.finance.entity.FinancialRecord;
import com.finance.entity.FinancialRecord.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository
        extends JpaRepository<FinancialRecord, Long>,
                JpaSpecificationExecutor<FinancialRecord> {

    Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);

    // ── Dashboard aggregates ──────────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
           "WHERE r.deleted = false AND r.type = :type")
    BigDecimal sumByType(@Param("type") RecordType type);

    @Query("SELECT r.category, COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
           "WHERE r.deleted = false AND r.type = :type " +
           "GROUP BY r.category ORDER BY SUM(r.amount) DESC")
    List<Object[]> sumByCategory(@Param("type") RecordType type);

    // Monthly trend — H2 native
    @Query(value =
           "SELECT FORMATDATETIME(date, 'yyyy-MM') AS month, type, SUM(amount) AS total " +
           "FROM financial_records " +
           "WHERE deleted = false AND date >= :from " +
           "GROUP BY FORMATDATETIME(date, 'yyyy-MM'), type " +
           "ORDER BY month",
           nativeQuery = true)
    List<Object[]> monthlyTrendNative(@Param("from") LocalDate from);

    Page<FinancialRecord> findByDeletedFalseOrderByDateDescCreatedAtDesc(Pageable pageable);
}
