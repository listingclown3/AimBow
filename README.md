## AimBow 1.8.9

Credit goes to [Michael Zang](https://github.com/michaelzangl) for the original [Minebot/Aimbow](https://github.com/michaelzangl/minebot/tree/forge-1.8.9/AimBow) project
<br>I simply added to the project with the help of LegoNinja48 and updated the compatibility to 1.8.9 (amazing wrapper!)

## Features

- 1.8.9 Forge Minecraft functionality
- Graphs the trajectory of projectiles, arrows, fishing rods, etc.
- Customizable trajectory colors
- Crosshair fixing (Not really functional?)
- Block distance (works sometimes)

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
- Smart autoaiming (predicts how to hit entity based on its speed, direction, etc)
  1. static auto aim forward based on the immediate direction a player is moving at the release of the arrow
  2. dynamic auto aim which accounts for different move paths and convolution of a path such like on a bridge (probably not needed)
- Backward aim finder (recommended by melumi in which you can find where a player is looking and their trajectory)
  1. could be quite computationally expensive depending on how we run this, but is feasible.
- More customizability (will merge PR from Cal)
  1. needs to have enderpearls and all other types of projectiles
  2. needs to fix block highlighting (make this customizable)

## Suggestions

Suggestions and Fixes are always appreciated, if you find a bug or have a suggestion, open a pull request or open a issue request!
