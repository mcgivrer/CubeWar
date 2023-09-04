## Scene and Scene Manager

`Scene` interface and the `AbstractScene` class are providing a structure to support dedicated game play implementation.

In a game (ou main `Application` class intends to be the core structure of a game), any state is a gameplay:

- the main title is a specific gameplay where you can choose to select a new game or exit this game,
- the main game screen is a dedicated gameplay, so a scene where the player can be moved and interact with enemies
  and/or other NPCs (Non-Player-Characters),
- the map display is also a specific gameplay, showing the game map and some inventory information.

All those states are `Scene` implementations.

### Scene interface

Here is the Scene interface methods detail:

```java
public interface Scene {
    String getName();

    void create(Application app);

    void input(Application app, InputHandler ih);

    void update(Application app, double elapsed);

    void draw(Application app, Graphics2D g, Map<String, Object> stats);

    void dispose();

    void addEntity(Entity<?> entity);

    void setWorld(World world);

    Camera getActiveCamera();

    Collection<Entity<?>> getEntities();

    Collection<SceneBehavior> getBehaviors();

    Entity<?> getEntity(String entityName);

    void clearScene();
}
```

You may notice the Scene management methods, the game loop lifecycle ones and the utilities & helpers:

#### Scene management

All the required operation to manage a `Scene` from the `SceneManager`

* **getName():String** method used to get the internal name for that scene, used to retrieve it in the SceneManager
  scene's list,
* **create(Application):void** to start the scene, we need to create its content, this is the goal of the create method,
* **dispose()** and when you quit a scene, this is how the full content is released/disabled, freeing resources in
  memory.

#### Game lifecycle

The main Game loop may also control the Scene operation, these methods are the entry point for those game loop steps:

* **input(Application, InputHandler):void** manage specific input for that `Scene`,
* **update(Application, double ):void** if the scene needs specific object update operation, this is where you must
  implement this,
* **draw(Application, Graphics2D, Map<String, Object>):void** and upon the already existing Renderer mechanism, you may
  have to add some specific processing for the rendering opetartion, you can implement your own draw in this method.

### AbstractScene: default operation

_TODO_

### SceneManager to rule'them all

_TODO_
