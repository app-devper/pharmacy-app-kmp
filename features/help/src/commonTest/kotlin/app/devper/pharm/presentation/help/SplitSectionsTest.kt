package app.devper.pharm.presentation.help

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SplitSectionsTest {

    @Test
    fun empty_markdown_returns_empty_list() {
        assertEquals(emptyList(), splitSections(""))
    }

    @Test
    fun markdown_without_h2_returns_empty_list() {
        val md = "# Title\n\nSome prose paragraph.\n\n### Sub heading\n\nMore prose."
        assertEquals(emptyList(), splitSections(md))
    }

    @Test
    fun single_h2_section_yields_one_section() {
        val md = "## เริ่มต้น\n\nวิธีเปิดบิลใหม่ กด F2 หรือสแกนบาร์โค้ด"
        val sections = splitSections(md)
        assertEquals(1, sections.size)
        assertEquals("เริ่มต้น", sections[0].title)
        assertTrue(sections[0].id.isNotBlank())
        assertTrue(sections[0].markdown.startsWith("## เริ่มต้น"))
    }

    @Test
    fun multiple_h2_sections_split_at_boundaries() {
        val md = """
            # Top Title

            preamble paragraph.

            ## หน้าขายยา
            สแกนบาร์โค้ดเพื่อเพิ่มยา

            ## สต็อก
            ตรวจสอบจำนวนคงเหลือ

            ## รายงาน
            export Excel ได้
        """.trimIndent()
        val sections = splitSections(md)
        assertEquals(3, sections.size)
        assertEquals(listOf("หน้าขายยา", "สต็อก", "รายงาน"), sections.map { it.title })
    }

    @Test
    fun h3_inside_section_does_not_split() {
        val md = """
            ## หลัก
            content

            ### sub-1
            sub content 1

            ### sub-2
            sub content 2

            ## ถัดไป
            next content
        """.trimIndent()
        val sections = splitSections(md)
        assertEquals(2, sections.size)
        assertTrue(sections[0].markdown.contains("### sub-1"))
        assertTrue(sections[0].markdown.contains("### sub-2"))
        assertTrue(sections[1].markdown.contains("next content"))
    }

    @Test
    fun crlf_line_endings_normalized() {
        val md = "## หัวข้อ\r\n\r\nบรรทัดเนื้อหา\r\n\r\n## หัวข้อสอง\r\nเนื้อหาสอง"
        val sections = splitSections(md)
        assertEquals(2, sections.size)
        assertEquals(listOf("หัวข้อ", "หัวข้อสอง"), sections.map { it.title })
    }

    @Test
    fun section_id_is_slugified_title_with_safe_chars() {
        val md = "## Hello World 123\nbody"
        val sections = splitSections(md)
        assertEquals(1, sections.size)
        assertTrue(sections[0].id.isNotBlank())
    }

    @Test
    fun section_markdown_includes_its_heading_line() {
        val md = "## หัวข้อ\nบรรทัดเนื้อหา"
        val sections = splitSections(md)
        assertTrue(sections[0].markdown.startsWith("## หัวข้อ"))
        assertTrue(sections[0].markdown.contains("บรรทัดเนื้อหา"))
    }

    @Test
    fun trailing_blank_lines_trimmed_from_section_markdown() {
        val md = "## หัวข้อ\nเนื้อหา\n\n\n## ต่อไป\nเนื้อหาสอง"
        val sections = splitSections(md)
        assertEquals(2, sections.size)
        assertTrue(!sections[0].markdown.endsWith("\n\n"))
    }
}
