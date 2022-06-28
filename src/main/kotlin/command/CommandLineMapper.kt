package command

private const val SET_COMMAND: String = "SET"
private const val GET_COMMAND: String = "GET"
private const val DELETE_COMMAND: String = "DELETE"
private const val COUNT_COMMAND: String = "COUNT"
private const val BEGIN_COMMAND: String = "BEGIN"
private const val COMMIT_COMMAND: String = "COMMIT"
private const val ROLLBACK_COMMAND: String = "ROLLBACK"
private const val SEPARATOR: String = " "
private const val EMPTY_STRING: String = ""

/**
 * Utility class to map from command line to [KeyValueStoreCommand].
 */
object CommandLineMapper {

    /**
     * Maps [commandLineInText] to [KeyValueStoreCommand].
     */
    fun mapToKeyStoreCommand(commandLineInText: String): KeyValueStoreCommand {
        val commandArgs = commandLineInText.split(SEPARATOR).filterNot { it.isBlank() }
        val commandInText = commandArgs[0].uppercase()
        val arg1 = if (commandArgs.size > 1) {
            commandArgs[1]
        } else {
            EMPTY_STRING
        }
        val arg2 = if (commandArgs.size > 2) {
            commandArgs[2]
        } else {
            EMPTY_STRING
        }
        return when (commandInText) {
            SET_COMMAND -> KeyValueStoreCommand.Set(arg1, arg2)
            GET_COMMAND -> KeyValueStoreCommand.Get(arg1)
            DELETE_COMMAND -> KeyValueStoreCommand.Delete(arg1)
            COUNT_COMMAND -> KeyValueStoreCommand.Count(arg1)
            BEGIN_COMMAND -> KeyValueStoreCommand.Begin
            COMMIT_COMMAND -> KeyValueStoreCommand.Commit
            ROLLBACK_COMMAND -> KeyValueStoreCommand.Rollback
            else -> KeyValueStoreCommand.Unknown
        }
    }
}