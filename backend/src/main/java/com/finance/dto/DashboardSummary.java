package com.finance.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardSummary {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> incomeByCategory;
    private Map<String, BigDecimal> expenseByCategory;
    private List<RecordResponse>    recentActivity;
    private List<MonthlyTrend>      monthlyTrends;

    public DashboardSummary() {}

    // ── Builder ───────────────────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal totalIncome, totalExpenses, netBalance;
        private Map<String, BigDecimal> incomeByCategory, expenseByCategory;
        private List<RecordResponse> recentActivity;
        private List<MonthlyTrend>   monthlyTrends;

        public Builder totalIncome(BigDecimal v)                         { this.totalIncome        = v; return this; }
        public Builder totalExpenses(BigDecimal v)                       { this.totalExpenses       = v; return this; }
        public Builder netBalance(BigDecimal v)                          { this.netBalance          = v; return this; }
        public Builder incomeByCategory(Map<String, BigDecimal> v)       { this.incomeByCategory   = v; return this; }
        public Builder expenseByCategory(Map<String, BigDecimal> v)      { this.expenseByCategory  = v; return this; }
        public Builder recentActivity(List<RecordResponse> v)            { this.recentActivity     = v; return this; }
        public Builder monthlyTrends(List<MonthlyTrend> v)               { this.monthlyTrends      = v; return this; }

        public DashboardSummary build() {
            DashboardSummary s = new DashboardSummary();
            s.totalIncome       = this.totalIncome;
            s.totalExpenses     = this.totalExpenses;
            s.netBalance        = this.netBalance;
            s.incomeByCategory  = this.incomeByCategory;
            s.expenseByCategory = this.expenseByCategory;
            s.recentActivity    = this.recentActivity;
            s.monthlyTrends     = this.monthlyTrends;
            return s;
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public BigDecimal               getTotalIncome()       { return totalIncome; }
    public BigDecimal               getTotalExpenses()     { return totalExpenses; }
    public BigDecimal               getNetBalance()        { return netBalance; }
    public Map<String, BigDecimal>  getIncomeByCategory()  { return incomeByCategory; }
    public Map<String, BigDecimal>  getExpenseByCategory() { return expenseByCategory; }
    public List<RecordResponse>     getRecentActivity()    { return recentActivity; }
    public List<MonthlyTrend>       getMonthlyTrends()     { return monthlyTrends; }

    // ── Inner class ───────────────────────────────────────────────────────────
    public static class MonthlyTrend {
        private String     month;
        private BigDecimal income;
        private BigDecimal expense;

        public MonthlyTrend() {}
        public MonthlyTrend(String month, BigDecimal income, BigDecimal expense) {
            this.month   = month;
            this.income  = income;
            this.expense = expense;
        }

        public String     getMonth()   { return month; }
        public BigDecimal getIncome()  { return income; }
        public BigDecimal getExpense() { return expense; }
    }
}
