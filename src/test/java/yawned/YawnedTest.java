package yawned;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the backend responses supplied to the graphical user interface. */
class YawnedTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_taskCommands_updatesTasksAndReturnsResponses() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                yawned.getResponse("todo read book"));
        assertEquals("finally, that's done:\n  [T][X] read book", yawned.getResponse("mark 1"));
        assertEquals("Here you go, the tasks in your list:\n1.[T][X] read book", yawned.getResponse("list"));
    }

    @Test
    void getResponse_aliasCommands_returnCanonicalCommandResponses() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                yawned.getResponse("T read book"));
        assertEquals("finally, that's done:\n  [T][X] read book", yawned.getResponse("m 1"));
        assertEquals("As productive as me... unmarked:\n  [T][ ] read book", yawned.getResponse("U 1"));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book", yawned.getResponse("f book"));
        assertEquals("fine. I removed this task:\n  [T][ ] read book\nNow you have 0 tasks in the list.",
                yawned.getResponse("del 1"));
        assertEquals("No Tasks!", yawned.getResponse("L"));
    }

    @Test
    void getResponse_customAliases_canBeCreatedReplacedRemovedAndReloaded() {
        Path saveFile = temporaryDirectory.resolve("Yawned.txt");
        Yawned yawned = new Yawned(saveFile);

        assertEquals("Alias 'hw' now runs 'todo'.", yawned.getResponse("alias HW todo"));
        assertEquals("Got it. I've added this task:\n  [T][ ] finish assignment\nNow you have 1 tasks in the list.",
                yawned.getResponse("hW finish assignment"));
        assertEquals("Alias 'hw' now runs 'deadline'.", yawned.getResponse("alias hw deadline"));

        Yawned reloadedYawned = new Yawned(saveFile);
        assertEquals("Got it. I've added this task:\n  [D][ ] submit report (by: JAN 01 2026 0900)"
                        + "\nNow you have 2 tasks in the list.",
                reloadedYawned.getResponse("HW submit report /by 2026-01-01 0900"));
        assertEquals("Alias 'hw' has been removed.", reloadedYawned.getResponse("alias remove Hw"));
        assertEquals("urmmm, but I don't know what that means?? >:-(",
                reloadedYawned.getResponse("hw finish assignment"));
    }

    @Test
    void getResponse_customAliases_rejectInvalidNamesAndTargets() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("Alias names must contain letters only.", yawned.getResponse("alias hw-1 todo"));
        assertEquals("That alias name is reserved.", yawned.getResponse("alias todo deadline"));
        assertEquals("That alias name is reserved.", yawned.getResponse("alias t deadline"));
        assertEquals("That alias name is reserved.", yawned.getResponse("alias alias todo"));
        assertEquals("Aliases must target a canonical task command.", yawned.getResponse("alias hw t"));
        assertEquals("Aliases must target a canonical task command.", yawned.getResponse("alias hw bye"));
        assertEquals("No alias named 'hw'.", yawned.getResponse("alias remove hw"));
    }

    @Test
    void getResponse_invalidOrUnknownCommand_returnsValidationMessage() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("*Yawns* You need to tell me which number to mark.. like: mark 2", yawned.getResponse("mark"));
        assertEquals("urmmm, but I don't know what that means?? >:-(", yawned.getResponse("dance"));
    }
}
