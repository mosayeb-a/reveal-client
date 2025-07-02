package app.ma.reveal.common.ui

import kotlinx.serialization.Serializable

@Serializable
data class PresentationListScreen(
    val presentationId: String? = null,
    val fromFiles: Boolean = true,
    val fromAssets: Boolean = true,
)

@Serializable
object CreateSlidesScreen

@Serializable
data class PresentScreen(val path: String)