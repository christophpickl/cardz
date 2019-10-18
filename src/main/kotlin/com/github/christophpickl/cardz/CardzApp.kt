package com.github.christophpickl.cardz

import com.itextpdf.text.Document
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

object CardzApp {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Generating Cardz PDF ...")
        val pdf = Document()
        val file = File("cardz/build/out.pdf")
        val writer = PdfWriter.getInstance(pdf, FileOutputStream(file))
        pdf.open()
        val canvas = writer.directContent

//        val font = FontFactory.getFont(FontFactory.COURIER, 16.0F, BaseColor.BLACK)
//        val chunk = Chunk("Hello Cardz", font)
//        pdf.add(chunk)

        // A4 size: 594 × 841
        // lower left X/Y, upper right X/Y
        val rect = Rectangle(0.0F, 0.0F, 593.0F, 840.0F)
        rect.border = Rectangle.BOX
        rect.borderWidth = 1.0F
//        canvas.rectangle(rect)

        val font = BaseFont.createFont()
        canvas.setFontAndSize(font, 12.0F)
        canvas.beginText()
        canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, "text", 50.0F, 50.0F, 0.0F)
//        canvas.setTextMatrix(pdf.left(), 50.0F)
//        canvas.showText("asdf")
        canvas.endText()

        pdf.close()
        println("Written PDF to: ${file.canonicalPath}")
    }
}
