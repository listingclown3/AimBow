## AimBow 1.8.9

Credit goes to [Michael Zang](https://github.com/michaelzangl) for the original [Minebot/Aimbow](https://github.com/michaelzangl/minebot/tree/forge-1.8.9/AimBow) project
<br>I simply added to the project with the help of LegoNinja48 and updated the compatibility to 1.8.9 (amazing wrapper!)

## Features

- 1.8.9 Forge Minecraft functionality
- Graphs the trajectory of projectiles, arrows, fishing rods, snowballs, eggs, ender pearls, potions, and fishing rods
- Customizable trajectory colors
- Crosshair fixing (Not really functional?)
- Block distance (works sometimes)
- Hit-face block highlighting (highlights the specific face the projectile will land on)
- Entity hit highlighting (red bounding box on entities in the trajectory path)
- Smart autoaim with target prediction (stationary, physics, path, and vector modes)
  - 🔴 Red ghost = Physics mode (target is airborne/jumping)
  - 🟢 Green ghost = Path mode (target is on a bridge)
  - 🟣 Purple ghost = Vector mode (target sprinting in open field)
  - Ghost brightness = prediction confidence
- `J` toggles trajectory, `Y` toggles autoaim

## Explaining the Math
- [Script/Explanation](https://cdn.chrisccluk.live/files/misc/aimbow-code-explanation.pdf)
- [Put this on 2x Speed Please]([https://www.youtube.com/channel/UCyjficiLV37wXhT3VBkp0ew](https://www.youtube.com/watch?v=IVCrKG-963A))!!!

## Demonstration video
[Latest Video](https://youtu.be/P63V_68TwI0)<br>
[Video](https://youtu.be/TnqWZ5gxEG4)<br>
[Code/Math Breakdown](https://www.youtube.com/watch?v=IVCrKG-963A)

## Extra Showcase
VIDEO:
[CE v1.3](https://youtu.be/iK6PLwxdkgQ)
CallenFlyn

<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/cce1a69b-d4fa-476c-aa60-6e80bb452c06" />
J toggles trajectories
Y toggles aimbot

<img width="2877" height="558" alt="image" src="https://github.com/user-attachments/assets/bf39561f-4859-4997-b079-588133777a5c" />


## Todo
- Backward aim finder (recommended by melumi in which you can find where a player is looking and their trajectory)
  1. could be quite computationally expensive depending on how we run this, but is feasible.
- More customizability
  1. block highlighting color/style should be configurable
  2. autoaim FOV and rotation speed should be exposed in config

## Autoaim Config
Edit `src/main/java/net/famzangl/minecraft/aimbow/SmartAimConfig.java` to tune behavior:
- `MAX_AIM_ANGLE = 30f` — max degrees the bot will snap from your current look
- `MAX_ROTATION_SPEED = 10f` — degrees/tick rotation speed (smoothness)
- `HITBOX_BIAS = 0.25f` — how far ahead of a moving target to aim (lag compensation)
- `RENDER_GHOST = true` — show/hide the prediction ghost
- `GHOST_ALPHA = 0.3f` — ghost transparency

## Known Limitations
- Path prediction assumes the target keeps moving forward — misses if they stop
- Bridge detection may false-positive on small platforms
- Hitbox bias is constant and doesn't scale with target speed (can overshoot slow targets)
- Only one target can be locked at a time

## Completed
- ~~Smart autoaiming~~ — static aim (stationary targets) and dynamic aim (moving targets, bridge detection) both implemented
- ~~Ender pearl trajectory~~ — dedicated solver with correct physics
- ~~Block highlighting~~ — now shows only the specific hit face instead of full wireframe box

## Suggestions

Suggestions and Fixes are always appreciated, if you find a bug or have a suggestion, open a pull request or open a issue request!
