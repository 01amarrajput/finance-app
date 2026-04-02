package com.finance.repository;

import com.finance.entity.FinancialRecord;
import com.finance.entity.FinancialRecord.RecordType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class RecordSpecification {

    private RecordSpecification() {}

    public static Specification<FinancialRecord> filter(
            RecordType type,
            String category,
            LocalDate dateFrom,
            LocalDate dateTo) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Never return soft-deleted records
            predicates.add(cb.isFalse(root.get("deleted")));

            if (type != null)
                predicates.add(cb.equal(root.get("type"), type));

            if (category != null && !category.isBlank())
                predicates.add(cb.like(
                        cb.lower(root.get("category")),
                        "%" + category.toLowerCase() + "%"));

            if (dateFrom != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), dateFrom));

            if (dateTo != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), dateTo));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
