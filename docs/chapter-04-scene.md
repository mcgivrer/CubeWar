## Scene and Scene Manager

`Scene` interface and the `AbstractScene` class are providing a structure to support dedicated game play implementation.

In a game (ou main `Application` class intends to be the core structure of a game), any state is a gameplay:

- the main title is a specific gameplay where you can choose to select a new game or exit this game,
- the main game screen is a dedicated gameplay, so a scene where the player can be moved and interact with enemies
  and/or other NPCs (Non-Player-Characters),
- the map display is also a specific gameplay, showing the game map and some inventory information.

All those states are `Scene` implementations.


