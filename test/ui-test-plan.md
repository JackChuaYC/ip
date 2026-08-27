# UI test plan

## Run command

Compile all files in `src/main/java` with Java 25, then run `Yawned` with the compiled classes on the classpath. Run each test case in a fresh console session.

Before each case, set `data/Yawned.txt` to the stated **Initial storage** value. `None` means the file must not exist; restore any pre-existing file after the test run.

## Test case: Add and list each task type

**Aim:** Verify that to-dos, deadlines, and events are created with ISO date-times, and that they are displayed as `MMM dd yyyy HHmm` when listed.

**Initial storage:** None

**Inputs:**

```text
todo borrow book
deadline return book /by 2026-01-01 1500
event project meeting /from 2026-01-02 1500 /to 2026-01-02 1600
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
  [D][ ] return book (by: JAN 01 2026 1500)
Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
  [E][ ] project meeting (from: JAN 02 2026 1500 to: JAN 02 2026 1600)
Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________

finally, that's done:
  [T][X] borrow book
____________________________________________________________

____________________________________________________________

Here you go, the tasks in your list:
1.[T][X] borrow book
2.[D][ ] return book (by: JAN 01 2026 1500)
3.[E][ ] project meeting (from: JAN 02 2026 1500 to: JAN 02 2026 1600)

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```

## Test case: Reject invalid ISO dates

**Aim:** Verify that invalid deadline and event date-times are rejected without adding tasks, while a later valid ISO date-time still works.

**Initial storage:** None

**Inputs:**

```text
deadline return book /by 2026-02-30 1500
event project meeting /from 2026-01-02 1500 /to 2026-01-02 2400
deadline submit report /by 2026-12-31 0900
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

Please use a valid date and time in yyyy-MM-dd HHmm format.
____________________________________________________________

____________________________________________________________

Please use a valid date and time in yyyy-MM-dd HHmm format.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
  [D][ ] submit report (by: DEC 31 2026 0900)
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Here you go, the tasks in your list:
1.[D][ ] submit report (by: DEC 31 2026 0900)

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```

## Test case: Load persisted ISO dates

**Aim:** Verify that persisted deadlines and events load as `LocalDateTime` values and display in `MMM dd yyyy HHmm` format with their completion states intact.

**Initial storage:**

```text
D | 1 | return book | 2026-01-01T15:00
E | 0 | project meeting | 2026-01-02T15:00 | 2026-01-02T16:00
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
1.[D][X] return book (by: JAN 01 2026 1500)
2.[E][ ] project meeting (from: JAN 02 2026 1500 to: JAN 02 2026 1600)

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```

## Test case: Explain incomplete deadline and event date-time formats

**Aim:** Verify that incomplete deadlines and events explain the required `yyyy-MM-dd HHmm` date-time format, without adding tasks before a later valid command.

**Initial storage:** None

**Inputs:**

```text
deadline /by 2026-01-01 0900
deadline submit report
event /from 2026-01-01 0900 /to 2026-01-01 1000
event meeting /from 2026-01-01 0900
todo read book
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

I just want to sleep... you forgot to provide a description for the deadline. Use: deadline <description> /by yyyy-MM-dd HHmm
____________________________________________________________

____________________________________________________________

you woke me up for this? A deadline must include a /by time in yyyy-MM-dd HHmm format.
____________________________________________________________

____________________________________________________________

I just want to sleep... you forgot to provide a description for the event. Use: event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm
____________________________________________________________

____________________________________________________________

Excuse me, An event must include /from and /to times in yyyy-MM-dd HHmm format.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

Here you go, the tasks in your list:
1.[T][ ] read book

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```

## Test case: Reject invalid task numbers without changing tasks

**Aim:** Verify that malformed and out-of-range task-number commands are rejected, while later valid mark, unmark, and delete commands still change the intended task.

**Initial storage:** None

**Inputs:**

```text
todo read book
mark
mark 1
unmark nope
unmark 1
delete 0
delete 1
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
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________

*Yawns* You need to tell me which number to mark.. like: mark 2
____________________________________________________________

____________________________________________________________

finally, that's done:
  [T][X] read book
____________________________________________________________

____________________________________________________________

*Yawns* You need to tell me which number to unmark.. like: unmark 2
____________________________________________________________

____________________________________________________________

As productive as me... unmarked:
  [T][ ] read book
____________________________________________________________

____________________________________________________________

you... don't have that task number???
____________________________________________________________

____________________________________________________________

fine. I removed this task:
  [T][ ] read book
Now you have 0 tasks in the list.
____________________________________________________________

____________________________________________________________

No Tasks!

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```
