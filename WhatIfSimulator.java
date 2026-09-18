package com.finexpert.service;
import com.finexpert.model.Category;
import java.time.LocalDate;
public class WhatIfSimulator {
    private final TransactionManager transactionManager;
    private final AnalyticsEngine analyticsEngine;
    public WhatIfSimulator(TransactionManager transactionManager, AnalyticsEngine analyticsEngine) {
        this.transactionManager = transactionManager;
        this.analyticsEngine = analyticsEngine;
    }
    public SimulationResult simulate(Category category, double percentChange,
                                      LocalDate periodStart, LocalDate periodEnd) {
        double currentCategorySpend = transactionManager.getTotalSpentInCategory(category, periodStart, periodEnd);
        double changeAmount = currentCategorySpend * (percentChange / 100.0);
        double projectedCategorySpend = Math.max(0, currentCategorySpend + changeAmount);
        AnalyticsEngine.Summary summary = analyticsEngine.generateSummary(periodStart, periodEnd);
        double currentNetSavings = summary.getNetSavings();
        double projectedNetSavings = currentNetSavings - changeAmount;
        return new SimulationResult(category, percentChange, currentCategorySpend,
                projectedCategorySpend, currentNetSavings, projectedNetSavings);
    }
    public double projectOverMonths(SimulationResult result, int months) {
        double extraSavingsPerMonth = result.getProjectedNetSavings() - result.getCurrentNetSavings();
        return extraSavingsPerMonth * months;
    }
    public static class SimulationResult {
        private final Category category;
        private final double percentChange;
        private final double currentCategorySpend;
        private final double projectedCategorySpend;
        private final double currentNetSavings;
        private final double projectedNetSavings;
        public SimulationResult(Category category, double percentChange, double currentCategorySpend,
                                 double projectedCategorySpend, double currentNetSavings, double projectedNetSavings) {
            this.category = category;
            this.percentChange = percentChange;
            this.currentCategorySpend = currentCategorySpend;
            this.projectedCategorySpend = projectedCategorySpend;
            this.currentNetSavings = currentNetSavings;
            this.projectedNetSavings = projectedNetSavings;
        }
        public Category getCategory() { return category; }
        public double getPercentChange() { return percentChange; }
        public double getCurrentCategorySpend() { return currentCategorySpend; }
        public double getProjectedCategorySpend() { return projectedCategorySpend; }
        public double getCurrentNetSavings() { return currentNetSavings; }
        public double getProjectedNetSavings() { return projectedNetSavings; }
        @Override
        public String toString() {
            String direction = percentChange < 0 ? "cutting" : "increasing";
            return String.format(
                    "If you tried %s %s spending by %.0f%%: spend would go from %.2f to %.2f, "
                            + "and net savings would change from %.2f to %.2f",
                    direction, category.getLabel(), Math.abs(percentChange),
                    currentCategorySpend, projectedCategorySpend, currentNetSavings, projectedNetSavings);
        }
    }
}
