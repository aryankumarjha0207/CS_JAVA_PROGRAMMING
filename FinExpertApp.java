package com.finexpert;
import com.finexpert.service.AnalyticsEngine;
import com.finexpert.service.BudgetManager;
import com.finexpert.service.GoalManager;
import com.finexpert.service.HealthScoreCalculator;
import com.finexpert.service.TransactionManager;
import com.finexpert.service.WhatIfSimulator;
import com.finexpert.storage.CSVStorageHandler;
import com.finexpert.storage.StorageException;
import com.finexpert.ui.ConsoleUI;
public class FinExpertApp {

    public static void main(String[] args) {
        CSVStorageHandler storageHandler = new CSVStorageHandler();
        try {
            storageHandler.ensureDataFilesExist();
        } catch (StorageException e) {
            System.out.println("Could not start FinExpert: " + e.getMessage());
            return;
        }
        TransactionManager transactionManager = new TransactionManager(storageHandler);
        BudgetManager budgetManager = new BudgetManager(storageHandler, transactionManager);
        AnalyticsEngine analyticsEngine = new AnalyticsEngine(transactionManager);
        HealthScoreCalculator healthScoreCalculator = new HealthScoreCalculator(budgetManager, analyticsEngine);
        WhatIfSimulator whatIfSimulator = new WhatIfSimulator(transactionManager, analyticsEngine);
        GoalManager goalManager = new GoalManager(storageHandler);
        ConsoleUI consoleUI = new ConsoleUI(
                transactionManager, budgetManager, analyticsEngine,
                healthScoreCalculator, whatIfSimulator, goalManager);

        consoleUI.run();
    }
}
