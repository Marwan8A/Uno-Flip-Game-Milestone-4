# UNO Flip Game – Milestone 4

A GUI-based implementation of the UNO Flip card game developed in Java with Swing, featuring full UNO Flip mechanics, AI Player functionality, and advanced game state management including Undo/Redo, Save/Load, and Replay capabilities.

---

## Project Overview

This project implements an UNO Flip card game for 2-4 players with a graphical user interface. The game follows UNO Flip rules with support for AI players, number cards, action cards (SKIP, REVERSE, DRAW_ONE, DRAW_FIVE, SKIP_EVERYONE), and wild cards (WILD, WILD_DRAW_TWO, WILD_DRAW_COLOR). Players compete in rounds until one player reaches 500 points. The game now includes full state persistence with undo/redo functionality, XML-based save/load system, and replay capabilities.


**Version:** 4.0  
**Milestone:** 4  
**Language:** Java 17+  
**Authors:** Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari

---

## Features

### Core Gameplay
- 2–4 Player Support (Human + AI-capable)
- Full UNO Flip deck implementation:
  - Light Side & Dark Side cards
  - Numeric cards, SKIP, REVERSE, DRAW_ONE, DRAW_FIVE
  - Wild cards: WILD, WILD_DRAW_TWO, WILD_DRAW_COLOR
  - FLIP cards toggle the entire deck, players' hands, and active colors
- Full scoring system based on UNO Flip values
- Turn management including reversed direction, skips, and multi-step actions

#### Game State Management (Milestone 4 additions)
- **Undo/Redo System**
  - Full game state restoration using deep-copy stack-based architecture
  - Undo reverts to previous turn state (before play/draw/nextPlayer)
  - Redo re-applies undone actions
  - Accessible via Menu → Undo/Redo
  - State saved before every mutating operation: play(), drawAndPass(), nextPlayer(), startGame()
  
- **Save/Load Functionality**
  - XML-based game serialization preserving complete game state:
    - All player hands, names, scores, human/AI status
    - Draw pile and discard pile with card orientations
    - Turn order, direction, active color, and flags
    - Saved human/AI player counts for replay
  - File chooser dialogs for save/load operations
  - Default filename: `uno_save.xml`
  - Error handling with user-friendly dialogs
  - Automatic scoreboard reconstruction on load
  
- **Replay System**
  - Restart entire game session from game-over dialog
  - Clears undo/redo history and initializes fresh round
  - Preserves original human/AI player configuration
  
### GUI Features
- **Menu Bar** with File operations:
  - Undo – revert last action
  - Redo – reapply undone action
  - Save – export game state to XML
  - Load – restore game from XML filepresentation for both Light and Dark sides
- Interactive card playing via mouse clicks
- Automatic updates when AI plays cards
- Pop-up dialogs for wild card color selection
- Display for current player, next-player status, scores, and active color
- Notifications for round completion and game end
- Dynamic scoreboard rebuilding after game load

---

## How to Run

### Prerequisites
- Java Development Kit (JDK) 17+
- Swing library (included with JDK)

### Option 1: Run the JAR file
```bash
cd C:\Users\kings\Downloads\UnoFlip
java -jar UnoFlip_M4.jar
```

### Option 2: Compile and Run from Source

Navigate to source directory:
```bash
cd C:\Users\kings\Downloads\UnoFlip\UNO
```

Compile all classes:
```bash
javac *.java
```

Run:
```bash
cd ..
java UNO.UnoView
```

---

## Gameplay Instructions

### Starting the Game
1. Launch the application  
2. Select the number of players (humans + optional AI players)  
3. Game deals 7 cards per player  
4. Starting card chosen from Light side

### Your Turn
- Click a valid card to play it
- Click **Draw Card** if no legal play is possible
- Click **Next Player** to pass turn  

### AI Turns
- AI automatically:
  - Chooses a legal card
  - Plays it (including action cards)
  - Selects colors for wild cards
  - Draws when no valid card exists

### Winning
- Round winner: first to empty hand
- Score based on opponents' remaining cards
- Game ends at **500 points** for any player

### Using Undo/Redo
- **Undo**: reverts last play/draw/pass
- **Redo**: reapplies undone action
- Undo/redo stack clears on new game or replay

### Saving and Loading
- **Save**: choose file location
- **Load**: select saved XML file
- Scoreboard automatically adjusts to loaded player count
- Game resumes from exact saved state

