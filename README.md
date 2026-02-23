# Pac-Man Java Implementation

A Pac-Man implementation in Java, featuring clever ghost movement, procedural map generation, and custom map persistence.

## Key Features

### Clever Ghost movement

- **Dijkstra Pathfinding**: Ghosts navigate the maze using a weighted Dijkstra algorithm.
- **Ghost Density Map**: To prevent overlapping, ghosts track each other's positions. Tiles occupied by other ghosts incur a weight penalty, encouraging them to spread out.
- **Dual Modes (Chase & Scatter)**: The game features a "Director" logic that flips every 10 seconds between **CHASE** (aggressive pursuit) and **SCATTER** (retreating to home corners).
- **Unique Personalities**:
  - **Blinky (Red)**: Directly targets Pac-Man's current tile.
  - **Pinky (Pink)**: Aims 4 tiles ahead of Pac-Man to cut him off.
  - **Inky (Blue)**: Uses a sophisticated vector logic between Blinky and Pac-Man to trap the player.
  - **Clyde (Yellow)**: Chases until close, then retreats to his corner, creating a "hit-and-run" behavior.

### Smooth Movement & Input

- **Input Buffering**: Implemented input buffering to ensure smooth turns. If you press a direction before reaching an intersection, Pac-Man will automatically turn at the first available opportunity.

### Procedural Map Generation

- **DFS Generation**: Maps are procedurally generated using a Depth-First Search (DFS) algorithm for a sprawling maze feel.
- **Mirror Symmetry**: The algorithm generates the left half and mirrors it to ensure classic arcade aesthetics and perfect balance.
- **Playability Verification**: Every generated map is verified using a Flood Fill (BFS) check to guarantee all areas are reachable.

### Map Management

- **Persistence**: Save and load custom generated maps as `.txt` files.
- **In-Game Registry**: A UI to browse, name, and load your custom creations.

## Class Diagram

```mermaid
classDiagram
    direction TB
    class GameState {
        <<enumeration>>
        MENU
        GENERATING_MAP
        PLAYING
        ALGORITHM_VISUALIZATION
        GAME_OVER
        REGISTERED_MAPS
        SAVING_MAP
        VICTORY
    }

    class GhostMode {
        <<enumeration>>
        CHASE
        SCATTER
    }

    class Block {
        #int x, y, width, height
        #Image image
        #int tileSize
        #Rectangle bounds
        +draw(Graphics g)
        +getBounds() Rectangle
    }

    class Entity {
        +int velocityX, velocityY
        +int speed
        +char currentDirection
        +char pendingDirection
        #int startX, startY
        +move(HashSet walls)
        +canMoveInDirection(char dir, HashSet walls)
        #setDirection(char dir)
        +reset()
    }

    class PacmanPlayer {
        +updateImage()
    }

    class Ghost {
        -char type
        -Image normalImage, scaredImage
        -boolean isScared
        -long respawnReadyTime
        +calculatePath(char[][] grid, int targetR, int targetC, int[][] densityMap)
        +isAtIntersection(char[][] grid)
        +isAtCenterOfTile() boolean
        +move(HashSet walls)
        +eat()
        +setScared(boolean scared)
    }

    class Wall {
    }

    class Food {
        -boolean isPower
    }

    class PacMan {
        -GameState currentState
        -GhostMode currentGhostMode
        -HashSet walls, foods, ghosts
        -PacmanPlayer pacman
        -int score, lives
        -int[][] ghostDensityMap
        +generateNewMap()
        +startGame()
        +updateGame()
        +loadMapFromGrid(char[][] grid)
        -handlePacmanDeath()
        -resetEntities()
    }

    class MapGenerator {
        -int rows, columns
        -char[][] grid
        -List visitOrder
        +generate() char[][]
        +verifyConnectivity(int startR, int startC) boolean
        -carve(int r, int c)
        -applyMirrorAndRule()
        -removeDeadEnds()
    }

    class GameRenderer {
        +render(Graphics g, GameState state, ...)
    }

    class MapStorage {
        +saveMap(String name, char[][] grid)
        +loadMap(String name, int rows, int cols) char[][]
        +getRegisteredMapNames() String[]
    }

    class PlayerInput {
        +keyPressed(KeyEvent e)
    }

    Block <|-- Entity
    Block <|-- Wall
    Block <|-- Food
    Entity <|-- PacmanPlayer
    Entity <|-- Ghost
    PacMan "1" *-- "many" Wall
    PacMan "1" *-- "many" Food
    PacMan "1" *-- "many" Ghost
    PacMan "1" *-- "1" PacmanPlayer
    PacMan "1" --> "1" GameState
    PacMan "1" --> "1" GhostMode
    PacMan ..> MapGenerator : uses
    PacMan ..> MapStorage : uses
    PacMan ..> GameRenderer : uses
    PlayerInput --> PacMan
```

## Build & Run

### Prerequisites

- JDK 17 or higher
- Windows (`build.bat`) or Linux/Mac (`build.sh`)

### How to Run

1. Run the build script: `.\build.bat` (Windows) or `./build.sh` (Unix).
2. Use the menu UI or keyboard (1-4) to start playing!

## Technical Details

- **GUI**: Java Swing / AWT
- **Persistence**: `java.nio.file` API
- **Graphics**: Custom `GameRenderer` with layered rendering for gameplay and UI.
