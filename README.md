# AFK-Omega
AFK Omega - Minecraft Mod

Features:
- AFK Support!
  - Sends message to all players when someone goes or returns from AFK
- Manual AFK Support:
  - Use /AFK command or hotkey (defaults to '\]') to toggle AFK status.
    - Has 2-second input delay in order to prevent spamming
- Automatic AFK Support:
  - Go AFK if no client input is detected for 2+ minutes
  - Return from AFK if the player moves or interacts with anything
    - Ignores pause-menu or looking around

TO-DO:
- Add AFK tag to Player List (Tab menu)
- Possibly add overlay/aura/effects around player when AFK
- Add configuration options for server hosts
  - File-based with in-game commands to alter it

Known Bugs:
- First trigger of AFK double-taps, thinking the player moved.