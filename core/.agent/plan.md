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
**Total Duration:** 21m 1s

### Task_1_Modular_Setup_and_Core: Establish the multi-module structure by creating :core:common and :core:ui. Configure Hilt, KSP, and build flavors (dev, staging, prod) with applicationIdSuffix in the :app module. In :core:common, define AppHealthTracker and AnalyticsTracker interfaces. In :core:ui, implement the Material 3 design system with vibrant colors, edge-to-edge support, and base Proguard/R8 configuration.
- **Status:** COMPLETED
- **Updates:** The initial project structure has been set up with :core:common and :core:ui modules. Hilt and KSP are configured across all modules. Build flavors (dev, staging, prod) are defined in :app. The Material 3 design system with vibrant colors and edge-to-edge support is implemented in :core:ui. The AppHealthTracker and AnalyticsTracker interfaces are defined in :core:common. The project builds successfully.
- **Acceptance Criteria:**
  - :core:common and :core:ui modules created
  - :app module configured with dev, staging, prod flavors
  - Hilt and KSP configured globally
  - Material 3 theme and tracking interfaces implemented
  - Project builds successfully
- **Duration:** 5s

### Task_2_Performance_Feature_Implementation: Create the :features:performance module. Implement the Performance Dashboard UI using Jetpack Compose to visualize metrics like TTID, TTFD, and frame render times. Implement the tracking interfaces to capture these metrics.
- **Status:** COMPLETED
- **Updates:** Successfully created the :features:performance module and implemented the Performance Dashboard UI using Jetpack Compose and Material 3. Implemented the AppHealthTracker and AnalyticsTracker interfaces in :core:common and provided Hilt bindings. Updated :app with a CoreNavHost to coordinate navigation. Captured TTID and TTFD metrics in MainActivity and visualized them in the dashboard. The feature exposes its own navigation graph and the project builds successfully.
- **Acceptance Criteria:**
  - :features:performance module created
  - Dashboard UI implemented with Compose
  - Metrics tracking (TTID, frame times) functional
  - Feature exposes its own navigation graph
- **Duration:** 6m 35s

### Task_3_App_Host_and_Navigation: Refactor the :app module into a thin host. Implement the NavHost using the 'Navigation Actions' pattern for decoupled navigation. Set up the global Hilt application and wire the performance feature into the main navigation.
- **Status:** COMPLETED
- **Updates:** Successfully refactored the :app module into a thin host and implemented the Navigation Actions pattern for decoupled navigation. Updated MainActivity to only handle system-level concerns and host the CoreApp composable. Set the Performance Dashboard as the start destination for the application. Verified that the :app module contains no business logic. The project builds successfully.
- **Acceptance Criteria:**
  - :app module serves as a thin host without business logic
  - Navigation Actions pattern correctly implemented
  - Performance dashboard is accessible on app launch
- **Duration:** 5m 11s

### Task_4_Final_Assets_and_Verification: Finalize the app by implementing an adaptive icon and integrating reportFullyDrawn() for TTFD tracking. Instruct critic_agent to verify application stability, Material 3 design alignment, and system health monitoring across flavors.
- **Status:** COMPLETED
- **Updates:** Successfully implemented the final refinements for the Core App. Module load tracking is now functional and displayed in the performance dashboard. Refactored PerformanceViewModel to inject the AppHealthTracker interface instead of the implementation. Removed manual status bar color settings from Theme.kt to rely on enableEdgeToEdge(). The critic_agent has verified these changes and confirmed that the app is stable, follows Material 3 guidelines, and correctly implements all requested performance tracking features. The project is now complete.
- **Acceptance Criteria:**
  - Adaptive app icon created and integrated
  - reportFullyDrawn() implemented for TTFD
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 9m 10s

