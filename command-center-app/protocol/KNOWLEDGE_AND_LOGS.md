# Knowledge Base and Event Log

## Required structure

```
knowledge/
  ROUTEMSK/
    PRODUCT/
    DEV/
    API/
    SEO/
    COMPETITORS/
    CLIENTS/
    BUGS/
    DECISIONS/
  KOSCHEI/
    GAME_DESIGN/
    UNITY/
    CHARACTERS/
    ART/
    BUILDS/
    BUGS/
    DECISIONS/
  BLOG/
  BUSINESS/
  AGENTS/
logs/
  TASKS/
  EVENTS/
  APPROVALS/
```

## Event record

```yaml
event_id:
timestamp:
project:
actor:
type: TASK_CREATED|STATUS_CHANGED|BUG_FOUND|QA_PASS|QA_FAIL|APPROVED|REJECTED|DEPLOYED|DECISION
object_id:
summary:
evidence:
next_action:
```

## Knowledge rule

Before starting: load PROJECT KNOWLEDGE + ROLE SKILL + TASK CARD.  
After finishing: update evidence, decision log, known limitations and next action.  
Chat is an interface, not the source of truth.
