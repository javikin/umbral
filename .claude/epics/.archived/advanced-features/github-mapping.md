# GitHub Issue Mapping

Epic: #39 - https://github.com/javikin/umbral/issues/39

## Tasks

| Local File | Issue | Title |
|------------|-------|-------|
| 40.md | #40 | Setup Glance Dependencies |
| 41.md | #41 | Widget de Estado |
| 42.md | #42 | Widget de Streak |
| 43.md | #43 | Quick Settings Tile |
| 44.md | #44 | Stats Database Schema |
| 45.md | #45 | Stats Screen UI |
| 46.md | #46 | Stats Charts & Top Apps |

## Dependencies

```
#40 (Setup) ──┬──> #41 (Widget Estado) ──┐
              └──> #42 (Widget Streak) ──┤ (parallel)
                                         │
#43 (Quick Settings) ────────────────────┤ (parallel, no deps)
                                         │
#44 (Stats DB) ──> #45 (Stats UI) ──> #46 (Charts)
```

## Quick Links

- Epic: https://github.com/javikin/umbral/issues/39
- Tasks: https://github.com/javikin/umbral/issues?q=label%3Aepic%3Aadvanced-features

Synced: 2026-01-06T03:04:35Z
