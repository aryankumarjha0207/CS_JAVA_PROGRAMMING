# FinExpert:-

A simple console-based personal finance and budget tracker.

## Overview

Most students don't really track where their money goes between food orders, subscriptions, and random spending, so it's easy to lose sight of how much they are actually spending. FinExpert is a friendly and light tool that helps students log income and expenses, set a monthly budget per category, and get a sense of what they're actually doing with their money including a computed "Financial Health Score" and a "What-If Simulator" to test out hypothetical spending changes before making them for real.

All data is stored locally in CSV files, so nothing needs to be installed beyond a working JDK.

## Features:-

* **Transaction management** — we can add, edit, delete, view, and search/filter income and expense entries.
* **Budgets \& alerts** — a student can set a monthly limit per category, track spend against it, and get warned at 80% and 100% usage.
* **Reports \& analytics** — this discusses the reports and analysis of student's monthly income/expense summary, category-wise spending breakdown, and month-over-month spending trend.
* **Financial Health Score** — a 0–100 score based on savings rate, budget adherence, and how much your spending is.
* **What-If Simulator** — see the projected effect on your savings if you changed spending in a category by some percentage.
* **Savings goal tracking** — set a target amount and date, and check progress against it.

## Technologies / Tools Used:-

We Have used basic level of Resources to get a simple tool rather than something which feels heavy and slow. Resources are:-

* **Java** (plain, no external frameworks)
* **CSV files** for data persistence (no database)
* **Git \& GitHub** for version control

## How to Install \& Run

1. Make sure you have a JDK installed. You can Check with:
   java -version
   javac -version
2. Clone this repository:
   git clone <repo-url>
   cd FinExpert
3. Compile the project from the project root (the folder containing `src/` and `data/`):
   javac -d out src/com/finexpert/\*.java src/com/finexpert/model/\*.java src/com/finexpert/storage/\*.java src/com/finexpert/service/\*.java src/com/finexpert/ui/\*.java
4. Run it:
   java -cp out com.finexpert.FinExpertApp
5. The app will create a `data/` folder automatically on first run if it doesn't already exist, and will start you off at the main menu.

## Instructions for Testing

There's no separate automated testing that took place, we have done the testing manually by running through each menu path ourselves:

* Adding, editing, deleting, and searching transactions, including invalid inputs (bad dates, non-numeric amounts) to confirm the app doesn't crash
* Setting budgets and checking that the budget-vs-actual view updates correctly as new expenses are added
* Checking the Financial Health Score and What-If Simulator against a few different sample data scenarios to confirm the numbers behave sensibly (e.g., cutting spending should always project higher savings)
* Closing and reopening the app to confirm data persists correctly in the CSV files between sessions

To try it yourself: run the app, add a few transactions across different categories, set a couple of budgets, and check out the Reports \& Analysis menu.

## Screenshots
1)Introduction:-
<img width="1391" height="355" alt="Screenshot 2026-09-18 185644" src="https://github.com/user-attachments/assets/a93e8695-e33f-418b-b6c8-eade18b5778d" />
2)Transactions:-
<img width="1478" height="937" alt="Screenshot 2026-09-18 185659" src="https://github.com/user-attachments/assets/48d0c0d8-25ae-44df-b250-73d9d2c2c851" />
3)Budgets:-
<img width="1448" height="752" alt="Screenshot 2026-09-18 185714" src="https://github.com/user-attachments/assets/fc46867c-e26e-42a8-a042-f98126033bb5" />
4)Report and analysis:-
<img width="1472" height="935" alt="Screenshot 2026-09-18 185725" src="https://github.com/user-attachments/assets/1007c245-3215-471a-9e35-3f21c2315947" />






