# IVAN COMMAND CENTER v2 — Operating System

Status: ACTIVE  
Owner: IVAN  
Orchestrator: COMMANDER

## Core pipeline

INTAKE → TASK CARD → EXECUTOR → EVIDENCE → QA GATE → COMMANDER → HUMAN APPROVAL → PRODUCTION → LOG

No task is complete when an executor says “done”. It is complete only after QA PASS and, for production-impacting changes, IVAN APPROVAL.

## Roles

- COMMANDER: clarifies outcome, creates task cards, assigns work, controls priorities and escalations.
- DEV: code, integrations, deployment preparation, technical fixes.
- SEO: semantics, indexation, metadata, search architecture and evidence.
- RESEARCH: market, competitors, sources, assumptions and fact-checking.
- CONTENT: copy, content plan, offers, briefs and channel adaptation.
- QA: independent acceptance, regression checks and bug cards.
- LEADS (next): prospect discovery, mini-audits and personalized outreach drafts.
- KNOWLEDGE BASE: project facts, decisions, APIs, assets and history. It is not an agent.

## Statuses

BACKLOG → READY → IN_PROGRESS → QA_REQUIRED → QA_PASS | QA_FAIL → APPROVAL_REQUIRED → DONE  
Blocked work uses BLOCKED with reason, owner and exact unblock action.

## Hard rules

1. Every task has one owner, expected result and Definition of Done.
2. Every material claim must include evidence or be marked ASSUMPTION.
3. DEV/SEO/CONTENT/RESEARCH cannot approve their own work.
4. QA FAIL returns the task to the executor with a Bug Card.
5. Production changes require rollback notes.
6. Any discovered bug gets a detailed Bug Card, not a chat mention.
7. Every decision and completed task is written to the Event Log.
8. Knowledge belongs in project folders, not only in chat history.
9. Human approval is mandatory for money, publication, production, external messages and destructive actions.
10. 3D office/UI polish is deferred until this pipeline works reliably.

## Priority

P0 — service/data/security failure; stop other work.  
P1 — blocks release, leads, payment, SEO indexation or core flow.  
P2 — important degradation with workaround.  
P3 — improvement/cosmetic debt.

## Project namespace

- RM — ROUTEMSK
- KS — КОЩЕЙ
- BL — БЛОГ
- FN — ДЕНЬГИ
- AP — АГЕНТНАЯ ПЛАТФОРМА

Task example: RM-0142. Bug example: BUG-RM-0031.
