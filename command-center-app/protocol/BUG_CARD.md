# BUG CARD template

```yaml
bug_id: BUG-RM-0001
project: ROUTEMSK
title:
found_at:
found_by:
environment: production|staging|local
location:
severity: P0|P1|P2|P3
status: OPEN|TRIAGED|IN_FIX|RETEST|CLOSED
preconditions:
steps_to_reproduce:
  - 
actual_behavior:
expected_behavior:
frequency: always|intermittent|once
evidence:
  - screenshot_log_url:
suspected_cause:
impact:
  production:
  seo:
  cro:
  analytics:
  security:
workaround:
owner:
fix_summary:
regression_scope:
retest_result:
closed_at:
```

## Lifecycle

BUG FOUND → QA CARD → COMMANDER TRIAGE → EXECUTOR FIX → QA RETEST → CLOSED

P0/P1 bugs cannot be hidden inside a general report. They require a separate card and immediate escalation.
