package com.github.christophpickl.cardz

import com.itextpdf.text.Document
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

object CardzApp {

    private const val PAGE_GAP_LEFT = 30.0F
    private const val PAGE_GAP_TOP = 10.0F
    private const val CARD_HEIGHT = 160.0F
    private const val lineHeight = 18.0F
    private const val MAX_LINES = 7
    private const val textPaddingLeft = 10.0F
    private const val textPaddingTopSoTextReachesTopLineOfRectangle = 14.0F

    @JvmStatic
    fun main(args: Array<String>) {
        val pdf = File("cardz/build/out.pdf")
        generate(
            target = pdf,
            sentences = DatingCards.cards
        )
        ProcessBuilder("open", pdf.canonicalPath).start().waitFor()
    }

    private fun generate(target: File, sentences: List<String>) {
        println("Generating Cardz PDF ...")
        val document = Document()
        val writer = PdfWriter.getInstance(document, FileOutputStream(target))
        document.open()
        val canvas = writer.directContent
        fillPdf(document, canvas, ContentPreparer.transform(sentences))
        document.close()
        println("Written PDF to: ${target.canonicalPath}")
    }

    private fun fillPdf(document: Document, canvas: PdfContentByte, pages: List<Page>) {
        val top = document.top() // top: 806.0
        val right = document.right() // right: 559.0

        pages.forEachIndexed { index, page ->

            page.cards.forEach { card ->
                val indexX = card.index.first
                val indexY = card.index.second

                val lowerLeftX = PAGE_GAP_LEFT + (indexX * (Constants.CARD_WIDTH + Constants.CARD_GAP))
                val lowerLeftY = top - PAGE_GAP_TOP - (indexY * (CARD_HEIGHT + Constants.CARD_GAP))
                val rect = Rectangle(
                    // lower left X/Y
                    lowerLeftX,
                    lowerLeftY,
                    // upper right X/Y
                    lowerLeftX + Constants.CARD_WIDTH,
                    lowerLeftY - CARD_HEIGHT
                )
                rect.border = Rectangle.BOX
                rect.borderWidth = 1.0F
                canvas.rectangle(rect)

                canvas.setFontAndSize(Constants.font, Constants.FONT_SIZE)
                canvas.beginText()
                val lines = ContentPreparer.splitLines(card.text)
                require(lines.size <= MAX_LINES) {
                    "Maximum lines is $MAX_LINES but was ${lines.size} for: '${lines.joinToString("")}'"
                }

                val verticalAlignmentAddition = (CARD_HEIGHT - lines.size * lineHeight) / 2.0F
                lines.forEachIndexed { index, line ->
                    canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, line,
                        lowerLeftX + textPaddingLeft,
                        lowerLeftY - textPaddingTopSoTextReachesTopLineOfRectangle - (index * lineHeight) - verticalAlignmentAddition,
                        0.0F)
                }
                canvas.endText()
            }

            if (index != pages.size - 1) {
                document.newPage()
            }
        }
    }

}

data class Page(
    val cards: List<CardRect>
)

data class CardRect(
    val index: Pair<Int, Int>,
    val text: String
)
