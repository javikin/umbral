package com.umbral.data.nfc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Build
import app.cash.turbine.test
import com.umbral.data.local.dao.NfcTagDao
import com.umbral.data.local.entity.NfcTagEntity
import com.umbral.domain.nfc.NdefPayload
import com.umbral.domain.nfc.NfcError
import com.umbral.domain.nfc.NfcResult
import com.umbral.domain.nfc.NfcState
import com.umbral.domain.nfc.TagEvent
import com.umbral.domain.nfc.TagType
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class NfcManagerImplTest {

    private lateinit var context: Context
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var nfcTagDao: NfcTagDao
    private lateinit var nfcManager: NfcManagerImpl
    private lateinit var activity: Activity

    private val testUid = "04A1B2C3D4E5F6"
    private val testTagId = "test-tag-id-123"

    private val testEntity = NfcTagEntity(
        id = testTagId,
        uid = testUid,
        name = "Test Tag",
        location = "Test Location",
        profileId = "test-profile",
        createdAt = Instant.now().toEpochMilli(),
        lastUsedAt = Instant.now().toEpochMilli(),
        useCount = 1
    )

    @Before
    fun setup() {
        // Clear any previous mocks
        clearAllMocks()
        unmockkAll()

        // Setup basic mocks
        context = mockk(relaxed = true)
        nfcAdapter = mockk(relaxed = true)
        nfcTagDao = mockk(relaxed = true)
        activity = mockk(relaxed = true)

        // Mock NfcAdapter.getDefaultAdapter
        mockkStatic(NfcAdapter::class)
        every { NfcAdapter.getDefaultAdapter(context) } returns nfcAdapter
        every { nfcAdapter.isEnabled } returns true

        // Mock NdefMessage and NdefRecord constructors
        mockkConstructor(NdefMessage::class)
        mockkConstructor(NdefRecord::class)
        mockkStatic(NdefRecord::class)

        // Create manager instance
        nfcManager = NfcManagerImpl(context, nfcTagDao)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }

    // ========================================
    // NFC Availability Tests
    // ========================================

    @Test
    fun `isNfcAvailable returns true when adapter exists`() {
        // When
        val result = nfcManager.isNfcAvailable()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isNfcAvailable returns false when adapter is null`() {
        // Given
        every { NfcAdapter.getDefaultAdapter(any()) } returns null
        val managerWithoutNfc = NfcManagerImpl(context, nfcTagDao)

        // When
        val result = managerWithoutNfc.isNfcAvailable()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isNfcEnabled returns true when adapter is enabled`() {
        // Given
        every { nfcAdapter.isEnabled } returns true

        // When
        val result = nfcManager.isNfcEnabled()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isNfcEnabled returns false when adapter is disabled`() {
        // Given
        every { nfcAdapter.isEnabled } returns false

        // When
        val result = nfcManager.isNfcEnabled()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isNfcEnabled returns false when adapter is null`() {
        // Given
        every { NfcAdapter.getDefaultAdapter(any()) } returns null
        val managerWithoutNfc = NfcManagerImpl(context, nfcTagDao)

        // When
        val result = managerWithoutNfc.isNfcEnabled()

        // Then
        assertFalse(result)
    }

    // ========================================
    // NFC State Tests
    // ========================================

    @Test
    fun `nfcState emits NotAvailable when adapter is null`() = runTest {
        // Given
        every { NfcAdapter.getDefaultAdapter(any()) } returns null
        val managerWithoutNfc = NfcManagerImpl(context, nfcTagDao)

        // When/Then
        managerWithoutNfc.nfcState.test {
            assertEquals(NfcState.NotAvailable, awaitItem())
        }
    }

    @Test
    fun `nfcState emits Disabled when adapter is disabled`() = runTest {
        // Given
        every { nfcAdapter.isEnabled } returns false
        val managerWithDisabledNfc = NfcManagerImpl(context, nfcTagDao)

        // When/Then
        managerWithDisabledNfc.nfcState.test {
            assertEquals(NfcState.Disabled, awaitItem())
        }
    }

    @Test
    fun `nfcState emits Enabled when adapter is enabled`() = runTest {
        // When/Then
        nfcManager.nfcState.test {
            assertEquals(NfcState.Enabled, awaitItem())
        }
    }

    @Test
    fun `refreshState updates nfcState correctly`() = runTest {
        // Given - Initially enabled
        every { nfcAdapter.isEnabled } returns true

        nfcManager.nfcState.test {
            // Then - Initially enabled
            assertEquals(NfcState.Enabled, awaitItem())

            // When - Adapter becomes disabled
            every { nfcAdapter.isEnabled } returns false
            nfcManager.refreshState()

            // Then - State updated to disabled
            assertEquals(NfcState.Disabled, awaitItem())
        }
    }

    // ========================================
    // Tag UID and Type Tests
    // ========================================

    @Test
    fun `getTagUid returns correctly formatted hex string`() {
        // Given
        val tag = mockk<Tag>()
        val byteArray = byteArrayOf(0x04, 0xA1.toByte(), 0xB2.toByte(), 0xC3.toByte())
        every { tag.id } returns byteArray

        // When
        val uid = nfcManager.getTagUid(tag)

        // Then
        assertEquals("04A1B2C3", uid)
    }

    @Test
    fun `getTagUid handles 7-byte UID correctly`() {
        // Given
        val tag = mockk<Tag>()
        val byteArray = byteArrayOf(
            0x04, 0xA1.toByte(), 0xB2.toByte(), 0xC3.toByte(),
            0xD4.toByte(), 0xE5.toByte(), 0xF6.toByte()
        )
        every { tag.id } returns byteArray

        // When
        val uid = nfcManager.getTagUid(tag)

        // Then
        assertEquals("04A1B2C3D4E5F6", uid)
    }

    @Test
    fun `getTagType detects NTAG213 correctly`() {
        // Given
        val tag = mockk<Tag>()
        every { tag.techList } returns arrayOf("android.nfc.tech.NfcA", "android.nfc.tech.Ndef")

        // When
        val tagType = nfcManager.getTagType(tag)

        // Then
        assertEquals(TagType.NTAG213, tagType)
    }

    @Test
    fun `getTagType detects MIFARE_ULTRALIGHT correctly`() {
        // Given
        val tag = mockk<Tag>()
        every { tag.techList } returns arrayOf("android.nfc.tech.MifareUltralight")

        // When
        val tagType = nfcManager.getTagType(tag)

        // Then
        assertEquals(TagType.MIFARE_ULTRALIGHT, tagType)
    }

    @Test
    fun `getTagType detects MIFARE_CLASSIC correctly`() {
        // Given
        val tag = mockk<Tag>()
        every { tag.techList } returns arrayOf("android.nfc.tech.MifareClassic")

        // When
        val tagType = nfcManager.getTagType(tag)

        // Then
        assertEquals(TagType.MIFARE_CLASSIC, tagType)
    }

    @Test
    fun `getTagType returns UNKNOWN for unsupported tech`() {
        // Given
        val tag = mockk<Tag>()
        every { tag.techList } returns arrayOf("android.nfc.tech.Unknown")

        // When
        val tagType = nfcManager.getTagType(tag)

        // Then
        assertEquals(TagType.UNKNOWN, tagType)
    }

    // ========================================
    // Read Tag Tests - isUmbralTag functionality
    // ========================================

    @Test
    fun `readTag returns profile id from valid Umbral tag`() = runTest {
        // Given
        val tag = mockk<Tag>()
        val ndef = mockk<Ndef>(relaxed = true)

        val payload = NdefPayload.create(testTagId)
        val uri = NdefPayload.toUri(payload)

        // Create mock NDEF record
        val mockRecord = mockk<NdefRecord>()
        every { mockRecord.tnf } returns NdefRecord.TNF_WELL_KNOWN
        every { mockRecord.type } returns NdefRecord.RTD_URI
        // Format: prefix byte (0x00) + URI string
        val uriBytes = uri.toByteArray(Charsets.UTF_8)
        val payloadBytes = ByteArray(uriBytes.size + 1)
        payloadBytes[0] = 0x00 // No prefix
        System.arraycopy(uriBytes, 0, payloadBytes, 1, uriBytes.size)
        every { mockRecord.payload } returns payloadBytes

        val mockMessage = mockk<NdefMessage>()
        every { mockMessage.records } returns arrayOf(mockRecord)

        every { tag.id } returns byteArrayOf(0x04, 0xA1.toByte())
        every { tag.techList } returns arrayOf("android.nfc.tech.NfcA")

        mockkStatic(Ndef::class)
        every { Ndef.get(tag) } returns ndef
        every { ndef.connect() } just Runs
        every { ndef.ndefMessage } returns mockMessage
        every { ndef.close() } just Runs

        coEvery { nfcTagDao.getTagByUid(any()) } returns null
        coEvery { nfcTagDao.getTagById(testTagId) } returns testEntity

        // Mock Intent
        val intent = mockk<Intent>()
        mockkStatic(Build.VERSION::class)
        every { Build.VERSION.SDK_INT } returns Build.VERSION_CODES.R
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns tag

        // When
        val result = nfcManager.processTagIntent(intent)

        // Then
        assertTrue("Result should be Success", result is NfcResult.Success)
        val event = (result as NfcResult.Success).data
        assertTrue("Event should be KnownTag", event is TagEvent.KnownTag)
        assertEquals("Tag ID should match", testTagId, (event as TagEvent.KnownTag).tag.id)
    }

    @Test
    fun `isUmbralTag detects valid Umbral tags`() {
        // Given - Valid Umbral payload
        val payload = NdefPayload.create("valid-tag-id")

        // When
        val isValid = payload.isValid()

        // Then
        assertTrue("Valid Umbral payload should be recognized", isValid)
    }

    @Test
    fun `isUmbralTag rejects external tags`() {
        // Given - Invalid/tampered payload
        val payload = NdefPayload(
            version = 1,
            tagId = "some-id",
            checksum = "00000000" // Wrong checksum = external tag
        )

        // When
        val isValid = payload.isValid()

        // Then
        assertFalse("External tag should not be valid", isValid)
    }

    // ========================================
    // Write Tag Tests
    // ========================================

    @Test
    fun `writeTag successfully writes profile id to tag`() = runTest {
        // Given
        val tag = mockk<Tag>()
        val ndef = mockk<Ndef>(relaxed = true)

        // Mock the message construction
        val mockRecord = mockk<NdefRecord>(relaxed = true)
        val mockMessage = mockk<NdefMessage>(relaxed = true)
        every { NdefRecord.createUri(any<String>()) } returns mockRecord
        every { mockMessage.toByteArray() } returns ByteArray(50) // Small enough

        mockkStatic(Ndef::class)
        every { Ndef.get(tag) } returns ndef
        every { ndef.connect() } just Runs
        every { ndef.isWritable } returns true
        every { ndef.maxSize } returns 1024
        every { ndef.writeNdefMessage(any()) } just Runs
        every { ndef.close() } just Runs

        // When
        val result = nfcManager.writeTag(tag, testTagId)

        // Then
        assertTrue("Write should succeed", result is NfcResult.Success)
        verify { ndef.writeNdefMessage(any()) }
        verify { ndef.close() }
    }

    @Test
    fun `writeTag returns error when tag is read-only`() = runTest {
        // Given
        val tag = mockk<Tag>()
        val ndef = mockk<Ndef>(relaxed = true)

        mockkStatic(Ndef::class)
        every { Ndef.get(tag) } returns ndef
        every { ndef.connect() } just Runs
        every { ndef.isWritable } returns false
        every { ndef.close() } just Runs

        // When
        val result = nfcManager.writeTag(tag, testTagId)

        // Then
        assertTrue("Result should be Error", result is NfcResult.Error)
        assertEquals(NfcError.TAG_READ_ONLY, (result as NfcResult.Error).error)
        verify(exactly = 0) { ndef.writeNdefMessage(any()) }
    }

    @Test
    fun `writeTag returns error when tag is too small`() = runTest {
        // Given
        val tag = mockk<Tag>()
        val ndef = mockk<Ndef>(relaxed = true)

        val mockMessage = mockk<NdefMessage>(relaxed = true)
        every { mockMessage.toByteArray() } returns ByteArray(100) // Larger than maxSize

        mockkStatic(Ndef::class)
        every { Ndef.get(tag) } returns ndef
        every { ndef.connect() } just Runs
        every { ndef.isWritable } returns true
        every { ndef.maxSize } returns 10 // Too small
        every { ndef.close() } just Runs

        // When
        val result = nfcManager.writeTag(tag, testTagId)

        // Then
        assertTrue("Result should be Error", result is NfcResult.Error)
        assertEquals(NfcError.TAG_TOO_SMALL, (result as NfcResult.Error).error)
    }

    @Test
    fun `writeTag handles IOException with TAG_IO_ERROR`() = runTest {
        // Given
        val tag = mockk<Tag>()
        val ndef = mockk<Ndef>(relaxed = true)

        mockkStatic(Ndef::class)
        every { Ndef.get(tag) } returns ndef
        every { ndef.connect() } throws IOException("Tag lost")

        // When
        val result = nfcManager.writeTag(tag, testTagId)

        // Then
        assertTrue("Result should be Error", result is NfcResult.Error)
        assertEquals(NfcError.TAG_IO_ERROR, (result as NfcResult.Error).error)
    }

    @Test
    fun `writeTag successfully formats and writes to formatable tag`() = runTest {
        // Given
        val tag = mockk<Tag>()
        val formatable = mockk<NdefFormatable>(relaxed = true)

        mockkStatic(Ndef::class)
        mockkStatic(NdefFormatable::class)
        every { Ndef.get(tag) } returns null
        every { NdefFormatable.get(tag) } returns formatable
        every { formatable.connect() } just Runs
        every { formatable.format(any()) } just Runs
        every { formatable.close() } just Runs

        // When
        val result = nfcManager.writeTag(tag, testTagId)

        // Then
        assertTrue("Result should be Success", result is NfcResult.Success)
        verify { formatable.format(any()) }
        verify { formatable.close() }
    }

    @Test
    fun `writeTag returns error when tag is not NDEF compatible`() = runTest {
        // Given
        val tag = mockk<Tag>()

        mockkStatic(Ndef::class)
        mockkStatic(NdefFormatable::class)
        every { Ndef.get(tag) } returns null
        every { NdefFormatable.get(tag) } returns null

        // When
        val result = nfcManager.writeTag(tag, testTagId)

        // Then
        assertTrue("Result should be Error", result is NfcResult.Error)
        assertEquals(NfcError.TAG_NOT_SUPPORTED, (result as NfcResult.Error).error)
    }

    @Test
    fun `writeTag handles FormatException in formatable tag`() = runTest {
        // Given
        val tag = mockk<Tag>()
        val formatable = mockk<NdefFormatable>(relaxed = true)

        mockkStatic(Ndef::class)
        mockkStatic(NdefFormatable::class)
        every { Ndef.get(tag) } returns null
        every { NdefFormatable.get(tag) } returns formatable
        every { formatable.connect() } just Runs
        every { formatable.format(any()) } throws IOException("Format failed")

        // When
        val result = nfcManager.writeTag(tag, testTagId)

        // Then
        assertTrue("Result should be Error", result is NfcResult.Error)
        assertEquals(NfcError.TAG_IO_ERROR, (result as NfcResult.Error).error)
    }

    // ========================================
    // NFC Exception Handling Tests
    // ========================================

    @Test
    fun `processTagIntent handles TAG_LOST error when tag is removed`() = runTest {
        // Given
        val intent = mockk<Intent>()
        mockkStatic(Build.VERSION::class)
        every { Build.VERSION.SDK_INT } returns Build.VERSION_CODES.R
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns null

        // When
        val result = nfcManager.processTagIntent(intent)

        // Then
        assertTrue("Result should be Error", result is NfcResult.Error)
        assertEquals(NfcError.TAG_LOST, (result as NfcResult.Error).error)
    }

    @Test
    fun `processTagIntent handles TAG_NOT_SUPPORTED error`() = runTest {
        // Given
        val intent = mockk<Intent>()
        val tag = mockk<Tag>()

        mockkStatic(Build.VERSION::class)
        every { Build.VERSION.SDK_INT } returns Build.VERSION_CODES.R
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns tag
        every { tag.id } returns byteArrayOf(0x04, 0xA1.toByte())
        every { tag.techList } returns arrayOf("android.nfc.tech.MifareClassic") // Not supported

        // When
        val result = nfcManager.processTagIntent(intent)

        // Then
        assertTrue("Result should be Error", result is NfcResult.Error)
        assertEquals(NfcError.TAG_NOT_SUPPORTED, (result as NfcResult.Error).error)
    }

    @Test
    fun `processTagIntent handles TAG_IO_ERROR during read`() = runTest {
        // Given
        val intent = mockk<Intent>()
        val tag = mockk<Tag>()
        val ndef = mockk<Ndef>(relaxed = true)

        mockkStatic(Build.VERSION::class)
        every { Build.VERSION.SDK_INT } returns Build.VERSION_CODES.R
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns tag
        every { tag.id } returns byteArrayOf(0x04, 0xA1.toByte())
        every { tag.techList } returns arrayOf("android.nfc.tech.NfcA")

        mockkStatic(Ndef::class)
        every { Ndef.get(tag) } returns ndef
        every { ndef.connect() } throws IOException("Read failed")

        coEvery { nfcTagDao.getTagByUid(any()) } returns null

        // When
        val result = nfcManager.processTagIntent(intent)

        // Then - Should return UnknownTag since we handle read errors gracefully
        assertTrue("Result should be Success with UnknownTag", result is NfcResult.Success)
        val event = (result as NfcResult.Success).data
        assertTrue("Event should be UnknownTag", event is TagEvent.UnknownTag)
    }

    @Test
    fun `writeTag handles general exception with WRITE_FAILED`() = runTest {
        // Given
        val tag = mockk<Tag>()

        mockkStatic(Ndef::class)
        mockkStatic(NdefFormatable::class)
        every { Ndef.get(tag) } throws RuntimeException("Unexpected error")

        // When
        val result = nfcManager.writeTag(tag, testTagId)

        // Then
        assertTrue("Result should be Error", result is NfcResult.Error)
        assertEquals(NfcError.WRITE_FAILED, (result as NfcResult.Error).error)
    }

    // ========================================
    // NdefPayload Tests
    // ========================================

    @Test
    fun `NdefPayload creates valid payload with checksum`() {
        // Given
        val tagId = "test-tag-123"

        // When
        val payload = NdefPayload.create(tagId)

        // Then
        assertNotNull("Payload should not be null", payload)
        assertEquals("Tag ID should match", tagId, payload.tagId)
        assertTrue("Payload should be valid", payload.isValid())
    }

    @Test
    fun `NdefPayload with wrong checksum is invalid`() {
        // Given
        val payload = NdefPayload(
            version = 1,
            tagId = "test-tag-123",
            checksum = "00000000" // Wrong checksum
        )

        // When
        val isValid = payload.isValid()

        // Then
        assertFalse("Payload with wrong checksum should be invalid", isValid)
    }

    @Test
    fun `NdefPayload roundtrip preserves data`() {
        // Given
        val originalPayload = NdefPayload.create("test-tag-456")

        // When
        val uri = NdefPayload.toUri(originalPayload)
        val parsedPayload = NdefPayload.fromUri(uri)

        // Then
        assertNotNull("Parsed payload should not be null", parsedPayload)
        assertEquals("Tag ID should match", originalPayload.tagId, parsedPayload?.tagId)
        assertEquals("Checksum should match", originalPayload.checksum, parsedPayload?.checksum)
        assertEquals("Version should match", originalPayload.version, parsedPayload?.version)
    }

    @Test
    fun `NdefPayload fromUri returns null for invalid URI`() {
        // Given
        val invalidUri = "https://example.com/not-umbral"

        // When
        val payload = NdefPayload.fromUri(invalidUri)

        // Then
        assertEquals("Invalid URI should return null", null, payload)
    }

    // ========================================
    // TagType Support Tests
    // ========================================

    @Test
    fun `NTAG213 is supported`() {
        assertTrue("NTAG213 should be supported", TagType.NTAG213.supported)
    }

    @Test
    fun `NTAG215 is supported`() {
        assertTrue("NTAG215 should be supported", TagType.NTAG215.supported)
    }

    @Test
    fun `NTAG216 is supported`() {
        assertTrue("NTAG216 should be supported", TagType.NTAG216.supported)
    }

    @Test
    fun `MIFARE_ULTRALIGHT is supported`() {
        assertTrue("MIFARE_ULTRALIGHT should be supported", TagType.MIFARE_ULTRALIGHT.supported)
    }

    @Test
    fun `MIFARE_CLASSIC is not supported`() {
        assertFalse("MIFARE_CLASSIC should not be supported", TagType.MIFARE_CLASSIC.supported)
    }

    @Test
    fun `UNKNOWN type is not supported`() {
        assertFalse("UNKNOWN type should not be supported", TagType.UNKNOWN.supported)
    }

    // ========================================
    // Foreground Dispatch Tests
    // ========================================

    @Test
    fun `enableForegroundDispatch sets state to Disabled when NFC is disabled`() = runTest {
        // Given
        every { nfcAdapter.isEnabled } returns false

        // When
        nfcManager.enableForegroundDispatch(activity)

        // Then
        nfcManager.nfcState.test {
            assertEquals(NfcState.Disabled, awaitItem())
        }
        verify(exactly = 0) { nfcAdapter.enableForegroundDispatch(any(), any(), any(), any()) }
    }

    @Test
    fun `disableForegroundDispatch updates state to Enabled when NFC is enabled`() = runTest {
        // Given
        every { nfcAdapter.isEnabled } returns true
        every { nfcAdapter.disableForegroundDispatch(activity) } just Runs

        // When
        nfcManager.disableForegroundDispatch(activity)

        // Then
        nfcManager.nfcState.test {
            assertEquals(NfcState.Enabled, awaitItem())
        }
        verify { nfcAdapter.disableForegroundDispatch(activity) }
    }

    @Test
    fun `disableForegroundDispatch updates state to Disabled when NFC is disabled`() = runTest {
        // Given
        every { nfcAdapter.isEnabled } returns false
        every { nfcAdapter.disableForegroundDispatch(activity) } just Runs

        // When
        nfcManager.disableForegroundDispatch(activity)

        // Then
        nfcManager.nfcState.test {
            assertEquals(NfcState.Disabled, awaitItem())
        }
    }

    // ========================================
    // Tag Event Flow Tests
    // ========================================

    @Test
    fun `tagEvents emits KnownTag event when tag is recognized by UID`() = runTest {
        // Given
        val intent = mockk<Intent>()
        val tag = mockk<Tag>()

        mockkStatic(Build.VERSION::class)
        every { Build.VERSION.SDK_INT } returns Build.VERSION_CODES.R
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns tag
        every { tag.id } returns byteArrayOf(0x04, 0xA1.toByte())
        every { tag.techList } returns arrayOf("android.nfc.tech.NfcA")

        coEvery { nfcTagDao.getTagByUid(any()) } returns testEntity
        coEvery { nfcTagDao.updateLastUsed(any(), any()) } just Runs

        // When
        nfcManager.tagEvents.test {
            nfcManager.processTagIntent(intent)

            // Then
            val event = awaitItem()
            assertTrue("Event should be KnownTag", event is TagEvent.KnownTag)
            assertEquals("Tag ID should match", testTagId, (event as TagEvent.KnownTag).tag.id)
        }
    }

    @Test
    fun `tagEvents emits UnknownTag event for new tag`() = runTest {
        // Given
        val intent = mockk<Intent>()
        val tag = mockk<Tag>()

        mockkStatic(Build.VERSION::class)
        mockkStatic(Ndef::class)
        every { Build.VERSION.SDK_INT } returns Build.VERSION_CODES.R
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns tag
        every { tag.id } returns byteArrayOf(0x04, 0xA1.toByte())
        every { tag.techList } returns arrayOf("android.nfc.tech.NfcA")
        every { Ndef.get(tag) } returns null

        coEvery { nfcTagDao.getTagByUid(any()) } returns null

        // When
        nfcManager.tagEvents.test {
            nfcManager.processTagIntent(intent)

            // Then
            val event = awaitItem()
            assertTrue("Event should be UnknownTag", event is TagEvent.UnknownTag)
            assertEquals("UID should match", "04A1", (event as TagEvent.UnknownTag).uid)
        }
    }

    @Test
    fun `tagEvents emits InvalidTag event for unsupported tag`() = runTest {
        // Given
        val intent = mockk<Intent>()
        val tag = mockk<Tag>()

        mockkStatic(Build.VERSION::class)
        every { Build.VERSION.SDK_INT } returns Build.VERSION_CODES.R
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns tag
        every { tag.id } returns byteArrayOf(0x04, 0xA1.toByte())
        every { tag.techList } returns arrayOf("android.nfc.tech.MifareClassic")

        // When
        nfcManager.tagEvents.test {
            nfcManager.processTagIntent(intent)

            // Then
            val event = awaitItem()
            assertTrue("Event should be InvalidTag", event is TagEvent.InvalidTag)
            assertEquals(NfcError.TAG_NOT_SUPPORTED, (event as TagEvent.InvalidTag).error)
        }
    }
}
