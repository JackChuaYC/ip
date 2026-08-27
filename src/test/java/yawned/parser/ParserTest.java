package yawned.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import yawned.exception.YawnedException;
import yawned.task.Deadline;
import yawned.task.Event;
import yawned.task.Task;
import yawned.task.ToDo;

/** Tests command parsing and task construction. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_validTaskCommands_createsTasksWithExpectedDetails() throws YawnedException {
        Task todo = parser.parseTask(CommandType.TODO, "todo read book");
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask(CommandType.DEADLINE, "deadline submit report /by 2026-01-01 0900"));
        Event event = assertInstanceOf(Event.class,
                parser.parseTask(CommandType.EVENT, "event project meeting /from 2026-01-02 1500 /to 2026-01-02 1600"));

        assertEquals("read book", todo.getDescription());
        assertInstanceOf(ToDo.class, todo);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 1, 1, 9, 0), deadline.getEndDate());
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 1, 2, 15, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2026, 1, 2, 16, 0), event.getEndDate());
    }

    @Test
    void parseTask_incompleteOrInvalidTaskCommands_throwsHelpfulException() {
        assertYawnedException(CommandType.TODO, "todo",
                "hey!!! The description of a todo cannot be empty. *yawns*");
        assertYawnedException(CommandType.DEADLINE, "deadline /by 2026-01-01 0900",
                "I just want to sleep... you forgot to provide a description for the deadline. "
                        + "Use: deadline <description> /by yyyy-MM-dd HHmm");
        assertYawnedException(CommandType.DEADLINE, "deadline submit report",
                "you woke me up for this? A deadline must include a /by time in yyyy-MM-dd HHmm format.");
        assertYawnedException(CommandType.DEADLINE, "deadline submit report /by 2026-02-30 0900",
                "Please use a valid date and time in yyyy-MM-dd HHmm format.");
        assertYawnedException(CommandType.EVENT, "event /from 2026-01-01 0900 /to 2026-01-01 1000",
                "I just want to sleep... you forgot to provide a description for the event. "
                        + "Use: event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm");
        assertYawnedException(CommandType.EVENT, "event meeting /from 2026-01-01 0900",
                "Excuse me, An event must include /from and /to times in yyyy-MM-dd HHmm format.");
        assertYawnedException(CommandType.EVENT,
                "event meeting /from 2026-01-01 0900 /to 2026-01-01 2400",
                "Please use a valid date and time in yyyy-MM-dd HHmm format.");
    }

    @Test
    void parseTask_nonTaskCommand_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseTask(CommandType.LIST, "list"));
    }

    @Test
    void parseTaskNumber_validNumber_returnsOneBasedNumber() throws YawnedException {
        assertEquals(12, parser.parseTaskNumber(CommandType.MARK, "mark 12"));
        assertEquals(-1, parser.parseTaskNumber(CommandType.DELETE, "delete -1"));
    }

    @Test
    void parseTaskNumber_missingOrMalformedNumber_throwsCommandSpecificException() {
        assertTaskNumberException(CommandType.MARK, "mark",
                "*Yawns* You need to tell me which number to mark.. like: mark 2");
        assertTaskNumberException(CommandType.UNMARK, "unmark nope",
                "*Yawns* You need to tell me which number to unmark.. like: unmark 2");
        assertTaskNumberException(CommandType.DELETE, "delete 1.5",
                "*Yawns* You need to tell me which number to delete.. like: delete 2");
    }

    @Test
    void parseFindKeyword_presentKeyword_returnsKeyword() throws YawnedException {
        assertEquals("book", parser.parseFindKeyword("find book"));
        assertEquals("project meeting", parser.parseFindKeyword("find project meeting"));
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsHelpfulException() {
        YawnedException exception = assertThrows(YawnedException.class,
                () -> parser.parseFindKeyword("find"));

        assertEquals("*Yawns* You need to tell me what to find.. like: find book", exception.getMessage());
    }

    private void assertYawnedException(CommandType commandType, String command, String expectedMessage) {
        YawnedException exception = assertThrows(YawnedException.class,
                () -> parser.parseTask(commandType, command));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void assertTaskNumberException(CommandType commandType, String command, String expectedMessage) {
        YawnedException exception = assertThrows(YawnedException.class,
                () -> parser.parseTaskNumber(commandType, command));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
