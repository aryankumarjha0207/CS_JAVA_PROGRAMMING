package com.finexpert.service;
import com.finexpert.model.Category;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
public class HealthScoreCalculator {
    private static final double SAVINGS_WEIGHT = 40;
    private static final double ADHERENCE_WEIGHT = 35;
    private static final double DIVERSITY_WEIGHT = 25;
    private final BudgetManager budgetManager;
    private final AnalyticsEngine analyticsEngine;

    public HealthScoreCalculator(BudgetManager budgetManager, AnalyticsEngine analyticsEngine) {
        this.budgetManager = budgetManager;
        this.analyticsEngine = analyticsEngine;
    }
    public HealthScoreResult calculateScore(LocalDate periodStart, LocalDate periodEnd) {
        AnalyticsEngine.Summary summary = analyticsEngine.generateSummary(periodStart, periodEnd);
        double savingsScore = calculateSavingsScore(summary);
        double adherenceScore = calculateBudgetAdherenceScore();
        double diversityScore = calculateDiversityScore(periodStart, periodEnd);
        double total = savingsScore + adherenceScore + diversityScore;
        int roundedTotal = (int) Math.round(Math.max(0, Math.min(100, total)));
        return new HealthScoreResult(roundedTotal, ratingFor(roundedTotal),
                savingsScore, adherenceScore, diversityScore);
    }
    private double calculateSavingsScore(AnalyticsEngine.Summary summary) {
        if (summary.getTotalIncome() <= 0) {
            return 0;
        }
        double savingsRatePercent = (summary.getNetSavings() / summary.getTotalIncome()) * 100;
        double clamped = Math.max(0, Math.min(100, savingsRatePercent));
        return (clamped / 100.0) * SAVINGS_WEIGHT;
    }
    private double calculateBudgetAdherenceScore() {
        List<BudgetManager.BudgetStatus> statuses = budgetManager.checkAllBudgetStatuses();
        if (statuses.isEmpty()) {
            return ADHERENCE_WEIGHT * 0.5; 
        }
        long withinLimit = statuses.stream()
                .filter(s -> s.getLevel() != BudgetManager.AlertLevel.EXCEEDED)
                .count();
        double fraction = (double) withinLimit / statuses.size();
        return fraction * ADHERENCE_WEIGHT;
    }
    private double calculateDiversityScore(LocalDate periodStart, LocalDate periodEnd) {
        Map<Category, Double> breakdown = analyticsEngine.categoryBreakdownPercentages(periodStart, periodEnd);
        if (breakdown.isEmpty()) {
            return DIVERSITY_WEIGHT * 0.5; 
        }
        double maxShare = 0;
        for (double percent : breakdown.values()) {
            if (percent > maxShare) {
                maxShare = percent;
            }
        }
        double diversityFraction = Math.max(0, 1 - (maxShare / 100.0));
        return diversityFraction * DIVERSITY_WEIGHT;
    }
    private String ratingFor(int score) {
        if (score >= 80) return "Excellent";
        if (score >= 60) return "Good";
        if (score >= 40) return "Fair";
        return "Poor";
    }
    public static class HealthScoreResult {
        private final int score;
        private final String rating;
        private final double savingsScore;
        private final double adherenceScore;
        private final double diversityScore;
        public HealthScoreResult(int score, String rating, double savingsScore,
                                  double adherenceScore, double diversityScore) {
            this.score = score;
            this.rating = rating;
            this.savingsScore = savingsScore;
            this.adherenceScore = adherenceScore;
            this.diversityScore = diversityScore;
        }
        public int getScore() { return score; }
        public String getRating() { return rating; }
        public double getSavingsScore() { return savingsScore; }
        public double getAdherenceScore() { return adherenceScore; }
        public double getDiversityScore() { return diversityScore; }
        @Override
        public String toString() {
            return String.format(
                    "Financial Health Score: %d/100 (%s) - savings %.1f, budget adherence %.1f, diversity %.1f",
                    score, rating, savingsScore, adherenceScore, diversityScore);
        }
    }
}
