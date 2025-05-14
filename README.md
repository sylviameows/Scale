**[Scale]**
Allow players to set their scale attribute easily.

> [!WARNING]
> This project now requires **KittiLib** (https://github.com/sylviameows/Kitti/), make sure to install the Core jar and place it in your server directory before updating beyond `3.0.0`!

***
**[Shrinking Potions]**
- Tier I: Shrinks the player 50%
- Tier II: Shrinks the player 75% (25% regular scale)

Crafted by using a `Brown Mushroom` on a `Thick Potion` in a brewing stand. Tier II can be made by adding a `Diamond` to Tier I in a brewing stand.

**[Growth Potions]**
- Tier I: Grows the player 100% (2x scale), increase reach and jump height.
- Tier II: Grows the player 300% (4x scale), increase reach and jump height.

Crafted by using a `Red Mushroom` on a `Thick Potion` in a brewing stand. Tier II can be made by adding a `Diamond` to Tier I in a brewing stand.

> `Thick Potions` = `Water Bottles` + `Glowstone` in a brewing stand.
> **Splash variants** can be made by adding `Gunpowder`

**[Commands]**
These commands work independent of potion effects, as the potion effects are modifiers to a users set scale value.
- `/scale`: Set a scale value between 0.88 and 1.11
- `/oscale`: Set a scale value between 0.1 and 16 (requires `scale.override` permission)

**[Permissions]**
- `scale.others`: Allows a player to set the scale of other players.
- `scale.override`: Allows a player to run the /oscale command, setting values to gameplay altering values.
