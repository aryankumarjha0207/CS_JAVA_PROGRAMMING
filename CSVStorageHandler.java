package com.finexpert.storage;
import com.finexpert.model.Budget;
import com.finexpert.model.Goal;
import com.finexpert.model.Transaction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class CSVStorageHandler {
    private static final String DATA_DIR = "data";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";
    private static final String BUDGETS_FILE = DATA_DIR + "/budgets.csv";
    private static final String GOALS_FILE = DATA_DIR + "/goals.csv";
    private static final String TRANSACTIONS_HEADER = "id,type,amount,category,date,note";
    private static final String BUDGETS_HEADER = "category,monthlyLimit,lastUpdated";
    private static final String GOALS_HEADER = "goalId,targetAmount,targetDate,createdDate,status";
    public void ensureDataFilesExist() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            createFileWithHeaderIfMissing(TRANSACTIONS_FILE, TRANSACTIONS_HEADER);
            createFileWithHeaderIfMissing(BUDGETS_FILE, BUDGETS_HEADER);
            createFileWithHeaderIfMissing(GOALS_FILE, GOALS_HEADER);
        } catch (IOException e) {
            throw new StorageException("Could not set up the data files in '" + DATA_DIR + "'.", e);
        }
    }
    private void createFileWithHeaderIfMissing(String path, String header) throws IOException {
        Path filePath = Paths.get(path);
        if (Files.notExists(filePath)) {
            Files.write(filePath, (header + System.lineSeparator()).getBytes());
        }
    }
    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (String line : readDataLines(TRANSACTIONS_FILE)) {
            try {
                transactions.add(Transaction.fromCsvRow(line));
            } catch (Exception e) {
                System.out.println("Skipping a corrupted transaction row: " + line + " (" + e.getMessage() + ")");
            }
        }
        return transactions;
    }
    public void saveTransactions(List<Transaction> transactions) {
        List<String> lines = new ArrayList<>();
        lines.add(TRANSACTIONS_HEADER);
        for (Transaction t : transactions) {
            lines.add(t.toCsvRow());
        }
        writeAllLines(TRANSACTIONS_FILE, lines);
    }
    public List<Budget> loadBudgets() {
        List<Budget> budgets = new ArrayList<>();
        for (String line : readDataLines(BUDGETS_FILE)) {
            try {
                budgets.add(Budget.fromCsvRow(line));
            } catch (Exception e) {
                System.out.println("Skipping a corrupted budget row: " + line + " (" + e.getMessage() + ")");
            }
        }
        return budgets;
    }
    public void saveBudgets(List<Budget> budgets) {
        List<String> lines = new ArrayList<>();
        lines.add(BUDGETS_HEADER);
        for (Budget b : budgets) {
            lines.add(b.toCsvRow());
        }
        writeAllLines(BUDGETS_FILE, lines);
    }
    public List<Goal> loadGoals() {
        List<Goal> goals = new ArrayList<>();
        for (String line : readDataLines(GOALS_FILE)) {
            try {
                goals.add(Goal.fromCsvRow(line));
            } catch (Exception e) {
                System.out.println("Skipping a corrupted goal row: " + line + " (" + e.getMessage() + ")");
            }
        }
        return goals;
    }
    public void saveGoals(List<Goal> goals) {
        List<String> lines = new ArrayList<>();
        lines.add(GOALS_HEADER);
        for (Goal g : goals) {
            lines.add(g.toCsvRow());
        }
        writeAllLines(GOALS_FILE, lines);
    }
    private List<String> readDataLines(String path) {
        Path filePath = Paths.get(path);
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }
        try {
            List<String> allLines = Files.readAllLines(filePath);
            List<String> dataLines = new ArrayList<>();
            for (int i = 1; i < allLines.size(); i++) {
                String line = allLines.get(i).trim();
                if (!line.isEmpty()) {
                    dataLines.add(line);
                }
            }
            return dataLines;
        } catch (IOException e) {
            throw new StorageException("Could not read data from '" + path + "'.", e);
        }
    }
    private void writeAllLines(String path, List<String> lines) {
        try {
            Files.write(Paths.get(path), lines);
        } catch (IOException e) {
            throw new StorageException("Could not save data to '" + path + "'.", e);
        }
    }
}
