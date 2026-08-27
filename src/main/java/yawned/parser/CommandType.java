package yawned.parser;

/**
 * Represents a command word accepted by Yawned.
 */
public enum CommandType {
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    FIND("find", true),
    BYE("bye", false),
    UNKNOWN("", false);

    private final String word;
    private final boolean acceptsArguments;

    CommandType(String word, boolean acceptsArguments) {
        this.word = word;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Returns the command word represented by this type.
     *
     * @return Command word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Identifies the command type represented by the user's full input.
     *
     * @param input Full user input.
     * @return Matching command type, or {@link #UNKNOWN}.
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (commandType == UNKNOWN) {
                continue;
            }
            if (input.equals(commandType.word)
                    || commandType.acceptsArguments && input.startsWith(commandType.word + " ")) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
