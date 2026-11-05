package com.example.safegrid

import com.example.safegrid.core.IncidentMerger
import com.example.safegrid.core.LoadsheddingParser
import com.example.safegrid.core.MergeIncident
import com.example.safegrid.core.PasswordHasher
import com.example.safegrid.core.ScheduleBuilder
import com.example.safegrid.core.Validators
import com.example.safegrid.ui.Lang
import com.example.safegrid.ui.S
import com.example.safegrid.ui.Strings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeGridLogicTest {
    @Test
    fun passwordHashIsStableAndNotPlaintext() {
        val hash = PasswordHasher.sha256("Secret123")
        assertEquals(64, hash.length)
        assertTrue(hash.matches(Regex("[0-9a-f]{64}")))
        assertNotEquals("Secret123", hash)
        assertEquals(hash, PasswordHasher.sha256("Secret123"))
        assertNotEquals(hash, PasswordHasher.sha256("Secret124"))
    }

    @Test
    fun validatorsRejectBadInput() {
        assertFalse(Validators.email("not-an-email"))
        assertFalse(Validators.email(""))
        assertTrue(Validators.email("kakuhle@example.com"))
        assertFalse(Validators.password("12345"))
        assertTrue(Validators.password("123456"))
        assertFalse(Validators.name("A"))
        assertTrue(Validators.name("Kakuhle"))
        assertFalse(Validators.description("short"))
        assertTrue(Validators.description("Broken street light"))
    }

    @Test
    fun eskomStatusMapsToStage() {
        assertEquals(0, LoadsheddingParser.stageFromStatus("1"))
        assertEquals(2, LoadsheddingParser.stageFromStatus(" 3 "))
        assertEquals(8, LoadsheddingParser.stageFromStatus("9"))
        assertNull(LoadsheddingParser.stageFromStatus("99"))
        assertNull(LoadsheddingParser.stageFromStatus("0"))
        assertNull(LoadsheddingParser.stageFromStatus("offline"))
    }

    @Test
    fun scheduleCoversFourDaysWhenStageIsActive() {
        val quiet = ScheduleBuilder.build(0)
        assertEquals(1, quiet.size)
        assertEquals("none", quiet.first().time)
        val stageTwo = ScheduleBuilder.build(2)
        assertEquals(4, stageTwo.size)
        assertTrue(stageTwo.all { it.stage == 2 && it.time != "none" })
    }

    @Test
    fun pendingLocalReportSurvivesCloudRefresh() {
        val local = listOf(
            MergeIncident("local", 20, pendingSync = true, payload = "queued"),
            MergeIncident("old", 5, pendingSync = false, payload = "kept")
        )
        val remote = listOf(
            MergeIncident("local", 20, pendingSync = false, payload = "stale"),
            MergeIncident("cloud", 30, pendingSync = false, payload = "new")
        )
        val merged = IncidentMerger.merge(local, remote)
        assertEquals(listOf("cloud", "local", "old"), merged.map { it.id })
        assertEquals("queued", merged.first { it.id == "local" }.payload)
        assertTrue(merged.first { it.id == "local" }.pendingSync)
    }

    @Test
    fun everyLanguageHasTheSameStrings() {
        S.entries.forEach { key ->
            val english = Strings.get(Lang.EN, key)
            assertTrue(english.isNotBlank())
            assertTrue(Strings.get(Lang.ZU, key).isNotBlank())
            assertTrue(Strings.get(Lang.AF, key).isNotBlank())
        }
        assertEquals("isiZulu", Lang.from("zu").label)
        assertEquals(Lang.EN, Lang.from("xx"))
    }
}
