# Backgammon Multiplayer Game

A fully functional multiplayer Backgammon game built with Java as part of the Computer Network Concepts course project at Fatih Sultan Mehmet Vakıf Üniversitesi.

## Features
- Two-player online multiplayer over TCP sockets
- Complete Backgammon rules (dice, bar, bearing off, win detection)
- Graphical user interface built with Java Swing
- Server hosted on AWS EC2
- Supports multiple simultaneous games

## Project Structure
- `server/GameServer.java` — Central relay server (AWS EC2)
- `client/GameClient.java` — Network client
- `client/GamePanel.java` — Main game board UI
- `client/BoardPanel.java` — Start screen
- `game/BackgammonBoard.java` — Game logic

## How to Run
1. Start the server (running on AWS at `56.228.22.133:6000`)
2. Run `GameWindow.java` on two machines
3. Enter the server IP and start playing

## Technologies
- Java (Swing, Sockets, Threads)
- AWS EC2 (Amazon Linux)
- Git & GitHub

## Author
Saed O. S. Radi — 2321051365
Fatih Sultan Mehmet Vakıf Üniversitesi — 2026
