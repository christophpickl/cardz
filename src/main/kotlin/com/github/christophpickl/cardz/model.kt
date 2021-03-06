package com.github.christophpickl.cardz

import com.itextpdf.text.pdf.BaseFont

object Constants {

    const val CARD_GAP = 20.0F
    const val CARD_WIDTH = 260.0F
    const val FONT_SIZE = 12.0F

    val font: BaseFont = BaseFont.createFont("JosefinSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)//.createFont()

}

data class Page(
    val cards: List<CardRect>
)

data class CardRect(
    val index: Pair<Int, Int>,
    val text: String
)
