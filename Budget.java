package com.finexpert.model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class Budget {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private final Category category;
    private double monthlyLimit;
    private LocalDate lastUpdated;
    public Budget(Category category, double monthlyLimit, LocalDate lastUpdated) {
        if (category.isIncome()) {
            throw new IllegalArgumentException(
                    "Budgets can only be set on expense categories, not '" + category.getLabel() + "'.");
        }
        if (monthlyLimit <= 0) {
            throw new IllegalArgumentException("Monthly budget limit must be greater than zero.");
        }
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.lastUpdated = lastUpdated;
    }
    public Category getCategory() {
        return category;
    }
    public double getMonthlyLimit() {
        return monthlyLimit;
    }
    public LocalDate getLastUpdated() {
        return lastUpdated;
    }
    public void updateLimit(double newLimit, LocalDate updateDate) {
        if (newLimit <= 0) {
            throw new IllegalArgumentException("Monthly budget limit must be greater than zero.");
        }
        this.monthlyLimit = newLimit;
        this.lastUpdated = updateDate;
    }
    public String toCsvRow() {
        return String.join(",",
                category.getLabel(),
                String.valueOf(monthlyLimit),
                lastUpdated.format(DATE_FORMAT));
    }
    public static Budget fromCsvRow(String csvRow) {
        String[] fields = csvRow.split(",");
        if (fields.length < 3) {
            throw new IllegalArgumentException("Malformed budget row (expected 3 columns): " + csvRow);
        }
        Category category = Category.fromLabel(fields[0].trim());
        double limit = Double.parseDouble(fields[1].trim());
        LocalDate updated = LocalDate.parse(fields[2].trim(), DATE_FORMAT);

        return new Budget(category, limit, updated);
    }
    @Override
    public String toString() {
        return String.format("%s: limit %.2f (last updated %s)", category.getLabel(), monthlyLimit, lastUpdated);
    }
}
