package com.finexpert.service;
import com.finexpert.model.Category;
import com.finexpert.model.Transaction;
import com.finexpert.model.TransactionType;
import com.finexpert.storage.CSVStorageHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
public class TransactionManager {
    private final CSVStorageHandler storageHandler;
    private final List<Transaction> transactions;
    public TransactionManager(CSVStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
        this.transactions = new ArrayList<>(storageHandler.loadTransactions());
    }
    public Transaction addTransaction(TransactionType type, double amount, Category category,
                                       LocalDate date, String note) {
        int newId = generateNextId();
        Transaction transaction = new Transaction(newId, type, amount, category, date, note);
        transactions.add(transaction);
        storageHandler.saveTransactions(transactions);
        return transaction;
    }
    private int generateNextId() {
        int maxId = 0;
        for (Transaction t : transactions) {
            if (t.getId() > maxId) {
                maxId = t.getId();
            }
        }
        return maxId + 1;
    }
    public Transaction editTransaction(int id, TransactionType newType, double newAmount,
                                        Category newCategory, LocalDate newDate, String newNote) {
        int index = findIndexById(id);
        if (index == -1) {
            throw new NoSuchElementException("No transaction found with id " + id + ".");
        }
        Transaction updated = new Transaction(id, newType, newAmount, newCategory, newDate, newNote);
        transactions.set(index, updated);
        storageHandler.saveTransactions(transactions);
        return updated;
    }
    public boolean deleteTransaction(int id) {
        boolean removed = transactions.removeIf(t -> t.getId() == id);
        if (removed) {
            storageHandler.saveTransactions(transactions);
        }
        return removed;
    }
    private int findIndexById(int id) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
    public List<Transaction> getAllTransactions() {
        return Collections.unmodifiableList(transactions);
    }
    public List<Transaction> filterTransactions(Category category, TransactionType type,
                                                 LocalDate fromDate, LocalDate toDate) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (category != null && t.getCategory() != category) continue;
            if (type != null && t.getType() != type) continue;
            if (fromDate != null && t.getDate().isBefore(fromDate)) continue;
            if (toDate != null && t.getDate().isAfter(toDate)) continue;
            result.add(t);
        }
        return result;
    }
    public double getTotalByType(TransactionType type) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getType() == type) {
                total += t.getAmount();
            }
        }
        return total;
    }
    public double getTotalSpentInCategory(Category category, LocalDate fromDate, LocalDate toDate) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getType() != TransactionType.EXPENSE || t.getCategory() != category) continue;
            if (fromDate != null && t.getDate().isBefore(fromDate)) continue;
            if (toDate != null && t.getDate().isAfter(toDate)) continue;
            total += t.getAmount();
        }
        return total;
    }
}
