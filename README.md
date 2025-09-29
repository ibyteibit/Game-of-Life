# Game of Life - *Personal Project*
![game of life main image](https://github.com/ibyteibit/Game-of-Life/blob/main/pics/GameofLife.png)
## Description
- An interactive adaptation of Conway’s Game of Life
- A variety of simulation tools are available to the user including zoom/pan, animated coordinate find, autozoom, speed adjustment, random cell generation, live statistics, and a maximizable 1000x1000 grid
- Users can customize colorful “species” with specified rules for survival and mutation
- Pre-programmed pattern templates, like gliders and oscillators, are also at the user’s disposal
### Add Species Menu & Template Menu
![species image](https://github.com/ibyteibit/Game-of-Life/blob/main/pics/Species.png) ![template image](https://github.com/ibyteibit/Game-of-Life/blob/main/pics/Templates.png)

## Tools
- EclipseIDE
## Methodology
- Researched extensively Java Swing classes and GridBagLayout to program the UI
- Developed manually the grid rendering and zoom/pan functions through much trial and error
- Prevented errors by dummy-proofing all user inputs
- Managed ArrayLists for Parent and Child species with code to decide what species to place in a cell and to allow for editing/deleting species
- Designed my own buttons (DevButtons) built on JPanels that fade between colors on hover
- Accounted for fractional values in graphics with a double-to-int converter that takes a loop index input
### Class Heirarchy
![class heirarchy](https://github.com/ibyteibit/Game-of-Life/blob/main/pics/ClassHeirarchy.png)
## Build Instructions
1. Download .java files in the [src](https://github.com/ibyteibit/Game-of-Life/tree/main/src) folder
2. Compile and run them with your favorite Java IDE
