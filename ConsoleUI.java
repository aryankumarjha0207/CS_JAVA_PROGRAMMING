package com.finexpert.ui;
import com.finexpert.model.Budget;
import com.finexpert.model.Category;
import com.finexpert.model.Goal;
import com.finexpert.model.Transaction;
import com.finexpert.model.TransactionType;
import com.finexpert.service.AnalyticsEngine;
import com.finexpert.service.BudgetManager;
import com.finexpert.service.GoalManager;
import com.finexpert.service.HealthScoreCalculator;
import com.finexpert.service.TransactionManager;
import com.finexpert.service.WhatIfSimulator;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
public class ConsoleUI {
    private final Scanner scanner;
    private final TransactionManager transactionManager;
    private final BudgetManager budgetManager;
    private final AnalyticsEngine analyticsEngine;
    private final HealthScoreCalculator healthScoreCalculator;
    private final WhatIfSimulator whatIfSimulator;
    private final GoalManager goalManager;
    public ConsoleUI(TransactionManager transactionManager, BudgetManager budgetManager,
                      AnalyticsEngine analyticsEngine, HealthScoreCalculator healthScoreCalculator,
                      WhatIfSimulator whatIfSimulator, GoalManager goalManager) {
        this.scanner = new Scanner(System.in);
        this.transactionManager = transactionManager;
        this.budgetManager = budgetManager;
        this.analyticsEngine = analyticsEngine;
        this.healthScoreCalculator = healthScoreCalculator;
        this.whatIfSimulator = whatIfSimulator;
        this.goalManager = goalManager;
    }
    public void run() {
        System.out.println("=======================================");
        System.out.println("           Welcome to FinExpert");
        System.out.println("    Smart Finance Manager for Students");
        System.out.println("=======================================");

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("Main menu:");
            System.out.println("  1. Transactions");
            System.out.println("  2. Budgets");
            System.out.println("  3. Reports & analysis");
            System.out.println("  4. Exit");
            int choice = readMenuChoice("Enter choice: ", 1, 4);
            switch (choice) {
                case 1: transactionsMenu(); break;
                case 2: budgetsMenu(); break;
                case 3: reportsMenu(); break;
                case 4: running = false; break;
            }
        }
        System.out.println("Thanks for using FinExpert. Goodbye!");
    }
    private void transactionsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Transactions:");
            System.out.println("  1. Add transaction");
            System.out.println("  2. Edit transaction");
            System.out.println("  3. Delete transaction");
            System.out.println("  4. View all transactions");
            System.out.println("  5. Search / filter transactions");
            System.out.println("  6. Back to main menu");
            int choice = readMenuChoice("Enter choice: ", 1, 6);
            switch (choice) {
                case 1: addTransactionFlow(); break;
                case 2: editTransactionFlow(); break;
                case 3: deleteTransactionFlow(); break;
                case 4: viewAllTransactions(); break;
                case 5: searchTransactionsFlow(); break;
                case 6: back = true; break;
            }
        }
    }
    private void addTransactionFlow() {
        try {
            TransactionType type = readTransactionType();
            Category category = selectCategory(type == TransactionType.INCOME);
            double amount = readPositiveDouble("Enter amount: ");
            LocalDate date = readDateOrDefault("Enter date (YYYY-MM-DD, blank for today): ", LocalDate.now());
            System.out.print("Enter a note (optional): ");
            String note = scanner.nextLine().trim();
            Transaction transaction = transactionManager.addTransaction(type, amount, category, date, note);
            System.out.println("Added: " + transaction);
        } catch (IllegalArgumentException e) {
            System.out.println("Could not add transaction: " + e.getMessage());
        }
    }
    private void editTransactionFlow() {
        viewAllTransactions();
        if (transactionManager.getAllTransactions().isEmpty()) {
            return;
        }
        int id = readPositiveInt("Enter the id of the transaction to edit: ");
        try {
            TransactionType type = readTransactionType();
            Category category = selectCategory(type == TransactionType.INCOME);
            double amount = readPositiveDouble("Enter new amount: ");
            LocalDate date = readDateOrDefault("Enter new date (YYYY-MM-DD, blank for today): ", LocalDate.now());
            System.out.print("Enter a new note (optional): ");
            String note = scanner.nextLine().trim();

            Transaction updated = transactionManager.editTransaction(id, type, amount, category, date, note);
            System.out.println("Updated: " + updated);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println("Could not edit transaction: " + e.getMessage());
        }
    }
    private void deleteTransactionFlow() {
        viewAllTransactions();
        if (transactionManager.getAllTransactions().isEmpty()) {
            return;
        }
        int id = readPositiveInt("Enter the id of the transaction to delete: ");
        boolean removed = transactionManager.deleteTransaction(id);
        System.out.println(removed ? "Transaction deleted." : "No transaction found with that id.");
    }
    private void viewAllTransactions() {
        List<Transaction> transactions = transactionManager.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }
        System.out.println();
        System.out.println("All transactions:");
        for (Transaction t : transactions) {
            System.out.println("  " + t);
        }
    }
    private void searchTransactionsFlow() {
        Category category = null;
        System.out.print("Filter by category? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("1. Income category   2. Expense category");
            int typeChoice = readMenuChoice("Enter choice: ", 1, 2);
            category = selectCategory(typeChoice == 1);
        }
        TransactionType type = null;
        System.out.print("Filter by type (income/expense)? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            type = readTransactionType();
        }
        LocalDate from = null;
        LocalDate to = null;
        System.out.print("Filter by date range? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            from = readDateOrNull("From date (YYYY-MM-DD, blank for no lower bound): ");
            to = readDateOrNull("To date (YYYY-MM-DD, blank for no upper bound): ");
        }
        List<Transaction> results = transactionManager.filterTransactions(category, type, from, to);
        System.out.println();
        if (results.isEmpty()) {
            System.out.println("No matching transactions found.");
        } else {
            System.out.println("Matching transactions:");
            for (Transaction t : results) {
                System.out.println("  " + t);
            }
        }
    }
    private void budgetsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Budgets:");
            System.out.println("  1. Set or update a budget");
            System.out.println("  2. Remove a budget");
            System.out.println("  3. View all budgets and status");
            System.out.println("  4. Back to main menu");
            int choice = readMenuChoice("Enter choice: ", 1, 4);
            switch (choice) {
                case 1: setBudgetFlow(); break;
                case 2: removeBudgetFlow(); break;
                case 3: viewBudgetsFlow(); break;
                case 4: back = true; break;
            }
        }
    }
    private void setBudgetFlow() {
        try {
            Category category = selectCategory(false);
            double limit = readPositiveDouble("Enter the monthly limit: ");
            Budget budget = budgetManager.setBudget(category, limit);
            System.out.println("Budget saved: " + budget);
        } catch (IllegalArgumentException e) {
            System.out.println("Could not set budget: " + e.getMessage());
        }
    }
    private void removeBudgetFlow() {
        Category category = selectCategory(false);
        boolean removed = budgetManager.removeBudget(category);
        System.out.println(removed ? "Budget removed." : "No budget was set for that category.");
    }
    private void viewBudgetsFlow() {
        List<BudgetManager.BudgetStatus> statuses = budgetManager.checkAllBudgetStatuses();
        if (statuses.isEmpty()) {
            System.out.println("No budgets set yet.");
            return;
        }
        System.out.println();
        System.out.println("Budget status for this month:");
        for (BudgetManager.BudgetStatus status : statuses) {
            String bar = buildProgressBar(status.getPercentUsed());
            System.out.printf("  %-16s %s  %.2f / %.2f  [%s]%n",
                    status.getCategory().getLabel(), bar, status.getSpent(), status.getLimit(), status.getLevel());
        }
    }
    private void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Reports & analysis:");
            System.out.println("  1. Monthly summary");
            System.out.println("  2. Category breakdown");
            System.out.println("  3. Spending trend (vs last month)");
            System.out.println("  4. Financial health score");
            System.out.println("  5. What-if simulator");
            System.out.println("  6. Savings goal");
            System.out.println("  7. Back to main menu");
            int choice = readMenuChoice("Enter choice: ", 1, 7);
            switch (choice) {
                case 1: monthlySummaryReport(); break;
                case 2: categoryBreakdownReport(); break;
                case 3: spendingTrendReport(); break;
                case 4: healthScoreReport(); break;
                case 5: whatIfReportFlow(); break;
                case 6: goalMenu(); break;
                case 7: back = true; break;
            }
        }
    }
    private void monthlySummaryReport() {
        YearMonth currentMonth = YearMonth.now();
        AnalyticsEngine.Summary summary = analyticsEngine.generateSummary(
                currentMonth.atDay(1), currentMonth.atEndOfMonth());
        System.out.println();
        System.out.println("Summary for " + currentMonth + ":");
        System.out.println("  " + summary);
    }
    private void categoryBreakdownReport() {
        YearMonth currentMonth = YearMonth.now();
        Map<Category, Double> breakdown = analyticsEngine.categoryBreakdownPercentages(
                currentMonth.atDay(1), currentMonth.atEndOfMonth());
        if (breakdown.isEmpty()) {
            System.out.println("No expenses recorded this month yet.");
            return;
        }
        System.out.println();
        System.out.println("Category breakdown for " + currentMonth + ":");
        for (Map.Entry<Category, Double> entry : breakdown.entrySet()) {
            System.out.printf("  %-16s %.1f%%%n", entry.getKey().getLabel(), entry.getValue());
        }
    }
    private void spendingTrendReport() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);
        AnalyticsEngine.TrendResult trend = analyticsEngine.compareSpending(
                currentMonth.atDay(1), currentMonth.atEndOfMonth(),
                previousMonth.atDay(1), previousMonth.atEndOfMonth());
        System.out.println();
        System.out.println("  " + trend);
    }
    private void healthScoreReport() {
        YearMonth currentMonth = YearMonth.now();
        HealthScoreCalculator.HealthScoreResult result = healthScoreCalculator.calculateScore(
                currentMonth.atDay(1), currentMonth.atEndOfMonth());
        System.out.println();
        System.out.println("  " + result);
    }
    private void whatIfReportFlow() {
        Category category = selectCategory(false);
        System.out.print("Enter percentage change (negative to cut spending, e.g. -20): ");
        double percentChange = readDoubleLoop();
        YearMonth currentMonth = YearMonth.now();
        WhatIfSimulator.SimulationResult result = whatIfSimulator.simulate(
                category, percentChange, currentMonth.atDay(1), currentMonth.atEndOfMonth());
        System.out.println();
        System.out.println("  " + result);
        int months;
        while (true) {
            System.out.print("Project this over how many months? (0 to skip): ");
            try {
                months = Integer.parseInt(scanner.nextLine().trim());
                if (months >= 0) break;
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Please enter zero or a positive whole number.");
        }
        if (months > 0) {
            double projected = whatIfSimulator.projectOverMonths(result, months);
            System.out.printf("  Keeping this up for %d months could change your savings by %.2f overall.%n",
                    months, projected);
        }
    }
    private void goalMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Savings goal:");
            System.out.println("  1. Set a new goal");
            System.out.println("  2. View progress on active goal");
            System.out.println("  3. Back to reports menu");
            int choice = readMenuChoice("Enter choice: ", 1, 3);
            switch (choice) {
                case 1: setGoalFlow(); break;
                case 2: viewGoalProgress(); break;
                case 3: back = true; break;
            }
        }
    }
    private void setGoalFlow() {
        try {
            double amount = readPositiveDouble("Enter the amount you want to save: ");
            LocalDate targetDate = readDateOrDefault(
                    "Enter the target date (YYYY-MM-DD, blank for one month from today): ",
                    LocalDate.now().plusMonths(1));
            Goal goal = goalManager.setGoal(amount, targetDate);
            System.out.println("Goal saved: " + goal);
        } catch (IllegalArgumentException e) {
            System.out.println("Could not set goal: " + e.getMessage());
        }
    }
    private void viewGoalProgress() {
        try {
            GoalManager.GoalProgress progress = goalManager.checkProgress(analyticsEngine);
            System.out.println();
            System.out.println("  " + progress);
            System.out.println("  " + buildProgressBar(Math.min(100, progress.getPercentComplete())));
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }
    private int readMenuChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below   
            }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }
    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Please enter a valid positive whole number.");
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Please enter a positive number.");
        }
    }
    private double readDoubleLoop() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    private LocalDate readDateOrDefault(String prompt, LocalDate defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format, please use YYYY-MM-DD.");
            }
        }
    }
    private LocalDate readDateOrNull(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format, please use YYYY-MM-DD (or leave blank).");
            }
        }
    }
    private TransactionType readTransactionType() {
        System.out.println("1. Income   2. Expense");
        int choice = readMenuChoice("Enter choice: ", 1, 2);
        return (choice == 1) ? TransactionType.INCOME : TransactionType.EXPENSE;
    }
    private Category selectCategory(boolean incomeCategories) {
        List<Category> options = new java.util.ArrayList<>();
        for (Category c : Category.values()) {
            if (c.isIncome() == incomeCategories) {
                options.add(c);
            }
        }
        System.out.println("Choose a category:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + options.get(i).getLabel());
        }
        int choice = readMenuChoice("Enter choice: ", 1, options.size());
        return options.get(choice - 1);
    }
    private String buildProgressBar(double percent) {
        int totalBlocks = 20;
        double clamped = Math.max(0, Math.min(100, percent));
        int filled = (int) Math.round((clamped / 100.0) * totalBlocks);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < totalBlocks; i++) {
            bar.append(i < filled ? '#' : '-');
        }
        bar.append("] ").append(String.format("%.0f%%", percent));
        return bar.toString();
    }
}
