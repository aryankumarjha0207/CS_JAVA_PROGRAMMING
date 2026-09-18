package com.finexpert.model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class Transaction {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private final int id;
    private final TransactionType type;
    private final double amount;
    private final Category category;
    private final LocalDate date;
    private final String note;
    public Transaction(int id, TransactionType type, double amount, Category category, LocalDate date, String note) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }
        if (type == TransactionType.INCOME && !category.isIncome()) {
            throw new IllegalArgumentException(
                    "'" + category.getLabel() + "' is an expense category, not an income category.");
        }
        if (type == TransactionType.EXPENSE && category.isIncome()) {
            throw new IllegalArgumentException(
                    "'" + category.getLabel() + "' is an income category, not an expense category.");
        }
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = (note == null) ? "" : note.trim();
    }
    public int getId() {
        return id;
    }
    public TransactionType getType() {
        return type;
    }
    public double getAmount() {
        return amount;
    }
    public Category getCategory() {
        return category;
    }
    public LocalDate getDate() {
        return date;
    }
    public String getNote() {
        return note;
    }
    public String toCsvRow() {
        String safeNote = note.contains(",") ? "\"" + note + "\"" : note;
        return String.join(",",
                String.valueOf(id),
                type.name(),
                String.valueOf(amount),
                category.getLabel(),
                date.format(DATE_FORMAT),
                safeNote);
    }
    public static Transaction fromCsvRow(String csvRow) {
        String[] fields = splitRespectingQuotes(csvRow);
        if (fields.length < 5) {
            throw new IllegalArgumentException("Malformed transaction row (expected at least 5 columns): " + csvRow);
        }
        int id = Integer.parseInt(fields[0].trim());
        TransactionType type = TransactionType.valueOf(fields[1].trim().toUpperCase());
        double amount = Double.parseDouble(fields[2].trim());
        Category category = Category.fromLabel(fields[3].trim());
        LocalDate date = LocalDate.parse(fields[4].trim(), DATE_FORMAT);
        String note = fields.length > 5 ? stripQuotes(fields[5].trim()) : "";
        return new Transaction(id, type, amount, category, date, note);
    }
    private static String[] splitRespectingQuotes(String row) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;
        for (char c : row.toCharArray()) {
            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        parts.add(current.toString());
        return parts.toArray(new String[0]);
    }
    private static String stripQuotes(String value) {
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
    @Override
    public String toString() {
        String sign = (type == TransactionType.INCOME) ? "+" : "-";
        String noteSuffix = note.isEmpty() ? "" : " | " + note;
        return String.format("[#%d] %s | %s%.2f | %s%s",
                id, date, sign, amount, category.getLabel(), noteSuffix);
    }
}
