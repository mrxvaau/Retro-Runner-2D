# Arcade Super Mario-Type Game

Welcome to the **Arcade Super Mario-Type Game**, a fun and engaging 2D arcade game built using Java! This project is designed to replicate the classic Super Mario experience with custom features and levels. It’s a tile-based game where players can explore, jump, and collect items to achieve the highest score. The project is completely open-source and can be used, modified, and shared freely without any restrictions.

---

## Features

- **Tile-Based Gameplay**: Navigate through maps with custom-designed tiles.
- **Smooth Animations**: Character and environment animations provide an immersive experience.
- **Custom Levels**: Multiple levels designed for varied gameplay experiences.
- **Easy Customization**: Modify maps, sprites, and mechanics to create your own version of the game.
- **Keyboard Controls**: Intuitive input handling for player actions.
- **Lightweight and Portable**: Runs smoothly on any Java-supported system.

---

## Installation and Setup

Follow these steps to get the game running on your system:

### Prerequisites
- **Java Development Kit (JDK)**: Ensure you have JDK 8 or later installed.
- **IDE (Optional)**: You can use IntelliJ IDEA, Eclipse, or any Java-supported IDE for development.

### Steps
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/Arcade-Super-Mario-Type-Game.git
   ```

2. **Navigate to the Project Directory**:
   ```bash
   cd Arcade-Super-Mario-Type-Game
   ```

3. **Compile the Source Code**:
   ```bash
   javac -d bin src/com/projectoop1aiub/edu/**/*.java
   ```

4. **Run the Game**:
   ```bash
   java -cp bin com.projectoop1aiub.edu.test.GameCore
   ```

5. Enjoy the game!

---

## How to Play

- Use the arrow keys to move the character left or right.
- Press the spacebar to jump.
- Collect coins, stars, and hearts to increase your score.
- Avoid enemies and obstacles to survive.
- Reach the end of the level to win!

---

## Project Structure

```
src/
├── com.projectoop1aiub.edu
    ├── graphics
    │   └── Animation, ScreenManager, Sprite
    ├── input
    │   └── GameAction, InputManager
    ├── test
    │   └── GameCore
    └── tilegame
        └── sprites
            └── GameEngine, MapLoader, TileMap, TileMapDrawer
maps/
    └── Level configuration files (.txt)
images/
    └── Sprites, tiles, and other assets (.png, .jpg)
```

---

## Customization Guide

### Modifying Levels
- Add or edit level files in the `maps` folder.
- Use simple text-based configuration to design new maps.

### Changing Sprites
- Replace or add new sprites in the `images` folder.
- Update the `Sprite` and `TileMap` classes to use your new assets.

### Adding Features
- Extend the `GameEngine` or `GameCore` classes to implement new gameplay mechanics.

---

## Contribution Guidelines

We welcome contributions from the community! To contribute:
1. Fork the repository.
2. Create a feature branch.
3. Submit a pull request with a clear description of your changes.

---

## License

This project is licensed under the **MIT License**, meaning you are free to use, modify, and distribute it without restrictions. See the `LICENSE` file for more details.

---

## Acknowledgments

Special thanks to:
- **AIUB**: For inspiration and support.
- The developers of classic arcade games for their timeless ideas.

---

Enjoy the game! If you have any questions or feedback, feel free to reach out or create an issue in the repository.

