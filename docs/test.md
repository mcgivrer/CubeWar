# Application Class Specification

## Package

- Located in `com.snapgames.core`.

## Overview

The `Application` class appears to act as the core or main class for a game application. It manages initialization,
configuration, the launching of the game loop, and the creation of scenes.

## Imports

- ... _(The previously mentioned list of imports)_

## Class Attributes

- **FPS**: Represents the number of frames per second, defaulted to 60.
- **UPS**: Represents the number of updates per second, defaulted to 120.
- **exit**: A boolean flag to determine whether the application should exit.
- **pause**: A boolean flag to determine if the application is paused.
- ... _(Other attributes)_

## Constructors

- **Application()**: The default constructor which loads the default message file from i18n/messages.properties.

## Public Methods

- **run(String[] args)**: Acts as the entry point to run the application. It oversees the initialization process, window
  creation, scene setup, and primary game loop execution.
- **createScenes()**: An abstract method that child classes should implement to define scenes.
- ... _(Other methods)_

## Private Methods

- **initialize(String[] args)**: Initializes the application by loading the configuration settings. It can take
  command-line arguments to customize the initialization process.
- ... _(Other methods)_

## UML Activity Diagrams

### run Method

```plantuml
@startuml
:start
:Initialize application with args;
:Initialize services;
:Load translated application name and version;
:Create window and input handler;
:Create scenes;
:Execute game loop;
:Dispose if not in test mode;
:Print exit messages;
:stop
@enduml
```

### initialize Method

```plantuml
@startuml
:start
:Convert args to list;
:Load configuration with list of args and default config path;
:Set testMode, exit, and pathToConfigFile;
:stop
@enduml
```

### initializeService Method

```plantuml
@startuml
:start
:Get SystemManager;
:Create new game loop;
:Add internationalization system to SystemManager;
:Add various systems to SystemManager (PhysicEngine, SpacePartition, etc.);
:Initialize SystemManager with application context;
:stop
@enduml
```

### loop Method

```plantuml
@startuml
:start
:Execute the game loop for application;
:stop
@enduml
