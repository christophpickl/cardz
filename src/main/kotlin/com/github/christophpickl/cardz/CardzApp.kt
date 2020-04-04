package com.github.christophpickl.cardz

import com.github.christophpickl.cardz.source.Sources
import java.io.File

object CardzApp {

    @JvmStatic
    fun main(args: Array<String>) {
        val source = Sources.conversation
        val pdf = File("cardz-${source.fileTitle}.pdf")
        PdfCreator.generate(target = pdf, source = source)
        println("Written PDF to: ${pdf.canonicalPath}")
        open(pdf)
    }

    private fun open(file: File) {
        ProcessBuilder("open", file.canonicalPath).start().waitFor()
    }

}
