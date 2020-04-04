package com.github.christophpickl.cardz.source

object Sources {
    val dating = Dating
    val conversation = Conversation
}

interface Source {
    val fileTitle: String
    val sentences: List<String>
}
