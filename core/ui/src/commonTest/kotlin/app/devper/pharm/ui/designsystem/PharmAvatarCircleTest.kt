package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals

class PharmAvatarCircleTest {

    @Test
    fun initials_from_two_word_thai_name() {
        assertEquals("สป", initialsFrom("สมชาย ปรีดา"))
    }

    @Test
    fun initials_from_two_word_english_name() {
        assertEquals("JD", initialsFrom("john doe"))
    }

    @Test
    fun initials_from_single_word_takes_first_two_chars() {
        assertEquals("SO", initialsFrom("somchai"))
    }

    @Test
    fun initials_from_blank_returns_question() {
        assertEquals("?", initialsFrom(""))
        assertEquals("?", initialsFrom("   "))
    }

    @Test
    fun initials_from_multi_word_uses_first_two_words() {
        assertEquals("AB", initialsFrom("Alpha Beta Gamma Delta"))
    }
}
