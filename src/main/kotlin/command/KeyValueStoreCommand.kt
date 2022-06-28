package command

/**
 * A command data model for the data store operation of [TransactionalKeyValueStoreApplication]
 */
sealed class KeyValueStoreCommand {

    /**
     * A command to store the [value] for [key].
     */
    data class Set(val key: String, val value: String) : KeyValueStoreCommand()

    /**
     * A command to return the current value for [key].
     */
    data class Get(val key: String) : KeyValueStoreCommand()

    /**
     * A command to remove the entry for [key].
     */
    data class Delete(val key: String) : KeyValueStoreCommand()

    /**
     * A command to return the number of keys that have the given value.
     */
    data class Count(val value: String) : KeyValueStoreCommand()

    /**
     * A command to start a new transaction.
     */
    object Begin : KeyValueStoreCommand()

    /**
     * A command to complete the current transaction.
     */
    object Commit : KeyValueStoreCommand()

    /**
     * A command to revert to state prior to [Begin] command.
     */
    object Rollback : KeyValueStoreCommand()

    /**
     * An undefined command.
     */
    object Unknown : KeyValueStoreCommand()
}
