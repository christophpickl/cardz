package com.github.christophpickl.cardz

import com.github.christophpickl.cardz.source.Source
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

object PdfCreator {

    private const val PAGE_GAP_LEFT = 30.0F
    private const val PAGE_GAP_TOP = 10.0F
    private const val CARD_HEIGHT = 160.0F
    private const val lineHeight = 18.0F
    private const val MAX_LINES = 7
    private const val textPaddingTopSoTextReachesTopLineOfRectangle = 14.0F
    private val decorationImage = Image.getInstance(javaClass.getResource("/decoration.jpg"))

    fun generate(target: File, source: Source) {
        val document = Document()
        val writer = PdfWriter.getInstance(document, FileOutputStream(target))
        document.open()
        val canvas = writer.directContent
        fillPdf(document, canvas, SourceTransformer.transform(source))
        document.close()
    }

    private fun fillPdf(document: Document, canvas: PdfContentByte, pages: List<Page>) {
        pages.forEachIndexed { index, page ->
            page.cards.forEach { card ->
                document.drawCard(card, canvas)
            }
            if (index != pages.size - 1) {
                document.newPage()
            }
        }
    }

    private fun Document.drawCard(card: CardRect, canvas: PdfContentByte) {
        val indexX = card.index.first
        val indexY = card.index.second
        val lowerLeftX = PAGE_GAP_LEFT + (indexX * (Constants.CARD_WIDTH + Constants.CARD_GAP))
        val lowerLeftY = top() - PAGE_GAP_TOP - (indexY * (CARD_HEIGHT + Constants.CARD_GAP))

        canvas.drawCardBorder(lowerLeftX, lowerLeftY)
        canvas.drawCardText(card.text, lowerLeftX, lowerLeftY)

        decorationImage.setAbsolutePosition(lowerLeftX + 18.0F, lowerLeftY - 150.0F)
        add(decorationImage)
    }

    private fun PdfContentByte.drawCardBorder(lowerLeftX: Float, lowerLeftY: Float) {
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
        rectangle(rect)
    }

    private fun PdfContentByte.drawCardText(text: String, lowerLeftX: Float, lowerLeftY: Float) {
        val lines = SourceTransformer.splitLines(text)
        require(lines.size <= MAX_LINES) {
            "Maximum lines is $MAX_LINES but was ${lines.size} for: '${lines.joinToString("")}'"
        }
        val verticalAlignmentAddition = (CARD_HEIGHT - lines.size * lineHeight) / 2.0F

        setFontAndSize(Constants.font, Constants.FONT_SIZE)
        beginText()
        lines.forEachIndexed { index, line ->
            showTextAligned(PdfContentByte.ALIGN_CENTER, line,
                lowerLeftX + Constants.CARD_WIDTH / 2,
                lowerLeftY - textPaddingTopSoTextReachesTopLineOfRectangle - (index * lineHeight) - verticalAlignmentAddition,
                0.0F)
        }
        endText()
    }


}
