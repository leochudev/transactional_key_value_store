package command

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * The unit test cases of [CommandLineMapper].
 */
internal class CommandLineMapperTest {

    @Test
    internal fun `SET command`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("SET foo 123")
        assertIs<KeyValueStoreCommand.Set>(command, "command type should be Set")
        assertEquals("foo", command.key)
        assertEquals("123", command.value)
    }

    @Test
    internal fun `SET command with more than one space in between`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("SET    foo  123")
        assertIs<KeyValueStoreCommand.Set>(command, "command type should be Set")
        assertEquals("foo", command.key)
        assertEquals("123", command.value)
    }

    @Test
    internal fun `GET command`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("GET foo")
        assertIs<KeyValueStoreCommand.Get>(command, "command type should be Get")
        assertEquals("foo", command.key)
    }

    @Test
    internal fun `GET command with more than one space in between`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("GET    foo")
        assertIs<KeyValueStoreCommand.Get>(command, "command type should be Get")
        assertEquals("foo", command.key)
    }

    @Test
    internal fun `DELETE command`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("DELETE foo")
        assertIs<KeyValueStoreCommand.Delete>(command, "command type should be Delete")
        assertEquals("foo", command.key)
    }

    @Test
    internal fun `COUNT command`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("COUNT 123")
        assertIs<KeyValueStoreCommand.Count>(command, "command type should be Count")
        assertEquals("123", command.value)
    }

    @Test
    internal fun `BEGIN command`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("BEGIN")
        assertIs<KeyValueStoreCommand.Begin>(command, "command type should be Begin")
    }

    @Test
    internal fun `COMMIT command`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("COMMIT")
        assertIs<KeyValueStoreCommand.Commit>(command, "command type should be Commit")
    }

    @Test
    internal fun `ROLLBACK command`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("ROLLBACK")
        assertIs<KeyValueStoreCommand.Rollback>(command, "command type should be Rollback")
    }

    @Test
    internal fun `Undefined command`() {
        val command = CommandLineMapper.mapToKeyStoreCommand("UNDEFINED")
        assertIs<KeyValueStoreCommand.Unknown>(command, "command type should be Unknown")
    }

}