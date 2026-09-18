package com.finexpert.model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class Goal {
    public enum Status {
        ACTIVE,
        COMPLETED
    }
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private final int goalId;
    private double targetAmount;
    private LocalDate targetDate;
    private final LocalDate createdDate;
    private Status status;
    public Goal(int goalId, double targetAmount, LocalDate targetDate, LocalDate createdDate, Status status) {
        if (targetAmount <= 0) {
            throw new IllegalArgumentException("Savings goal target must be greater than zero.");
        }
        if (targetDate.isBefore(createdDate)) {
            throw new IllegalArgumentException("Goal target date can't be before the date it was created.");
        }
        this.goalId = goalId;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.createdDate = createdDate;
        this.status = status;
    }
    public int getGoalId() {
        return goalId;
    }
    public double getTargetAmount() {
        return targetAmount;
    }
    public LocalDate getTargetDate() {
        return targetDate;
    }
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    public Status getStatus() {
        return status;
    }
    public void markCompleted() {
        this.status = Status.COMPLETED;
    }
    public String toCsvRow() {
        return String.join(",",
                String.valueOf(goalId),
                String.valueOf(targetAmount),
                targetDate.format(DATE_FORMAT),
                createdDate.format(DATE_FORMAT),
                status.name());
    }
    public static Goal fromCsvRow(String csvRow) {
        String[] fields = csvRow.split(",");
        if (fields.length < 5) {
            throw new IllegalArgumentException("Malformed goal row (expected 5 columns): " + csvRow);
        }
        int id = Integer.parseInt(fields[0].trim());
        double amount = Double.parseDouble(fields[1].trim());
        LocalDate target = LocalDate.parse(fields[2].trim(), DATE_FORMAT);
        LocalDate created = LocalDate.parse(fields[3].trim(), DATE_FORMAT);
        Status status = Status.valueOf(fields[4].trim().toUpperCase());
        return new Goal(id, amount, target, created, status);
    }
    @Override
    public String toString() {
        return String.format("Goal #%d: save %.2f by %s [%s]", goalId, targetAmount, targetDate, status);
    }
}
