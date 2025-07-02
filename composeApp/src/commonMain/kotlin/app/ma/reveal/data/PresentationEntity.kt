package app.ma.reveal.data

import kotlinx.datetime.Instant

data class PresentationEntity(
    val id: String,
    val path: String,
    val content: String,
    val addedDate: Instant
)