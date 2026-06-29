# Project Context: Self Improve

## Overview
Self Improve is a modern habit-tracking Android application. It has been fully migrated from a legacy Java/XML architecture to a 100% Kotlin and Jetpack Compose tech stack, utilizing the latest Android development practices.

## Tech Stack
- **Language**: 100% Kotlin.
- **UI Framework**: Jetpack Compose with Material 3.
- **Architecture**: MVVM with Repository pattern and Dependency Injection (Hilt).
- **Database**: Room 2.7.0 (Kotlin-first with Flow and Coroutines).
- **Asynchronous Work**: Kotlin Coroutines, StateFlow, and WorkManager.
- **Navigation**: Navigation 3 (Compose-centric, type-safe).
- **Key Libraries**:
    - **Hilt**: Dependency injection.
    - **WorkManager**: Periodic sync and daily reminders.
    - **DataStore**: Persistent user settings (e.g., Dark Mode).
    - **Adaptive Layouts**: List-Detail patterns for large screens.
    - **Retrofit & Moshi**: Network layer for cloud sync.
    - **Coil**: Image loading (ready for use).

## Project Structure
- `MainActivity.kt`: Entry point with Navigation 3 host.
- `HabitViewModel.kt`: Central state management using StateFlow and Hilt.
- `HabitRepository.kt`: Clean data layer abstraction.
- `di/`: Hilt modules for Database and Network injection.
- `data/`: `SettingsManager` for DataStore preferences.
- `ui/`:
    - `history/`: `HistoryScreen` with Adaptive List-Detail layout.
    - `theme/`: Material 3 theme and color definitions.
    - `NavDestinations.kt`: Type-safe navigation keys.
- `worker/`: `ReminderWorker` for notifications and `SyncWorker` for cloud backup.
- `api/`: Retrofit service definitions.
- `Habit.kt`: Room Entity with status, category, and date tracking.

## Current Features
- **Daily Habit Tracking**: Interactive checklist with expanding cards and micro-animations.
- **Categorization**: Habits are grouped by Physical, Mental, or Productivity.
- **Dynamic Streaks**: Automated calculation of success streaks (🔥) based on historical data.
- **Advanced Analytics**: Weekly consistency bar charts in the History screen.
- **Enhanced Reminders**: Users can set specific reminder times for each habit.
- **Adaptive History**: Multi-pane List-Detail layout for viewing past performance.
- **Persistent Settings**: Dark Mode support via DataStore.
- **Simulated Cloud Sync**: Periodic background synchronization.
- **Data Integrity**: Automatic database seeding and status persistence.

## Future Improvements (Planned/Suggested)
- **Real Backend Integration**: Replace the simulated sync with a real Firebase or custom API.
- **Advanced Analytics**: Add graphs and consistency charts using a library like MPAndroidChart or custom Compose drawing.
- **Enhanced Reminders**: Allow users to set specific reminder times for each habit.
- **Habit Streaks**: Calculate and display streaks more dynamically based on historical data.
- **UI Polish**: Add more micro-animations and custom Material 3 motion.
- **Photo Proof**: Allow users to attach photos to habits using the available CameraX dependency.
- **Widget Support**: Add Home Screen widgets for quick habit marking.
