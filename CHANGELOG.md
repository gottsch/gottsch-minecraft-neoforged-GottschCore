**# Changelog for GottschCore 1.21.1 for NeoForge

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.10.0] - 2026-11-02

Brings the NeoForge build level with the Forge 1.20.1 GottschCore 2.10.0: the 2.9.0 and 2.10.0 work not already
ported in 2.7.0. See the Forge changelog for the full reasoning behind each item; the notes below cover what differs
on NeoForge 1.21.1.

### Fixed
- `SpawnUtil.spawnMob` no longer discards the mobs it creates. It treated a `null` from `Mob.finalizeSpawn` as a
  cancelled spawn, but that return is the spawn data passed in (here `null`) for most mobs, so most spawns were thrown
  away and proximity spawners spawned nothing. Finalizing now goes through `EventHooks.finalizeMobSpawn`, so
  `FinalizeSpawnEvent` fires, and its return value is ignored, as NeoForge's javadoc says it should be.
- `SpawnUtil.spawnMob` now rejects a position where the mob's bounding box clips geometry (`Mob.checkSpawnObstruction`,
  as vanilla's `BaseSpawner` does), so a spawned mob is no longer left inside a wall to suffocate.
- `SpawnUtil.spawnAndAddMob` now adds the mob it created and positioned. It used to add the un-positioned instance
  that was passed in.
- `MobSetDataHandler` clears the registry at the start of each reload, so removed or edited data packs no longer leave
  stale entries behind.
- `MobSetDataHandler` merge/replace now reads the `replace` flag from the incoming data instead of the existing entry.

### Changed
- `SpawnUtil`: creating and adding are separate steps. `spawnMob(...)` creates, positions and finalizes a mob but
  does not add it; `spawnAndAddMob(...)` adds it. Spawn positions are checked with the entity's
  `SpawnPlacementType` rather than `NaturalSpawner.isValidEmptySpawnBlock`. The overloads that took an unused
  `Entity mob` argument are deprecated and delegate to the new ones.
- `ProximityMobSetSpawnerBlockEntity.execute` delegates spawning to `SpawnUtil.spawnAndAddMob`, and picks a set from
  `mobSetNames` at trigger time when no single `mobSetName` is set.
- `MobSetDataRegistry` is backed by a `ConcurrentHashMap` (data pack reloads run off-thread); `get(null)` returns
  empty.
- `MobCount` normalizes counts to be non-negative with `min <= max`.
- `WeightedMob` is a plain record: the hand-written `getId()`/`getWeight()`, `equals`, `hashCode` and `toString` are
  gone. Use `id()` and `weight()`.

### Added
- `StructureMobSetBlockEntity` loads and saves its `mobSets` list.
- **Codec-backed NBT fields** (`mod.gottsch.neo.gottschcore.nbt`): `TagComponent`, `TagData` and `StackData`, with the
  same API as the Forge build so shared code compiles on both. On 1.21 `StackData` keeps its values in the stack's
  `minecraft:custom_data` component, which is where vanilla's data fixer moves a 1.20.1 stack's root NBT when a world
  is upgraded. For new 1.21-only item data, a real `DataComponentType` is the better choice.
- **`StrictCodecs`** (`mod.gottsch.neo.gottschcore.json`), used throughout `world.gen.structure.templatesystem`. On
  1.21's DFU a plain `optionalFieldOf` already fails on a malformed value, so here it mainly keeps parity with the
  Forge build and puts the field name in front of the error.
- **`world.gen.structure.templatesystem`**: `AgingProcessor`, `DecorationProcessor` and their datapack model
  (`AgingRule`, `AgingStage`, `BlockMatch`, `DecorationRule`, `WallGrowthRule`, `WeightedGrowth`), plus `BlockIds`
  and `LevelIndependentProcessor`. This includes weighted palettes, palette entries that name an entity instead of a
  block, and a warning for unknown block ids.
  - On 1.21 `StructureProcessorType.codec()` returns a `MapCodec`, so `AgingProcessor.codec(...)` and
    `DecorationProcessor.codec(...)` return one. Register with a `DeferredHolder` as shown in their class javadoc. To
    parse a processor on its own, use `codec(...).codec()`.
  - Entities grown by `DecorationProcessor` are finalized through `EventHooks.finalizeMobSpawn`.
- Test source set (JUnit 5): `AgingProcessorTest`, `DecorationProcessorTest`, `WeightedGrowthTest`,
  `StrictFieldsTest`, `BlockIdsTest` and `TagDataTest`, ported from the Forge build. They run through NeoGradle's
  `junit` run, which boots the game before the tests start.

### Deprecated
- `world.gen.structure.StructureProcessor` (the legacy `GottschTemplate` path). New processors should extend vanilla's
  `StructureProcessor` and register a `StructureProcessorType`. **Not scheduled for removal.**

## [2.7.0] - 2026-10-05

### Added
- CommandResponseFormatter, FormatterConstants and ReportBuilder (ported from the Forge 1.20.1 GottschCore). Shared chat formatting for mod commands: success/failure/warning/info messages, confirm prompts, and structured admin reports.
- DimensionCoords (ported from the Forge 1.20.1 GottschCore). An immutable Coords that also carries a dimension key and saves/loads it.

### Fixed
- WaterloggedFacingHalfBlock now keeps WATERLOGGED on placement. getStateForPlacement discarded the result of setValue, so a block placed into water pushed the water out instead of waterlogging.

## [2.6.0] - 2026-03-26

### Changed
- Update BST - dimensional aware.
- ProximitySpawnerBlockEntity.
- Box added new methods.
- SpawnUtil.
- Add Codec to Heading,

### Added
- MobCount, MobSetData, MobSetDataHandler, MobSetDataRegistry, WeightedMob
- StructureMobSetBlock, ProximityMobSetSpawnerBlockEntity, StructureMobSetBlockEntity
- MobSetConfiguration

## [2.5.1] - 2025-09-23

### Changed
- Fixed AbstractProximinityBlockEntity load() method to properly check for "proximity" value.
    - this had prevented proximity from loading properly and always defaulting to the 5D value.

## [2.5.0] - 2025-06-15

### Added

- Interval implementation for bst package.

## [2.4.0] - 2025-05-09

### Added

- IHalfBlock block interface
- HalfBlock block
- FacingHalfBlock
- WaterloggedFacingHalfBlock block
- ModUtil class - contains asLocation(), mcLocation() methods, spawnMob(), spawnAndAddMob() methods.

## [2.3.0] - 2024-10-27

### Changed
- deprecated usage of Coords constructors in favor of static of() methods.
- deprecated Quantity

### Added
- InterRange
- DoubleRange

## [2.2.0] - 2024-08-20

### Changed
- changed WeightedCollection.add() to synchronized. 

### Added
- WeightedCollection.remove(T key) method.
