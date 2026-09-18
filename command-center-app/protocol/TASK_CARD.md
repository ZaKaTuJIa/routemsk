# TASK CARD template

```yaml
id: RM-0001
project: ROUTEMSK
title:
created_at:
priority: P0|P1|P2|P3
status: READY
owner: COMMANDER
executor: DEV|SEO|RESEARCH|CONTENT|QA|LEADS
reviewer: QA
goal:
business_value:
inputs:
  - link_or_file:
constraints:
  - no_production_write_without_approval
scope:
  included:
  excluded:
steps:
  - 
expected_result:
definition_of_done:
  - functional_result_verified
  - mobile_or_target_platform_verified
  - error_state_verified
  - regression_check_passed
  - evidence_attached
risks:
rollback:
evidence:
  - screenshot_or_log_or_url:
qa_result: PENDING|PASS|FAIL
approval: NOT_REQUIRED|REQUIRED|APPROVED|REJECTED
next_action:
```

## Completion message

Executor must report:
- What changed
- Where changed
- Evidence
- Known limitations
- Risks
- QA REQUIRED

Never report only “готово”.
