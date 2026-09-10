package yawned.parser;

/**
 * Represents a command word accepted by Yawned.
 */
public enum CommandType {
    TODO("todo", true, "t"),
    DEADLINE("deadline", true, "d"),
    EVENT("event", true, "e"),
    LIST("list", false, "l"),
    MARK("mark", true, "m"),
    UNMARK("unmark", true, "u"),
    DELETE("delete", true, "del"),
    FIND("find", true, "f"),
    BYE("bye", false),
    UNKNOWN("", false);

    private final String word;
    private final boolean acceptsArguments;
    private final String[] aliases;

    CommandType(String word, boolean acceptsArguments, String... aliases) {
        this.word = word;
        this.acceptsArguments = acceptsArguments;
        this.aliases = aliases;
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
            if (commandType.matchesInput(input)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }

    /** Returns whether the input begins with this command's canonical word or alias. */
    private boolean matchesInput(String input) {
        if (this == UNKNOWN) {
            return false;
        }
        int firstSpaceIndex = input.indexOf(' ');
        boolean hasArguments = firstSpaceIndex >= 0;
        if (hasArguments && !acceptsArguments) {
            return false;
        }
        String typedWord = hasArguments ? input.substring(0, firstSpaceIndex) : input;
        return typedWord.equals(word) || isAlias(typedWord);
    }

    /** Returns whether the supplied word is one of this command's aliases. */
    private boolean isAlias(String typedWord) {
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(typedWord)) {
                return true;
            }
        }
        return false;
    }
}
