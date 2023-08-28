Feature: Scene has Entity

  Any Scene will support internally only Entity, despite this Entity is a sound,
  an animated object of a virtual not displayed one.

  Scenario: Scene has Entity map.
    Given A Test Scene "scene01" is created
    When I add a new  Entity named "entity01" to this Scene "scene01"
    Then The Entity "entity01" appear in the Scene "scene01" entity list
