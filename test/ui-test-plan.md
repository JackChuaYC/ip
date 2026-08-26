# UI test plan

## Run command

Compile all files in `src/main/java` with Java 25, then run `Yawned` with the compiled classes on the classpath. Run each test case in a fresh console session.

Before each case, set `data/Yawned.txt` to the stated **Initial storage** value. `None` means the file must not exist; restore any pre-existing file after the test run.

## Test case: Add and list each task type

**Aim:** Verify that to-dos, deadlines, and events are created with ISO dates, and that dates are displayed as `MMM dd yyyy` when listed.

**Initial storage:** None

**Inputs:**

```text
todo borrow book
deadline return book /by 2026-01-01
event project meeting /from 2026-01-02 /to 2026-01-03
mark 1
list
bye
```

**Expected output:**

```text
____________________________________________________________

========================
         YAWNED
   Your sleepy chatbot
========================

*Yawns..* You woke me up...
What do you want?

____________________________________________________________

____________________________________________________________

Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
  [D][ ] return book (by: JAN 01 2026)
Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
  [E][ ] project meeting (from: JAN 02 2026 to: JAN 03 2026)
Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________

finally, that's done:
  [T][X] borrow book
____________________________________________________________

____________________________________________________________

Here you go, the tasks in your list:
1.[T][X] borrow book
2.[D][ ] return book (by: JAN 01 2026)
3.[E][ ] project meeting (from: JAN 02 2026 to: JAN 03 2026)

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```

## Test case: Reject invalid ISO dates

**Aim:** Verify that invalid deadline and event dates are rejected without adding tasks, while a later valid ISO date still works.

**Initial storage:** None

**Inputs:**

```text
deadline return book /by 2026-02-30
event project meeting /from 2026-01-02 /to tomorrow
deadline submit report /by 2026-12-31
list
bye
```

**Expected output:**

```text
____________________________________________________________

========================
         YAWNED
   Your sleepy chatbot
========================

*Yawns..* You woke me up...
What do you want?

____________________________________________________________

____________________________________________________________

Please use a valid date in yyyy-MM-dd format.
____________________________________________________________

____________________________________________________________

Please use a valid date in yyyy-MM-dd format.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
  [D][ ] submit report (by: DEC 31 2026)
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Here you go, the tasks in your list:
1.[D][ ] submit report (by: DEC 31 2026)

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```

## Test case: Load persisted ISO dates

**Aim:** Verify that persisted deadlines and events load as `LocalDate` values and display in `MMM dd yyyy` format with their completion states intact.

**Initial storage:**

```text
D | 1 | return book | 2026-01-01
E | 0 | project meeting | 2026-01-02 | 2026-01-03
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________

========================
         YAWNED
   Your sleepy chatbot
========================

*Yawns..* You woke me up...
What do you want?

____________________________________________________________

____________________________________________________________

Here you go, the tasks in your list:
1.[D][X] return book (by: JAN 01 2026)
2.[E][ ] project meeting (from: JAN 02 2026 to: JAN 03 2026)

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```
