package com.finexpert.service;
import com.finexpert.model.Goal;
import com.finexpert.storage.CSVStorageHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
public class GoalManager {
    private final CSVStorageHandler storageHandler;
    private final List<Goal> goals;
    public GoalManager(CSVStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
        this.goals = new ArrayList<>(storageHandler.loadGoals());
    }
    public Goal setGoal(double targetAmount, LocalDate targetDate) {
        int newId = generateNextId();
        Goal goal = new Goal(newId, targetAmount, targetDate, LocalDate.now(), Goal.Status.ACTIVE);
        goals.add(goal);
        storageHandler.saveGoals(goals);
        return goal;
    }
    private int generateNextId() {
        int maxId = 0;
        for (Goal g : goals) {
            if (g.getGoalId() > maxId) {
                maxId = g.getGoalId();
            }
        }
        return maxId + 1;
    }
    public Goal getActiveGoal() {
        for (Goal g : goals) {
            if (g.getStatus() == Goal.Status.ACTIVE) {
                return g;
            }
        }
        return null;
    }
    public List<Goal> getAllGoals() {
        return Collections.unmodifiableList(goals);
    }
    public GoalProgress checkProgress(AnalyticsEngine analyticsEngine) {
        Goal goal = getActiveGoal();
        if (goal == null) {
            throw new NoSuchElementException("No active savings goal has been set yet.");
        }
        AnalyticsEngine.Summary summary = analyticsEngine.generateSummary(goal.getCreatedDate(), LocalDate.now());
        double savedSoFar = summary.getNetSavings();
        double percentComplete = (savedSoFar / goal.getTargetAmount()) * 100;
        boolean reached = savedSoFar >= goal.getTargetAmount();
        boolean overdue = !reached && LocalDate.now().isAfter(goal.getTargetDate());

        if (reached && goal.getStatus() == Goal.Status.ACTIVE) {
            goal.markCompleted();
            storageHandler.saveGoals(goals);
        }
        return new GoalProgress(goal, savedSoFar, percentComplete, reached, overdue);
    }
    public static class GoalProgress {
        private final Goal goal;
        private final double savedSoFar;
        private final double percentComplete;
        private final boolean reached;
        private final boolean overdue;
        public GoalProgress(Goal goal, double savedSoFar, double percentComplete, boolean reached, boolean overdue) {
            this.goal = goal;
            this.savedSoFar = savedSoFar;
            this.percentComplete = percentComplete;
            this.reached = reached;
            this.overdue = overdue;
        }
        public Goal getGoal() { return goal; }
        public double getSavedSoFar() { return savedSoFar; }
        public double getPercentComplete() { return percentComplete; }
        public boolean isReached() { return reached; }
        public boolean isOverdue() { return overdue; }
        @Override
        public String toString() {
            String status = reached ? "reached!" : (overdue ? "overdue" : "in progress");
            return String.format("Goal: save %.2f by %s - saved %.2f so far (%.0f%%) [%s]",
                    goal.getTargetAmount(), goal.getTargetDate(), savedSoFar, percentComplete, status);
        }
    }
}
