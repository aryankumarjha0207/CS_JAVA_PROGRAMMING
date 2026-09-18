# Problem Statement

Most students don't really track where their money goes. Between food orders, subscriptions, random online shopping, and hanging out with friends, it's easy to lose track of spending — especially when there's no simple way to see it all in one place. Most existing finance apps are built for adults managing salaries, loans, and taxes, not for a student handling pocket money, a part-time stipend, or a monthly allowance.

FinExpert is a console-based Java application that addresses this by letting a student log their income and expenses, set a budget for different spending categories, and get a quick, honest picture of their financial habits. Beyond basic tracking, it computes a "Financial Health Score" based on savings rate, budget adherence, and spending diversity, and lets the user run "what-if" simulations (like "what if I cut my food spending by 20%?") to see the projected effect on their savings before actually making the change.

# Scope

The project is a solo submission for a VITyarthi flipped-course assignment, built entirely in plain Java with no external frameworks. Data is stored locally in CSV files rather than a database, and the interface is console-based rather than a GUI — both deliberate choices to keep the project focused on demonstrating core Java concepts (OOP, collections, file I/O, exception handling) rather than UI or database complexity.

The scope covers three core areas:

1. Transaction management (adding, editing, deleting, and searching income/expense records)
2. Budget setting and tracking, with alerts when spending approaches or exceeds a limit
3. Analytics and insights, including reports, the Financial Health Score, the What-If Simulator, and savings goal tracking

It does not cover multi-user support, bank account integration, or real-time currency data — all of that is out of scope for this assignment.

# Target Users

The primary target user is a student or young adult (broadly, Gen Z) managing a relatively small, irregular income — allowance, a part-time stipend, or similar — who wants a simple way to understand and improve their spending habits without needing a full-scale banking app.

# High-Level Features

- Add, edit, delete, and search income/expense transactions across fixed categories
- Set monthly budgets per category, with automatic tracking and threshold alerts (80% warning, 100% exceeded)
- View monthly summaries, category-wise spending breakdowns, and spending trends over time
- Financial Health Score — a computed 0–100 score reflecting overall money habits
- What-If Simulator — project the effect of a hypothetical spending change on savings
- Savings goal tracking — set a target amount and date, and monitor progress toward it
- All data persisted locally through CSV files
