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
    void getResponse_invalidOrUnknownCommand_returnsValidationMessage() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("*Yawns* You need to tell me which number to mark.. like: mark 2", yawned.getResponse("mark"));
        assertEquals("urmmm, but I don't know what that means?? >:-(", yawned.getResponse("dance"));
    }
}
