## Introduction

This small project intends to provide an easy-to-use framework
to build 2D arcade and platform game only the JDK 20.

Building a game can be a game itself; or a nightmare. Depending on the tools, the team, the skills, … a lot of things
can
interact badly with the process of creating a game. And this is exactly what we won't talk about!:)

Here I will talk about creating an A-B-AB game with the only weapons provided by the nice Java JDK 19 :) And no, I won't
use JavaFX (beuarrk:)) I'll use the good old AWT and Swing (yes, I am maybe a 50 years old man…).

Anyway, let's try it through a series of posts to design some code.

## Creating a simple class

The basic Game class will support the famous "triptych" of the game loop: manage input, update objects, and draw
everything on screen!
But do you know why this is the basic default pattern for a game?

## A bit of video game history

Why the game loop exists?
This is an exceptional question and the reason why is a historically based answer.
Everything <starts from the first ever video game: PONG.

![Pong, where all begin !](https://cdn-images-1.medium.com/max/800/0*ySNC72GHeT19Nq3N "wikipedia PONG screenshot")

_figure 0.1 - Pong where all begins (ref:[https://fr.wikipedia.org/wiki/Pong](https://fr.wikipedia.org/wiki/Pong))_

The original Pong video game from wikipediaAt this very beginning time, the processor to execute tasks is very slow
on, almost some hundreds of Khz as CPU frequency. To understand the scale we are talking about, the current processor is
running at 2 to 4 GHz!
So the processor is very slow, each cycle of CPU is precious. So every line of code is very optimized and clearly
dedicated to some precise tasks.

And another element must be taken in account: the display process. At this time, the screen was not flat with a bunch
of LCD, but CRT ones. CRT display screen is based on ionic flow started from a cathode (electronic gun) and moving to
the anode (the screen grid) to excite fluorescent layer in the intern face of the glass bulb.

And swiping the all surfaces of the screen has a time cost: to display frame 25 per seconds, we need 16ms to swipe a
frame.
A CRT tube with its ion gun! The CRT Tube is nothing more than a big bubble light. (3) the cathode emits ions (1) and
(2) are anodes, deflecting ion ray to screen, lighting a fluorescent dot.

![A CRT diagram with ions gun and anodes deflectors](images/figure-crt.jpg "A CRT diagram with ions gun and anodes deflectors (c) myself with my own hands !")

_figure 0.2 - A CRT diagram with ion gun and anodes deflectors_

This is the available time for the CPU to prepare next image!

So capturing input, moving things and displaying things must be done in 16ms. And loop again for the next frame.

So the main process is a LOOP. that's why we talk about a Game Loop:

![The basic Game loop explained with a pencil: the method to keep a fixed frame rate !](images/figure-game-loop.jpg "the basic Game loop explained with a pencil: the method to keep a fixed frame rate ! (c) myself with my own hands !")

_figure 0.3 - The basic Game loop explained with a pencil: the method to keep a fixed frame rate !_

There is also some advanced version of the Game Loop, where multiple updates can be performed between each rendering
phase, the timer is around the update methods only:

![The advanced method to keep a fixed update rate](images/figure-game-loop-fixed.jpg "The advanced method to keep a fixed update rate (c) myself with my own hands !")

_figure 0.4 - The advanced method to keep a fixed update rate_

I can only invite you to read the fantastic book from Robert Nystrom for details about the Game loop.

> **Note:** diagram is largely inspired by the Robert Nystrom book, thanks to him to share his own knowledge!

Anyway, I need to implement my own. As a good diagram is better than word:

![A good diagram explaining the Game class and its usage](http://www.plantuml.com/plantuml/png/VOynRiOm28Ltdu9koT0BPAZIew0OjKWO8nihLKzVbAc9VddnW3_UzzYxO_DH9BtvDnK24sHD9Hvu_u1FC-43dQjke7IcVuF7Doixe_0lBUiD2-wAs-0BGy0QIZtejQCDdJYEHkYKf5Q3eSu2zCEU06QrRFYo1eFYeDN1S7JACRYjFPiCow6w5adpLD42e1ReDF14zc22dHoOkLlj5DmUu2Zo1m00 "figure 5 - A good diagram explaining the Game class and its usage")

_figure 0.5 - A good diagram explaining the Game class and its usage_
