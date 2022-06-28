package collections

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The unit test cases of [TransactionalKeyValueStore].
 */
internal class TransactionalKeyValueStoreTest {

    private lateinit var keyValueStore: TransactionalKeyValueStore<String, String>

    @BeforeEach
    fun setUp() {
        keyValueStore = TransactionalKeyValueStore()
    }

    @AfterEach
    fun tearDown() {
        keyValueStore.clear()
    }

    @Test
    internal fun `Set and get a value`() {
        keyValueStore["foo"] = "123"
        assertEquals("keyValueStore[\"foo\"] should return \"123\"!", "123", keyValueStore["foo"])
    }

    @Test
    internal fun `Delete a value`() {
        keyValueStore["foo"] = "123"
        keyValueStore.remove("foo")
        assertNull(keyValueStore["foo"], "keyValueStore[\"foo\"] should return null!")
    }

    @Test
    internal fun `Delete a non exists value`() {
        keyValueStore.remove("foo")
        assertNull(keyValueStore["foo"], "keyValueStore[\"foo\"] should return null!")
    }

    @Test
    internal fun `Count the number of occurrences of a value`() {
        keyValueStore["foo"] = "123"
        keyValueStore["bar"] = "456"
        keyValueStore["baz"] = "123"
        assertEquals("keyValueStore.count(\"123\") should return 2!", 2, keyValueStore.count("123"))
        assertEquals("keyValueStore.count(\"456\") should return 1!", 1, keyValueStore.count("456"))
    }

    @Test
    internal fun `Count the number of occurrences of a non exists value`() {
        assertEquals("keyValueStore.count(\"123\") should return 0!", 0, keyValueStore.count("123"))
    }

    @Test
    internal fun `Commit a transaction`() {
        keyValueStore.begin()
        keyValueStore["foo"] = "456"
        keyValueStore.commit()
        assertEquals("keyValueStore[\"foo\"] should return 456!", "456", keyValueStore["foo"])
    }

    @Test
    internal fun `Rollback a transaction`() {
        keyValueStore["foo"] = "123"
        keyValueStore["bar"] = "abc"
        keyValueStore.begin()
        keyValueStore["foo"] = "456"
        assertEquals("keyValueStore[\"foo\"] should return 456!", "456", keyValueStore["foo"])
        keyValueStore["bar"] = "def"
        assertEquals("keyValueStore[\"bar\"] should return def!", "def", keyValueStore["bar"])
        assertTrue(keyValueStore.rollback())
        assertEquals("keyValueStore[\"foo\"] should return 123!", "123", keyValueStore["foo"])
        assertEquals("keyValueStore[\"bar\"] should return abc!", "abc", keyValueStore["bar"])
        assertFalse(keyValueStore.commit(), "There should be no transaction to commit!")
    }

    @Test
    internal fun `Rollback when no transaction`() {
        assertFalse(keyValueStore.rollback(), "There should be no transaction to rollback!")
    }

    @Test
    internal fun `Nested transactions`() {
        keyValueStore["foo"] = "123"
        keyValueStore.begin()
        keyValueStore["bar"] = "456"
        keyValueStore["foo"] = "456"
        keyValueStore.begin()
        assertEquals("keyValueStore.count(\"456\") should return 2!", 2, keyValueStore.count("456"))
        assertEquals("keyValueStore[\"foo\"] should return 456!", "456", keyValueStore["foo"])
        keyValueStore["foo"] = "789"
        assertEquals("keyValueStore[\"foo\"] should return 789!", "789", keyValueStore["foo"])
        assertTrue(keyValueStore.rollback(), "keyValueStore.rollback() should return true")
        assertEquals("keyValueStore[\"foo\"] should return 456!", "456", keyValueStore["foo"])
        assertTrue(keyValueStore.rollback(), "keyValueStore.rollback() should return true")
        assertEquals("keyValueStore[\"foo\"] should return 123!", "123", keyValueStore["foo"])
    }
}