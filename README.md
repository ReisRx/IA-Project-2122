# Ms. Pacman Controller - Artificial Intelligence Final Project

For the final Project for this course, we were asked to create a controller for the Ms. Pacman framework.

## How to navigate through the repo

Inside the `src/screenpac/controllers` folder is my main implementation: `P2G14.java` file.

## How to run it

Run the main class in `src/screenpac/model/Game.java` file.

## How I implemented the Controller

My main thought process was to use some algorithm used earlier in the course, so I used the [A* Search Algorithm](https://en.wikipedia.org/wiki/A*_search_algorithm).
How it works is it tries to find a valid path from Ms. Pacman to the Nearest Pill. If the Ghosts are edible, it focuses on the Nearest Ghost.

If I was to make the Controller today, I would make it different. Updating the GameStates and using them to my advantage since this controller can't predict too far in the future.

