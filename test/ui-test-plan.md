# UI test plan

## Run command

Compile all files in `src/main/java` with Java 25, then run `Yawned` with the compiled classes on the classpath. Run each test case in a fresh console session.

## Test case: Add and list each task type

**Aim:** Verify that to-dos, deadlines, and events are created with their type-specific time information, and retain their type and done status when listed.

**Inputs:**

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
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
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________

Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________

finally, that's done:
  [T][X] borrow book
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1.[T][X] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```
