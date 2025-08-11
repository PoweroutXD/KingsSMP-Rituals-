# KingsSMP-Rituals (Paper 1.21.x)

A focused plugin for Kings SMP that adds ritual crafting and PvP-focused legendary items.

## Highlights
- 30-minute global ritual when crafting any legendary (boss bar + spinning item visible to all)
- One-per-server limit for each legendary (persisted in data.yml)
- Abilities via `/ability`:
  - Dragon Bone Blade: temporary dragon-breath clouds + roar (duration/cooldown in config)
  - Avian Trident: grants creative-style flight for a limited time
  - Void Bow: first arrow marks a spot; second arrow teleports you or the target there (10s cooldown)
- Life Stealer: on-kill grants +2 hearts up to 14 total
- King's Crown: permanent Strength III, Fire Resistance, Resistance I while worn
- Bucket of Mulk: strong food item, no ritual, unlimited crafting

## Build
- Java 17+, Maven
- `mvn package` -> drop the shaded jar into `plugins/`

## Notes
- Recipes are **not** registered here—add them with a datapack or another plugin, then set the result items to these custom items via commands/admin or give.
- The Ender Dragon "rideable" concept is intentionally *not* spawned here to avoid world grief; replace `dragonBreath` with your preferred safe implementation.
