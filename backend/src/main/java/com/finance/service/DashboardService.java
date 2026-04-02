package com.finance.service;

import com.finance.dto.*;
import com.finance.entity.FinancialRecord.RecordType;
import com.finance.repository.FinancialRecordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardService(FinancialRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public DashboardSummary getSummary() {
        BigDecimal totalIncome   = recordRepository.sumByType(RecordType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(RecordType.EXPENSE);
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> incomeByCategory  = toMap(recordRepository.sumByCategory(RecordType.INCOME));
        Map<String, BigDecimal> expenseByCategory = toMap(recordRepository.sumByCategory(RecordType.EXPENSE));

        List<RecordResponse> recent = recordRepository
                .findByDeletedFalseOrderByDateDescCreatedAtDesc(PageRequest.of(0, 10))
                .getContent().stream().map(RecordResponse::from).toList();

        LocalDate from = LocalDate.now().minusMonths(11).withDayOfMonth(1);
        List<DashboardSummary.MonthlyTrend> trends = buildTrends(from);

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .incomeByCategory(incomeByCategory)
                .expenseByCategory(expenseByCategory)
                .recentActivity(recent)
                .monthlyTrends(trends)
                .build();
    }

    private Map<String, BigDecimal> toMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], (BigDecimal) row[1]);
        }
        return map;
    }

    private List<DashboardSummary.MonthlyTrend> buildTrends(LocalDate from) {
        List<Object[]> rows = recordRepository.monthlyTrendNative(from);
        Map<String, BigDecimal[]> monthMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String month     = (String) row[0];
            String type      = (String) row[1];
            BigDecimal total = new BigDecimal(row[2].toString());
            monthMap.computeIfAbsent(month, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            if ("INCOME".equals(type)) monthMap.get(month)[0] = total;
            else                        monthMap.get(month)[1] = total;
        }
        return monthMap.entrySet().stream()
                .map(e -> new DashboardSummary.MonthlyTrend(
                        e.getKey(), e.getValue()[0], e.getValue()[1]))
                .collect(Collectors.toList());
    }
}
