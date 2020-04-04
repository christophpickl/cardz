package com.github.christophpickl.cardz

import com.github.christophpickl.cardz.source.Source
import kotlin.math.ceil

object SourceTransformer {

    private const val CARDS_X_PER_PAGE = 2
    private const val CARDS_Y_PER_PAGE = 4

    fun transform(source: Source): List<Page> {
        val cardsPerPage = CARDS_X_PER_PAGE * CARDS_Y_PER_PAGE
        val pagesCount: Int = ceil(source.sentences.size.toDouble() / cardsPerPage).toInt()

        var leftoverSentences = source.sentences.toMutableList()
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

    fun splitLines(text: String): List<String> {
        var textRest = text
        val lines = mutableListOf<String>()
        val cardContentWidth = Constants.CARD_WIDTH - (2 * Constants.CARD_GAP)
        fun textIsTooLong(testText: String) = Constants.font.getWidthPoint(testText, Constants.FONT_SIZE) > cardContentWidth
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
