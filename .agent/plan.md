# Project Plan

Self Improve: A Daily Habit & Self-Improvement Tracker app styled like a physical journal using native Android (Java), XML, and Material Design 3. We need to implement:
1. Day Header with streak/date.
2. Habit checklist using a RecyclerView with custom toggle mechanisms (Success/Failed states).
3. Core hardcoded habits (No Porn, No Sugar, No Fast Food, 7 Hrs Sleep, 2 KM Running, Workout at Home, Cold Shower, Study > 1 Hr, Screen Time < 3 Hrs).
4. Daily Evaluation section with Day Status and Regrets/Notes.
5. Daily Motivation quote section.
All built in Java with custom XML designs meeting MD3 specifications.

## Project Brief

# Project Brief: Daily Habit & Self-Improvement Tracker (Self Improve)

An elegant, minimalist daily journal-inspired tracker designed to foster daily habit-building and active self-reflection. The interface replicates the experience of a physical journal, styled with Material Design 3 guidelines using native Android (Java) and XML layouts.

---

### Features

1. **Physical Journal-Style Day Header**
   * Displays the current date and an active habit-streak counter (e.g., "7-Day Streak 🔥") to motivate consistency and reward daily check-ins.
2. **Interactive Habit Checklist (Success/Failed Toggles)**
   * Replaces traditional checkboxes with dual-state toggle buttons inside a list (`RecyclerView`).
   * Color-coded dynamically (soft green for Success, soft red for Failed) to reflect instant status updates.
3. **Daily Reflection & Evaluation Inputs**
   * Features a status assessment and text input fields for recording "Regrets & Notes," prompting daily mindfulness and journaling.
4. **Daily Motivation Corner**
   * A dedicated section at the bottom of the layout presenting inspiring daily motivational quotes using crisp typography and clean card styling.

---

### High-Level Technical Stack

* **Language**: Java (Android SDK)
* **UI & Layouts**: XML Layouts featuring **Material Design 3 (M3)** components (e.g., `MaterialCardView`, `MaterialButton`, `TextInputEditText`, `RecyclerView`)
* **List Management**: `RecyclerView` with custom adapter support for the habit check-off list
* **Architecture / Source Files**:
  * `activity_main.xml`: Main layout containing the Day Header, Habit checklist list view, Daily Evaluation inputs, and Daily Motivation card.
  * `habit_item.xml`: Individual habit layout displaying habit description alongside Success/Failed toggle buttons.
  * `Habit.java`: Data class modeling individual habits and their status.
  * `HabitAdapter.java`: Adapter linking the list of habits to the `RecyclerView`.
  * `MainActivity.java`: Host controller managing the lifecycles, user inputs, habit list initialization, and toggle states.

## Implementation Steps
**Total Duration:** 11m 45s

### Task_1_UI_Foundation: Configure the Material 3 theme with a vibrant, journal-style color scheme. Create XML layouts including activity_main.xml (header, recycler view, evaluation, quotes) and habit_item.xml (habit description with dual-state toggles).
- **Status:** COMPLETED
- **Updates:** Material 3 theme configured with journal-style colors. activity_main.xml and habit_item.xml created with all required sections and custom toggles. MainActivity.java updated for Edge-to-Edge. Build successful.
- **Acceptance Criteria:**
  - Material 3 theme is correctly configured in themes.xml and colors.xml.
  - activity_main.xml includes all required sections (Header, RecyclerView, Evaluation, Motivation).
  - habit_item.xml features custom toggle buttons for Success/Failed states.
- **Duration:** 9m 6s

### Task_2_Logic_Implementation: Create the Habit.java data model. Implement HabitAdapter.java with logic for handling dual-state toggles. Initialize MainActivity.java with the hardcoded habit list and bind data to the UI.
- **Status:** COMPLETED
- **Updates:** Habit.java, HabitAdapter.java, and MainActivity.java implemented in Java. RecyclerView populated with the 9 specified habits. Success/Failed toggles provide color-coded feedback. Edge-to-Edge support maintained. Build successful.
- **Acceptance Criteria:**
  - Habit.java and HabitAdapter.java are implemented in Java.
  - MainActivity.java correctly populates the RecyclerView with the specified 9 habits.
  - Toggles update habit states with color-coded feedback (green for success, red for failure).
- **Duration:** 56s

### Task_3_Polish_And_Assets: Enable Full Edge-to-Edge display. Design and implement an adaptive app icon matching the journal theme. Refine typography and spacing to achieve the minimalist journal aesthetic.
- **Status:** COMPLETED
- **Updates:** Edge-to-Edge display fully implemented with inset handling. Typography and spacing refined for a vibrant journal aesthetic. Custom vector icons added to habit toggles. Adaptive app icon finalized. Build successful.
- **Acceptance Criteria:**
  - App displays edge-to-edge (behind status/navigation bars).
  - Adaptive app icon is present and matches the app theme.
  - UI matches the vibrant and energetic Material 3 journal aesthetic.
- **Duration:** 1m 43s

### Task_4_Run_And_Verify: Perform a final build and run the app. Verify all features: streak counter, habit toggling, evaluation input, and motivational quotes. Check for stability and crashes.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - The project builds successfully.
  - App does not crash on launch or during interaction.
  - All features (habits, evaluation, quotes) are functional and UI looks correct.
- **StartTime:** 2026-06-27 01:16:17 BDT

