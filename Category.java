package com.finexpert.model;
public enum Category {
    SALARY_STIPEND("Salary/Stipend", true),
    ALLOWANCE("Allowance", true),
    GIFT("Gift", true),
    OTHER_INCOME("Other Income", true),
    FOOD("Food", false),
    RENT("Rent", false),
    SUBSCRIPTIONS("Subscriptions", false),
    TRAVEL("Travel", false),
    ENTERTAINMENT("Entertainment", false),
    SHOPPING("Shopping", false),
    HEALTH("Health", false),
    EDUCATION("Education", false),
    OTHER_EXPENSE("Other Expense", false);
    private final String label;
    private final boolean income;
    Category(String label, boolean income) {
        this.label = label;
        this.income = income;
    }
    public String getLabel() {
        return label;
    }
    public boolean isIncome() {
        return income;
    }    
    public static Category fromLabel(String label) {
        for (Category category : values()) {
            if (category.label.equalsIgnoreCase(label.trim())) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: '" + label + "'. Please pick from the listed categories.");
    }
}
