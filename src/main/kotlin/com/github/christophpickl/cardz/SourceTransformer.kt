package com.github.christophpickl.cardz

import com.github.christophpickl.cardz.source.Source
import kotlin.math.ceil


object SourceTransformer {

    private const val cardsCountX = 2
    private const val cardsCountY = 4
    private const val cardsPerPage = cardsCountX * cardsCountY

    fun transform(source: Source): List<Page> {
        var leftoverSentences = source.sentences.toMutableList()
        val pages = mutableListOf<Page>()
        repeat(source.pagesCount) {
            var currentX = 0
            var currentY = 0
            val cards = mutableListOf<CardRect>()

            val sentencesForThisPage = leftoverSentences.take(cardsPerPage)
            leftoverSentences = leftoverSentences.subList(sentencesForThisPage.size, leftoverSentences.size)
            sentencesForThisPage.forEach { sentence ->
                cards += CardRect(currentX to currentY, sentence)
                currentX++
                if (currentX > cardsCountX - 1) {
                    currentX = 0
                    currentY++
                }
            }

            pages += Page(cards)
        }
        return pages
    }

    private val Source.pagesCount get() = ceil(sentences.size.toDouble() / cardsPerPage).toInt()

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
