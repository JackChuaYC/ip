# Yawned User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Command aliases

Use these case-insensitive shortcuts in place of the corresponding lowercase command word:

| Alias | Command |
| --- | --- |
| `t` | `todo` |
| `d` | `deadline` |
| `e` | `event` |
| `l` | `list` |
| `m` | `mark` |
| `u` | `unmark` |
| `del` | `delete` |
| `f` | `find` |

For example, `T buy milk` is equivalent to `todo buy milk`, and `DeL 2` is equivalent to
`delete 2`. The `bye` command has no alias. Canonical commands remain lowercase.

### Custom aliases

Create or replace a persistent alias with `alias <name> <command>`. Names contain letters only and are
case-insensitive; targets must be one of `todo`, `deadline`, `event`, `list`, `mark`, `unmark`, `delete`,
or `find`.

```text
alias hw todo
HW finish assignment
```

Remove a custom alias with `alias remove <name>`:

```text
alias remove hw
```

Custom aliases are saved in `data/aliases.txt` and remain available after restarting Yawned. They cannot
replace canonical command words, built-in aliases, `alias`, or `remove`; they also cannot target `alias` or
`bye`.

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
