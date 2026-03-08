# Project Plan

Create a modern Android app called 'Core App' with a modular architecture.
The app should use Jetpack Compose and follow these structural requirements:
- :app (Main Module): Thin host, NavHost, global Hilt setup, no business logic.
- :core:common: Shared logic, AppHealthTracker interface for metrics (startup, render time).
- :core:ui: Shared design system, Material 3 themes, reusable composables.
- :features:[feature-name]: Individual modules for each flow, exposing their own navigation graph.

Best Practices & Config:
- Build Flavors (dev, staging, prod) with distinct applicationIdSuffix.
- Base Proguard/R8 configuration for Compose and Hilt.
- Hilt for DI across all modules.
- Jetpack Navigation using 'Navigation Actions' pattern.
- AnalyticsTracker interface in :core:common (app_startup_time, module_load_time, frame_render_time).
- Use reportFullyDrawn() for TTFD tracking.
- Material Design 3 (M3) with a vibrant, energetic color scheme.
- Full Edge-to-Edge Display.
- Adaptive app icon.

## Project Brief

# Core App Project Brief

A high-performance, modular Android application designed as a benchmark for architectural excellence and system health monitoring. The project demonstrates a scalable multi-module structure using the latest Jetpack Compose and Material 3 standards.

### Features
* **Real-Time Performance Dashboard**: A dedicated interface to visualize key app health metrics, including Time to Initial Display (TTID), Time to Full Display (TTFD), and frame render times.
* **Modular Feature Navigation**: A decoupled navigation system that allows the `:app` module to coordinate between independent feature modules using the Navigation Actions pattern.
* **Adaptive Design System**: A centralized Material 3 design library in `:core:ui` providing a vibrant, energetic color scheme, full edge-to-edge support, and adaptive iconography.
* **Automated Health Tracking**: Integrated `AppHealthTracker` and `AnalyticsTracker` interfaces that automatically capture startup and module loading metrics across the entire application lifecycle.

### High-Level Tech Stack
* **Language**: Kotlin
* **UI Framework**: Jetpack Compose with Material Design 3
* **Architecture**: Modular (Clean Architecture principles)
* **Dependency Injection**: Hilt
* **Code Generation**: KSP (Kotlin Symbol Processing)
* **Asynchronous Programming**: Kotlin Coroutines & Flow
* **Navigation**: Jetpack Navigation Compose (Navigation Actions pattern)
* **Performance Monitoring**: Jetpack Tracking (`reportFullyDrawn`)

## Implementation Steps

### Task_1_Modular_Setup_and_Core: Set up the multi-module project structure including :core:common and :core:ui modules. Configure Hilt for Dependency Injection, KSP, and create Build Flavors (dev, staging, prod) in :app. Implement the Material 3 design system with a vibrant, energetic color scheme, edge-to-edge support, and the AppHealthTracker/AnalyticsTracker interfaces in :core:common.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Modules :core:common and :core:ui are created and configured.
  - Hilt and KSP are set up across all modules.
  - Build flavors (dev, staging, prod) are defined in build.gradle.kts.
  - Material 3 theme and edge-to-edge support are implemented in :core:ui.
  - Project builds successfully after modularization.
- **StartTime:** 2026-03-06 22:34:32 BRT

### Task_2_Feature_Dashboard_Performance: Create the :features:performance (or dashboard) module. Implement the Real-Time Performance Dashboard UI using Jetpack Compose to visualize metrics such as TTID, TTFD, and frame render times. Develop concrete implementations for AppHealthTracker and AnalyticsTracker to capture these metrics across the app lifecycle.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Module :features:performance is created.
  - Dashboard UI is implemented with M3 components.
  - AppHealthTracker captures startup and render metrics correctly.
  - The feature module exposes its own navigation graph.

### Task_3_App_Host_and_Navigation: Configure the :app module as a thin host. Implement the Jetpack Navigation 'Navigation Actions' pattern and NavHost. Set up the Hilt Application class and global dependency management. Connect the feature module to the main NavHost.
- **Status:** PENDING
- **Acceptance Criteria:**
  - NavHost is implemented in the :app module.
  - Navigation Actions pattern is used for decoupled navigation.
  - Hilt Application is correctly configured.
  - The app successfully navigates to the Performance Dashboard on startup.

### Task_4_Assets_and_Verification: Finalize the application with an adaptive icon, implement reportFullyDrawn() for accurate TTFD tracking, and perform a final verification run. Ensure stability across all build flavors and alignment with user requirements.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Adaptive app icon is generated and integrated.
  - reportFullyDrawn() is called in appropriate UI states for TTFD reporting.
  - App builds and runs without crashes on all flavors.
  - Verified alignment with Material Design 3 and performance tracking requirements.