### Replaying
- At game-over dialog: choose "Yes" to replay
- Clears all history and starts fresh with same player config

---

## Game Rules

### UNO Flip Rules 
- All cards have **Light/Dark sides**
- Flip cards invert:
  - Discard pile orientation  
  - Draw pile orientation  
  - All player hands  
  - Active color  
- Additional Dark-side action cards (DRAW_FIVE, etc.)
- WILD_DRAW_COLOR: next player draws until the declared color appears

### Card Values
Updated to match UNO Flip scoring.

### Legal Play Requirements
- Matching color (active color)
- Matching number
- Matching type
- Wild cards always legal

---

## Deliverables

### Milestone 4 Deliverables
1. **Source Code (UNO/)**  
   - Full undo/redo implementation using UnoStack architecture  
   - XML serialization and deserialization for complete game state  
   - Replay system with history management  
   - Menu bar integration for Save/Load/Undo/Redo  
   - New class: `UnoStack` for state management  

2. **UML Class Diagram**  
   - Updated with Milestone 4 attributes and methods:
     - `UnoStack` class (new)
     - `UnoModel`: undo/redo/replay, toXML/parseXML, state management fields
     - `UnoView`: menu bar, file choosers, scoreboard rebuild
     - `UnoController`: handlers for Undo/Redo/Save/Load/Replay
     - `Card`, `Deck`, `Hand`: toXML serialization methods
   - Reflects full MVC and Observer pattern

3. **UML Sequence Diagrams**  
   - Undo operation flow (state restoration)
   - Save/Load game state flow (XML serialization)

4. **Documentation**  
   - Descriptions of all design changes since Milestone 2  
   - Updated README, user instructions, design decisions  
   - Javadoc & inline comments  

5. **Testing**  
   - Unit tests for undo/redo stack operations
   - tests for save/load XML roundtrip

---

## Team Contributions

### Milestone 4

| Team Member      | Primary Contributions                                                                                           |
|------------------|-----------------------------------------------------------------------------------------------------------------|
| Noor Karabala    | Implemented XML serialization/deserialization, designed save/load file format, integrated menu bar GUI          |
| Abdullah Hasan   | Developed UnoStack deep-copy architecture, implemented undo/redo logic, controller action handlers              |
| Marwan Alakhras  | Added replay system, state restoration methods, scoreboard rebuild logic, error handling for file operations    |
| Yousif Al-Satari | Updated UML diagrams with Milestone 4 changes, created sequence diagrams, documentation, README updates         |

### Milestone 3

| Team Member      | Primary Contributions                                                                                           |
|------------------|-----------------------------------------------------------------------------------------------------------------|
| Noor Karabala    | Integrated dual-sided card logic, UI updates for Flip cards, enhanced GUI responsiveness                        |
| Abdullah Hasan   | Implemented AI turn execution, expanded controller logic, assisted with model refactoring                        |
| Marwan Alakhras  | Added flip mechanics to deck/discard/hand, updated scoring logic, resolved model edge cases                     |
| Yousif Al-Satari | Updated UML diagrams, created AI sequence diagrams, refined design documentation, produced README & deliverables |

---

## Milestone Roadmap

### Milestone 1 (Completed)
- CLI implementation  
- Basic Model code  
- Initial UML  

### Milestone 2 (Completed)
- GUI implementation  
- Initial Swing View + Controller  

### Milestone 3 (Completed – November 24, 2025)  
- UNO Flip support  
- AI Player logic  
- All tests passing  
- Refactored, smell-free code  

### Milestone 4 (Current – December 5, 2025)
- Redo/Undo functionality  
- Replay system  
- Save/Load (Serialization)  

---

## License
Academic project for SYSC 3110 @ Carleton University.  
UNO is a copyrighted product — all use is for educational purposes only.

---

## Contact
For questions or issues, please contact the development team:
- **Noor Karabala** – NoorKarabala@cmail.carleton.ca
- **Abdullah Hasan** – BASSELHASAN3@cmail.carleton.ca
- **Marwan Alakhras** – MarwanAlakhras@cmail.carleton.ca
- **Yousif Al-Satari** – yousifalsatari@cmail.carleton.ca

---

**Last Updated:** December 5, 2025  
**Milestone:** 4
