# КОЩЕЙ — automation launch runbook

Status: PREPARED, NOT STARTED  
Launch condition: IVAN is at the computer and explicitly starts the run.

## Goal of first automation

Create a safe repeatable loop for Unity work:
GOAL → GAME TASK → IMPLEMENTATION → BUILD/TEST → QA → RESULT LOG

## Before launch

- Confirm Unity project path and Unity version.
- Confirm Git repository/branch and clean or understood working tree.
- Create a backup/commit checkpoint.
- Confirm Android SDK/JDK and target device or emulator.
- Select exactly one first vertical slice. Recommended: playable Кощей scene with movement, camera follow and one ability.
- Define acceptance checks and screenshots/video required.

## Agents

COMMANDER → GAME_DESIGN + UNITY_DEV → QA → IVAN APPROVAL

For first run, reuse DEV as UNITY_DEV and RESEARCH as GAME_DESIGN. Do not create extra agents until the loop passes twice.

## First task pack

1. KS-0001 PROJECT AUDIT — scene, scripts, packages, build settings, current errors.
2. KS-0002 VERTICAL SLICE SPEC — controls, camera, one ability, win/fail condition.
3. KS-0003 IMPLEMENT — smallest playable slice.
4. KS-0004 ANDROID BUILD TEST — install, launch, controls, performance smoke test.
5. KS-0005 QA REPORT — evidence, bugs, next release candidate.

## Stop conditions

- Missing backup/checkpoint.
- Unknown project path or wrong Unity version.
- Compilation errors not understood.
- Automation attempts destructive project-wide rewrite.
- Production/external publishing without approval.

## Success criteria

- Project opens without compile errors.
- Target scene runs.
- Player movement and camera work.
- One Кощей ability works.
- Android build installs and launches.
- QA PASS includes evidence.
- All bugs and decisions are logged.
