package app.ma.reveal.common.ui

import kotlinx.serialization.Serializable

@Serializable
object PresentationListScreen

@Serializable
object CreateSlidesScreen

@Serializable
data class PresentScreen(val path: String)