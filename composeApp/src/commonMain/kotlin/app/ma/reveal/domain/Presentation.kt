package app.ma.reveal.domain

import kotlinx.datetime.Instant

data class Presentation(
    val id: String,
    val name: String,
    val path: String,
    val slideCount: Int,
    val addedDate: Instant,
    val isAsset: Boolean
)