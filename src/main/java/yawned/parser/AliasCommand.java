package yawned.parser;

/**
 * Represents the arguments of an alias creation or removal command.
 *
 * @param aliasName Name being created or removed.
 * @param targetCommand Canonical target command for a creation request.
 * @param removal Whether this request removes an alias.
 */
public record AliasCommand(String aliasName, String targetCommand, boolean removal) {
}
