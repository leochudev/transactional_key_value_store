import collections.TransactionalKeyValueStore
import command.CommandLineMapper
import command.KeyValueStoreCommand
import lifecycle.ApplicationLifecycle

private const val START_INFO: String = "Start Transactional Key Value Store"
private const val EXIT_INFO: String = "Exit Program"
private const val EXIT_COMMAND: String = "QUIT"
private const val INVALID_INPUT: String = "invalid input!"
private const val KEY_NOT_FOUND: String = "key not set"
private const val NO_TRANSACTION: String = "no transaction"

/**
 * The main application of transaction key value store. The user can enter commands to set/get/delete key/value pairs
 * and count values. All values are treated as strings. The key/value data is only stored in memory for this
 * application.
 *
 * This application allow the user to perform operations in transactions, which allows the user to commit or roll back
 * the changes to the key values store. It includes the ability to nest transactions and roll back and commit within
 * nested transactions.
 */
class TransactionalKeyValueStoreApplication : ApplicationLifecycle {

    private val dataStore: TransactionalKeyValueStore<String, String> = TransactionalKeyValueStore()

    /**
     * Starts the transactional key value store application.
     */
    fun start() {
        onStart()
        running()
        onExit()
    }

    override fun onStart() {
        println(START_INFO)
    }

    override fun onExit() {
        println(EXIT_INFO)
    }

    private fun running() {
        var commandLineText = readLine()
        while (commandLineText != null && commandLineText.uppercase() != EXIT_COMMAND) {
            val command = CommandLineMapper.mapToKeyStoreCommand(commandLineText)
            try {
                process(command) { println(it) }
            } catch (exception: IllegalArgumentException) {
                println(INVALID_INPUT)
            }
            commandLineText = readLine()
        }
    }

    private fun process(command: KeyValueStoreCommand, output: (String) -> Unit) {
        when (command) {
            is KeyValueStoreCommand.Set -> dataStore[command.key] = command.value
            is KeyValueStoreCommand.Get -> output(dataStore[command.key] ?: KEY_NOT_FOUND)
            is KeyValueStoreCommand.Count -> output(dataStore.count(command.value).toString())
            is KeyValueStoreCommand.Delete -> dataStore.remove(command.key)
            KeyValueStoreCommand.Begin -> dataStore.begin()
            KeyValueStoreCommand.Commit -> if (!dataStore.commit()) {
                output(NO_TRANSACTION)
            }
            KeyValueStoreCommand.Rollback -> if (!dataStore.rollback()) {
                output(NO_TRANSACTION)
            }
            KeyValueStoreCommand.Unknown -> throw IllegalArgumentException()
        }
    }
}