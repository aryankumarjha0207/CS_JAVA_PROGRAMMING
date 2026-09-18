package com.finexpert.service;
import com.finexpert.model.Category;
import com.finexpert.model.Transaction;
import com.finexpert.model.TransactionType;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
public class AnalyticsEngine {
    private final TransactionManager transactionManager;
    public AnalyticsEngine(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    public Summary generateSummary(LocalDate fromDate, LocalDate toDate) {
        double income = 0;
        double expense = 0;
        for (Transaction t : transactionManager.filterTransactions(null, null, fromDate, toDate)) {
            if (t.getType() == TransactionType.INCOME) {
                income += t.getAmount();
            } else {
                expense += t.getAmount();
            }
        }
        return new Summary(fromDate, toDate, income, expense);
    }
    public Map<Category, Double> categoryBreakdownPercentages(LocalDate fromDate, LocalDate toDate) {
        Map<Category, Double> totals = new LinkedHashMap<>();
        double totalExpense = 0;
        for (Transaction t : transactionManager.filterTransactions(null, TransactionType.EXPENSE, fromDate, toDate)) {
            totals.merge(t.getCategory(), t.getAmount(), Double::sum);
            totalExpense += t.getAmount();
        }
        Map<Category, Double> percentages = new LinkedHashMap<>();
        for (Map.Entry<Category, Double> entry : totals.entrySet()) {
            double percent = (totalExpense == 0) ? 0 : (entry.getValue() / totalExpense) * 100;
            percentages.put(entry.getKey(), percent);
        }
        return percentages;
    }
    public TrendResult compareSpending(LocalDate currentStart, LocalDate currentEnd,
                                        LocalDate previousStart, LocalDate previousEnd) {
        double currentExpense = totalExpenseInRange(currentStart, currentEnd);
        double previousExpense = totalExpenseInRange(previousStart, previousEnd);
        double percentChange;
        if (previousExpense == 0) {
            percentChange = (currentExpense == 0) ? 0 : 100;
        } else {
            percentChange = ((currentExpense - previousExpense) / previousExpense) * 100;
        }
        Direction direction;
        if (percentChange > 0.01) {
            direction = Direction.UP;
        } else if (percentChange < -0.01) {
            direction = Direction.DOWN;
        } else {
            direction = Direction.FLAT;
        }
        return new TrendResult(currentExpense, previousExpense, percentChange, direction);
    }
    private double totalExpenseInRange(LocalDate from, LocalDate to) {
        double total = 0;
        for (Transaction t : transactionManager.filterTransactions(null, TransactionType.EXPENSE, from, to)) {
            total += t.getAmount();
        }
        return total;
    }
    public enum Direction {
        UP, DOWN, FLAT
    }
    public static class Summary {
        private final LocalDate fromDate;
        private final LocalDate toDate;
        private final double totalIncome;
        private final double totalExpense;
        public Summary(LocalDate fromDate, LocalDate toDate, double totalIncome, double totalExpense) {
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
        }
        public LocalDate getFromDate() { return fromDate; }
        public LocalDate getToDate() { return toDate; }
        public double getTotalIncome() { return totalIncome; }
        public double getTotalExpense() { return totalExpense; }
        public double getNetSavings() { return totalIncome - totalExpense; }
        @Override
        public String toString() {
            return String.format("From %s to %s: income %.2f, expense %.2f, net savings %.2f",
                    fromDate, toDate, totalIncome, totalExpense, getNetSavings());
        }
    }
    public static class TrendResult {
        private final double currentExpense;
        private final double previousExpense;
        private final double percentChange;
        private final Direction direction;
        public TrendResult(double currentExpense, double previousExpense, double percentChange, Direction direction) {
            this.currentExpense = currentExpense;
            this.previousExpense = previousExpense;
            this.percentChange = percentChange;
            this.direction = direction;
        }
        public double getCurrentExpense() { return currentExpense; }
        public double getPreviousExpense() { return previousExpense; }
        public double getPercentChange() { return percentChange; }
        public Direction getDirection() { return direction; }
        @Override
        public String toString() {
            return String.format("Spending is %s by %.1f%% compared to the previous period (%.2f vs %.2f)",
                    direction, Math.abs(percentChange), currentExpense, previousExpense);
        }
    }
}
