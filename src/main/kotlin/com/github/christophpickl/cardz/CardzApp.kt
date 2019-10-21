package com.github.christophpickl.cardz

import com.itextpdf.text.Document
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

object CardzApp {

    private val font = BaseFont.createFont()
    private const val PAGE_GAP = 10.0F
    private const val CARD_GAP = 20.0F
    private const val CARD_WIDTH = 260.0F
    private const val CARD_HEIGHT = 160.0F
    private const val FONT_SIZE = 12.0F
    private const val CARDS_X_PER_PAGE = 2
    private const val CARDS_Y_PER_PAGE = 4
    private const val lineHeight = 18.0F
    private const val MAX_LINES = 7

    @JvmStatic
    fun main(args: Array<String>) {
        generate(
            target = File("cardz/build/out.pdf"),
            sentences = DatingCards.cards
        )
    }

    private fun generate(target: File, sentences: List<String>) {
        println("Generating Cardz PDF ...")
        val document = Document()
        val writer = PdfWriter.getInstance(document, FileOutputStream(target))
        document.open()
        val canvas = writer.directContent
        fillPdf(document, canvas, transform(sentences))
        document.close()
        println("Written PDF to: ${target.canonicalPath}")
    }

    private fun transform(sentences: List<String>): List<Page> {
        val cardsPerPage = CARDS_X_PER_PAGE * CARDS_Y_PER_PAGE
        val pagesCount: Int = ceil(sentences.size.toDouble() / cardsPerPage).toInt()

        var leftoverSentences = sentences.toMutableList()
        val pages = mutableListOf<Page>()
        1.rangeTo(pagesCount).forEach {
            var currentX = 0
            var currentY = 0
            val cards = mutableListOf<CardRect>()

            val takeCards = leftoverSentences.take(cardsPerPage)
            leftoverSentences = leftoverSentences.subList(takeCards.size, leftoverSentences.size)
            takeCards.forEach { sentence ->
                cards += CardRect(currentX to currentY, sentence)
                currentX++
                if (currentX > CARDS_X_PER_PAGE - 1) {
                    currentX = 0
                    currentY++
                }
            }

            pages += Page(cards)
        }
        return pages
    }

    private fun fillPdf(document: Document, canvas: PdfContentByte, pages: List<Page>) {
        val top = document.top() // top: 806.0
        val right = document.right() // right: 559.0

        pages.forEachIndexed { index, page ->

            page.cards.forEach { card ->
                val indexX = card.index.first
                val indexY = card.index.second

                val lowerLeftX = PAGE_GAP + (indexX * (CARD_WIDTH + CARD_GAP))
                val lowerLeftY = top - PAGE_GAP - (indexY * (CARD_HEIGHT + CARD_GAP))
                val rect = Rectangle(
                    // lower left X/Y
                    lowerLeftX,
                    lowerLeftY,
                    // upper right X/Y
                    lowerLeftX + CARD_WIDTH,
                    lowerLeftY - CARD_HEIGHT
                )
                rect.border = Rectangle.BOX
                rect.borderWidth = 1.0F
                canvas.rectangle(rect)

                canvas.setFontAndSize(font, FONT_SIZE)
                canvas.beginText()
                splitLines(card.text).also { lines ->
                    require(lines.size <= MAX_LINES) {
                        "Maximum lines is $MAX_LINES but was ${lines.size} for: '${lines.joinToString("")}'"
                    }
                }.forEachIndexed { index, line ->
                    canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, line,
                        lowerLeftX + 10.0F, lowerLeftY - 30.0F - (index * lineHeight),
                        0.0F)
                }
                canvas.endText()
            }

            if (index != pages.size - 1) {
                document.newPage()
            }
        }
    }

    private fun splitLines(text: String): List<String> {
        var textRest = text
        val lines = mutableListOf<String>()
        val cardContentWidth = CARD_WIDTH - (2 * CARD_GAP)
        fun textIsTooLong(testText: String) = font.getWidthPoint(testText, FONT_SIZE) > cardContentWidth
        while (textIsTooLong(textRest)) {
            var currentLine = textRest
            while (textIsTooLong(currentLine)) {
                currentLine = currentLine.dropLast(1)
            }
            while (!currentLine.endsWith(' ')) {
                currentLine = currentLine.dropLast(1)
            }
            lines += currentLine
            textRest = textRest.substring(currentLine.length)
        }
        lines += textRest
        return lines
    }
}

data class Page(
    val cards: List<CardRect>
)

data class CardRect(
    val index: Pair<Int, Int>,
    val text: String
)
