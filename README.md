# inf218-game

This repository contains the source code for a game project in the context of an informatics class I took back in 2018.

It is archived, because I wish to preserve the code as it was for the foreseeable future. When I feel like it, I may or
may not unarchive it and open it to contributions.

## Features

* Inbuilt level editor (figure out the password)
* Own format for writing the level data to a text file
* Crude physics and render engine, written from scratch
* "Hydra-mode", by changing a single variable to a different value
* Absolute mess of a codebase because we did not know better back then

## Known Issues
* Codebase is a mess
* It may or may not be super efficient in terms of FPS and whatever else
* Single-threaded
* More of a proof-of-concept than an actual game

## Structure

```
|-- res
| |-- levels
| |-- sounds
| | |-- music
| |-- textures
| | |-- characters
| | | |-- test
| | |-- tileMaps
|-- src
| |-- components
| |-- coreEngine
| |-- enumerations
| |-- utility
README.md
```

Game assets like sound and textures are found in `res`.

Code responsible for rendering, inputs, and physics is located in `src/coreEngine`.

Individual game components that need to be rendered in reside inside `components`, for example the player sprite rendering.

Enums that I have forgotten their use for are found in `src/enumerations`. If I recall correctly, we used them for
state-tracking in animations, and at least one other thing.

Lastly, like any good beginner project we have a `utility` package with various "utilities" that may or may not be better off
being somewhere else. Individual files apart from the README are not listed, that would be way too much.

## License

This project is licensed under the terms of The 3-Clause BSD License (SPDX-identifier BSD-3-Clause). Refer to the
`LICENSE` file for more details.
