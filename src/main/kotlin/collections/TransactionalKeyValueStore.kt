package collections

/**
 * A modified collection that holds pairs of objects (keys and values) and supports efficiently retrieving the value
 * corresponding to each key. Datastore keys are unique; the datastore holds only one value for each key.
 *
 * An additional feature for this [TransactionalKeyValueStore] in prior to [MutableMap] is that this datastore can
 * perform operations in transactions, which is able to commit or rollback the changes to the key value store.
 *
 * ## Usage:
 *
 * ### Commit a transaction
 *
 * ```kotlin
 * val keyValueStore = TransactionalKeyValueStore<String, String>()     // creates an new instance
 * keyValueStore.begin()                                                // begins the transaction
 * keyValueStore["foo"] = "456"                                         // stores the value "456" for key "foo"
 * keyValueStore.commit()                                               // commits the changes
 * keyValueStore.rollback()                                             // no transaction
 * keyValueStore["foo"]                                                 // returns "456"
 * ```
 *
 * ### Rollback a transaction
 *
 * ```kotlin
 * val keyValueStore = TransactionalKeyValueStore<String, String>()     // creates an new instance
 * keyValueStore["foo"] = "123"                                         // stores the value "123" for key "foo"
 * keyValueStore.begin()                                                // begins the transaction
 * keyValueStore["foo"] = "456"                                         // stores the value "456" for key "foo"
 * keyValueStore["foo"]                                                 // returns "456"
 * keyValueStore.rollback()                                             // reverts the changes
 * keyValueStore["foo"]                                                 // returns "123"
 * ```
 *
 * [TransactionalKeyValueStore] is also able to nest transactions and roll back and commit within nested transactions.
 *
 * ## Usage:
 *
 * ### Nested transactions
 *
 * ```kotlin
 * val keyValueStore = TransactionalKeyValueStore<String, String>()     // creates an new instance
 * keyValueStore["foo"] = "123"                                         // stores the value "123" for key "foo"
 * keyValueStore.begin()                                                // begins the transaction
 * keyValueStore["bar"] = "456"                                         // stores the value "456" for key "bar"
 * keyValueStore["foo"] = "456"                                         // sets the value "456" for key "foo"
 * keyValueStore.count("456")                                           // returns 2
 * keyValueStore["foo"]                                                 // returns "456"
 * keyValueStore["foo"] = "789"                                         // sets the value "789" for key "foo"
 * keyValueStore["foo"]                                                 // returns "789"
 * keyValueStore.rollback()                                             // reverts the changes
 * keyValueStore["foo"]                                                 // returns "456"
 * keyValueStore.rollback()                                             // reverts the changes
 * keyValueStore["foo"]                                                 // returns "123"
 * ```
 *
 * Note: This [TransactionalKeyValueStore] is a not thread-safe implementation for efficiency.
 *
 * @param Key the type of datastore keys. The datastore is invariant in its key type.
 * @param Value the type of datastore values. The datastore is invariant in its value type.
 */
class TransactionalKeyValueStore<Key, Value> : MutableMap<Key, Value> {

    private val buffers: ArrayDeque<MutableMap<Key, Value>> = ArrayDeque()

    init {
        buffers.add(mutableMapOf())
    }

    /**
     * Returns a [MutableSet] of all key/value pairs in this key value data store.
     */
    override val entries: MutableSet<MutableMap.MutableEntry<Key, Value>>
        get() = buffers.last().entries

    /**
     * Returns a [MutableSet] of all keys in this key value data store.
     */
    override val keys: MutableSet<Key>
        get() = buffers.last().keys

    /**
     * Returns the number of key/value pairs in this key value data store.
     */
    override val size: Int
        get() = buffers.last().size

    /**
     * Returns a [MutableCollection] of all values in the key value datastore. Note that this collection may contains
     * duplicate values.
     */
    override val values: MutableCollection<Value>
        get() = buffers.last().values

    /**
     * Removes all data from this key value datastore.
     */
    override fun clear() {
        buffers.clear()
        buffers.add(mutableMapOf())
    }

    /**
     * Returns true if the key value datastore contains the specific [key].
     */
    override fun containsKey(key: Key): Boolean = buffers.last().containsKey(key)

    /**
     * Returns true if the key value datastore maps one or more keys to the specific [value].
     */
    override fun containsValue(value: Value): Boolean = buffers.last().containsValue(value)

    /**
     * Returns the current value for [key].
     */
    override fun get(key: Key): Value? = buffers.last()[key]

    /**
     * Returns true if the key value datastore is empty (contains no data). Otherwise, returns false.
     */
    override fun isEmpty(): Boolean = buffers.last().isEmpty()

    /**
     * Removes the entry for [key] from the key value datastore.
     */
    override fun remove(key: Key): Value? = buffers.last().remove(key)

    /**
     * Stores all the [from]'s data to the key value datastore
     */
    override fun putAll(from: Map<out Key, Value>) = buffers.last().putAll(from)

    /**
     * Stores the [value] for [key] to the key value datastore.
     */
    override fun put(key: Key, value: Value): Value? = buffers.last().put(key, value)

    /**
     * Returns the number of keys that map to the specific [value].
     */
    fun count(value: Value): Int = values.count { it == value }

    /**
     * Starts a new transaction.
     */
    fun begin() {
        buffers.add(buffers.last().toMutableMap())
    }

    /**
     * Completes the current transaction.
     */
    fun commit(): Boolean {
        if (buffers.size == 1) {
            return false
        }
        val latestMap = buffers.removeLast()
        buffers.removeLast()
        buffers.add(latestMap)
        return true
    }

    /**
     * Reverts to the state prior to [begin] function call.
     */
    fun rollback(): Boolean {
        if (buffers.size == 1) {
            return false
        }
        buffers.removeLast()
        return true
    }
}