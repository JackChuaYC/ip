package yawned.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

/** Tests command-word recognition performed by {@link CommandType}. */
class CommandTypeTest {

    @Test
    void getWord_everyCommandType_returnsItsCommandWord() {
        Map<CommandType, String> commandWords = Map.ofEntries(
                Map.entry(CommandType.TODO, "todo"),
                Map.entry(CommandType.DEADLINE, "deadline"),
                Map.entry(CommandType.EVENT, "event"),
                Map.entry(CommandType.LIST, "list"),
                Map.entry(CommandType.MARK, "mark"),
                Map.entry(CommandType.UNMARK, "unmark"),
                Map.entry(CommandType.DELETE, "delete"),
                Map.entry(CommandType.FIND, "find"),
                Map.entry(CommandType.BYE, "bye"),
                Map.entry(CommandType.UNKNOWN, ""));

        commandWords.forEach((commandType, expectedWord) ->
                assertEquals(expectedWord, commandType.getWord()));
    }

    @Test
    void fromInput_exactSupportedCommand_returnsMatchingType() {
        Map<String, CommandType> commands = Map.of(
                "todo", CommandType.TODO,
                "deadline", CommandType.DEADLINE,
                "event", CommandType.EVENT,
                "list", CommandType.LIST,
                "mark", CommandType.MARK,
                "unmark", CommandType.UNMARK,
                "delete", CommandType.DELETE,
                "find", CommandType.FIND,
                "bye", CommandType.BYE);

        commands.forEach((input, expectedType) ->
                assertEquals(expectedType, CommandType.fromInput(input)));
    }

    @Test
    void fromInput_argumentAcceptingCommandWithDetails_returnsMatchingType() {
        Map<String, CommandType> commands = Map.of(
                "todo read book", CommandType.TODO,
                "deadline submit report /by 2026-01-01 0900", CommandType.DEADLINE,
                "event meeting /from 2026-01-01 0900 /to 2026-01-01 1000", CommandType.EVENT,
                "mark 1", CommandType.MARK,
                "unmark 1", CommandType.UNMARK,
                "delete 1", CommandType.DELETE,
                "find book", CommandType.FIND);

        commands.forEach((input, expectedType) ->
                assertEquals(expectedType, CommandType.fromInput(input)));
    }

    @Test
    void fromInput_noArgumentCommandWithExtraText_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("list all"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("bye now"));
    }

    @Test
    void fromInput_emptyMalformedOrCaseMismatchedCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput(""));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("todoist buy bread"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("mark2"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput(" list"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("TODO buy bread"));
    }
}
