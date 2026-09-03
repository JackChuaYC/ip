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
    void getResponse_userInput_returnsYawnedResponse() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("Yawned heard: hello", yawned.getResponse("hello"));
    }
}
