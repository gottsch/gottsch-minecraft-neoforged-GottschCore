**# Changelog for Mage Flame 1.20.1

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.1.0] - 2025-02-25

### Changed

- Fixed Bubble Flame's default attributes.
- Included Glowglob in loot modifiers.
- Included fishing/junk and fisining/treasure loot tables for loot modifiers.
- Fixed Ember Hounds light adjustment when wounded.
- Updated Treasure2 integration loot tables to include Bubble Flame scroll, Ember Hound scroll and Glowglob Ball.

### Added

- Bubble Flame glow layer


## [2.0.0] - 2025-01-26

### Changed

- Dynamic lighting is now powered by LambdAurora's LambDynamicLights
- Enabled multiple light entities per player at the same time
- Improved player-light entity registration. All entities are restored on world load with correct lifespan remaining
- Removed entity shadows from entities (mage flame etc)
- Updated Scroll item textures
- Changed scroll texture for Winged Torch
- Fixed spelling/text for scroll tooltips
- Replaced changelog.txt with CHANGELOG.md
- Replaced manual asset and data files with datagen

### Added

- Bubble Flame entity (can go underwater)
- Ember Hound entity
- Glowglob entity (throwable)
- Lifespan display HUD on entities when hovered over
- Command to remove summoned entities
- Datagen generated asset and data files
- Patchouli support and entries

## [1.6.0] - 2024-09-13
- Add Patchouli entries


## [1.5.0] - 2024-02-20

### Changed

- Fixed Update URL
- 
### Added

- Built-In Treasure2 integration. ie. scrolls/items will be injected into Treasure2 loot.
- Patchouli book support. Added to your inventory on first join.
