# UI test plan

## Run command

Compile `src/main/java/Task.java`, `src/main/java/ToDo.java`, and `src/main/java/Yawned.java` with Java 25, then run `Yawned` with the compiled classes on the classpath. Run each test case in a fresh console session.

## Test case: Unmark a completed task

**Aim:** Verify that `unmark <number>` changes a completed task back to not done and that `list` displays the updated status.

**Inputs:**

```text
read book
return book
buy bread
mark 1
mark 2
unmark 2
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

added: read book
____________________________________________________________

____________________________________________________________

added: return book
____________________________________________________________

____________________________________________________________

added: buy bread
____________________________________________________________

____________________________________________________________

finally, that's done:
  [X] read book
____________________________________________________________

____________________________________________________________

finally, that's done:
  [X] return book
____________________________________________________________

____________________________________________________________

As productive as me... unmarked:
  [ ] return book
____________________________________________________________

____________________________________________________________

Here are the tasks in your list:
1. [X] read book
2. [ ] return book
3. [ ] buy bread

____________________________________________________________

____________________________________________________________

Bye.. I am going back to sleep.
____________________________________________________________

```
