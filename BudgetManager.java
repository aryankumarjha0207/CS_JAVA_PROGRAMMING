package com.finexpert.service;
import com.finexpert.model.Budget;
import com.finexpert.model.Category;
import com.finexpert.storage.CSVStorageHandler;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
public class BudgetManager {
    private static final double WARNING_THRESHOLD = 0.8;
    private final CSVStorageHandler storageHandler;
    private final TransactionManager transactionManager;
    private final List<Budget> budgets;
    public BudgetManager(CSVStorageHandler storageHandler, TransactionManager transactionManager) {
        this.storageHandler = storageHandler;
        this.transactionManager = transactionManager;
        this.budgets = new ArrayList<>(storageHandler.loadBudgets());
    }
    public Budget setBudget(Category category, double monthlyLimit) {
        Budget existing = findByCategory(category);
        if (existing != null) {
            existing.updateLimit(monthlyLimit, LocalDate.now());
        } else {
            budgets.add(new Budget(category, monthlyLimit, LocalDate.now()));
        }
        storageHandler.saveBudgets(budgets);
        return findByCategory(category);
    }
    public boolean removeBudget(Category category) {
        boolean removed = budgets.removeIf(b -> b.getCategory() == category);
        if (removed) {
            storageHandler.saveBudgets(budgets);
        }
        return removed;
    }
    private Budget findByCategory(Category category) {
        for (Budget b : budgets) {
            if (b.getCategory() == category) {
                return b;
            }
        }
        return null;
    }
    public List<Budget> getAllBudgets() {
        return Collections.unmodifiableList(budgets);
    }
    public BudgetStatus checkBudgetStatus(Category category) {
        Budget budget = findByCategory(category);
        if (budget == null) {
            throw new NoSuchElementException("No budget set for category '" + category.getLabel() + "'.");
        }
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();
        double spent = transactionManager.getTotalSpentInCategory(category, monthStart, monthEnd);
        double percentUsed = (spent / budget.getMonthlyLimit()) * 100;
        AlertLevel level;
        if (percentUsed >= 100) {
            level = AlertLevel.EXCEEDED;
        } else if (percentUsed >= WARNING_THRESHOLD * 100) {
            level = AlertLevel.WARNING;
        } else {
            level = AlertLevel.OK;
        }
        return new BudgetStatus(category, budget.getMonthlyLimit(), spent, percentUsed, level);
    }
    public List<BudgetStatus> checkAllBudgetStatuses() {
        List<BudgetStatus> statuses = new ArrayList<>();
        for (Budget b : budgets) {
            statuses.add(checkBudgetStatus(b.getCategory()));
        }
        return statuses;
    }
    public enum AlertLevel {
        OK, WARNING, EXCEEDED
    }
    public static class BudgetStatus {
        private final Category category;
        private final double limit;
        private final double spent;
        private final double percentUsed;
        private final AlertLevel level;

        public BudgetStatus(Category category, double limit, double spent, double percentUsed, AlertLevel level) {
            this.category = category;
            this.limit = limit;
            this.spent = spent;
            this.percentUsed = percentUsed;
            this.level = level;
        }
        public Category getCategory() { return category; }
        public double getLimit() { return limit; }
        public double getSpent() { return spent; }
        public double getPercentUsed() { return percentUsed; }
        public AlertLevel getLevel() { return level; }

        @Override
        public String toString() {
            return String.format("%s: %.2f / %.2f (%.0f%%) [%s]",
                    category.getLabel(), spent, limit, percentUsed, level);
        }
    }
}
