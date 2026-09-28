# Island Connect 🏝️

**Island Connect** is a captivating Android puzzle game built entirely with modern Android development practices using **Kotlin** and **Jetpack Compose (Material 3)**.

The objective is simple yet challenging: place and rotate bridge tiles to connect all isolated islands into a single unified network!

---

## ✨ Features

- 🧩 **Engaging Puzzle Mechanics**: Place straight, corner, T-shape, or cross bridge tiles to establish valid network connections between all islands.
- 🎨 **Modern Material 3 Design**: Supports system Light & Dark themes along with custom board themes.
- 🔊 **Rich Audio & Haptics**: Immersive sound effects and tactile feedback for tile selections, rotations, placements, island connections, and level completions.
- ⏱️ **Timer & Move Counter**: Challenge yourself to complete puzzles with minimum moves and faster times.
- ↩️ **Undo Support**: Easily undo previous tile placements to refine your strategy.
- 📊 **Player Statistics & Preferences**: Persistent tracking of total games played, puzzles solved, best moves, and best completion times backed by **AndroidX DataStore**.
- 📱 **Smooth Responsive UI**: Fluid animations and clean navigation powered by Jetpack Compose & Navigation Compose.

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with **Material 3**
- **Architecture:** MVVM (Model-View-ViewModel) with `StateFlow` and Kotlin Coroutines
- **State Management:** Reactive state streams using `StateFlow` and `collectAsState`
- **Data Persistence:** [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Navigation:** Jetpack Navigation Compose
- **Target SDK:** 37 (Android 16) | **Min SDK:** 24 (Android 7.0)

---

## 📂 Project Structure

```text
com.example.islandconnect/
├── audio/          # Sound effects and SoundPool manager
├── data/           # DataStore preferences & game settings model
├── haptics/        # Haptic feedback integration
├── logic/          # Network engine and puzzle connectivity algorithms
├── model/          # Game state models (BoardCell, BridgeTile, IslandCell, etc.)
├── ui/             # GameViewModel & Compose Navigation Graph
│   ├── components/ # BoardView, GameControls, GameHud, TileTrayView, etc.
│   ├── screens/    # HomeScreen, GameScreen, HowToPlayScreen, SettingsScreen, etc.
│   └── theme/      # Color, Type, and Material 3 Theme setup
└── MainActivity.kt # Main entry point
```

---

## 🎮 How to Play

1. **Select a Tile**: Choose a bridge tile from your tile tray at the bottom.
2. **Rotate**: Tap the rotate button to orient the tile in the required direction (North, East, South, West).
3. **Place Tile**: Tap an empty cell on the grid to place your tile.
4. **Connect Islands**: Form continuous path networks connecting all islands on the board without leaving dead ends or broken connections.
5. **Win**: Once every island is part of a single connected network, you win!

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (Ladybug / 2024.2.1 or newer recommended)
- JDK 11 or higher
- Android Device or Emulator running Android 7.0 (API level 24) or higher

### Building the Project

1. **Clone the repository:**
   ```bash
   git clone https://github.com/abdullah-ateeq/Island-Connect.git
   cd Island-Connect
   ```

2. **Open in Android Studio:**
   - Select **File > Open** and select the `IslandConnect` directory.

3. **Build & Run:**
   - Let Gradle sync dependencies.
   - Connect an Android device or start an emulator.
   - Click **Run (Shift + F10)**.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
