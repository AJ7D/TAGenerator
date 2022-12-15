# TAGenerator
TAGenerator is a system produced to propagate the creation, sharing and playing of textual adventure games. It provides simplified tools for creatives to design games by abstracting programming concepts behind an intuitive graphical user interface. A built in engine allows for these games to be played and shared with others.

# Features
- Create your own textual adventure game with custom rooms, items and enemies, then export and share with your peers
- Define the language for players to interact with your virtual world by adding custom action verbs
- Reload your game into the generator to make any updates you see fit
- Share your game files with others for collaborative efforts
- Play games other users have made through the integrated engine
- Save and load your game states so you can play for however long you want to, when you want to

### Items
Item templates make it easy for you to create items with the behaviour you want and expect. Currently supported:
- Default (viewable items with no practical use)
- Consumable
- Weapon
- Container
- Key

### Enemies
Enemies attack the player and can:
- be passive (only attack the player when attacked) or aggressive (attack player when in the same room)
- be given items to hold which will be dropped on enemy defeat

# Images
### Generator
<img src="https://github.com/AJ7D/TAGeneratorGUITest/blob/master/generator.PNG?raw=true" width="600" height="400" />
The generator offers a comprehensive overview of the current game configuration and provides a multitude of ways to develop your game further.

### New Game Objects
<img src="https://github.com/AJ7D/TAGeneratorGUITest/blob/master/newitem.PNG?raw=true" width="600" height="400" />
Item templates speed up the process of creating items and verb configuration can be used to control exactly how your item is interacted with.

### Engine
<img src="https://github.com/AJ7D/TAGeneratorGUITest/blob/master/engine.PNG?raw=true" width="600" height="400" />
The flexible text parser can handle a wide range of potential input, making it easier for players to communicate with the game.

# Installation
- Download the source files.
- Download JavaFX jar file, then include as a dependency in the project
