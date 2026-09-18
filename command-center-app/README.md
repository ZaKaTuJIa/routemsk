# IVAN COMMAND CENTER v0.3

Android package: `ru.ivan.commandcenter`  
Version: `0.3.0` (`versionCode 4`)

## v0.3
- keeps the dark/neon control-center direction;
- adds factual team states: `WORKING`, `IDLE`, `BLOCKED`, `MANUAL`;
- reads ROUTEMSK Night Shift live state from `runtime.json`;
- reads overnight report from `latest.json`;
- shows completed work, changed items, checks, blockers and next tasks;
- adds AI Hub links for ChatGPT, Gemini and Claude;
- caches the last successful feeds locally.

## Command Center v2 protocol

Operational source of truth is stored in `command-center-app/protocol/`:

- `OPERATING_SYSTEM.md` — workflow, roles, statuses, priorities and approval rules;
- `TASK_CARD.md` — mandatory task contract and Definition of Done;
- `BUG_CARD.md` — mandatory detailed bug report and retest lifecycle;
- `AGENT_SKILLS.md` — contracts for COMMANDER, DEV, SEO, RESEARCH, CONTENT, QA and LEADS;
- `KNOWLEDGE_AND_LOGS.md` — knowledge structure and event log;
- `KOSCHEI_AUTOMATION_RUNBOOK.md` — safe launch plan for the first Unity automation.

Core rule: executor completion is not task completion. Only QA PASS plus required human approval can close work.

## Transport feeds
The app reads only the dedicated branch `command-center-reports`:
- `reports/command-center/team.json`
- `reports/routemsk/runtime.json`
- `reports/routemsk/latest.json`

No credentials, STS contents, phone numbers, e-mails or legal requisites should ever be written to these public feeds.

## Signing
The original private key for v0.1–v0.2.1 was not recoverable. A new permanent v0.3+ signing key is stored separately in the user's private ChatGPT Library and must never be committed here.

Because the signing certificate changes, the first migration from installed v0.2.1 to v0.3 requires a one-time uninstall/reinstall. Future versions signed with the permanent v0.3 key can update normally over v0.3.
