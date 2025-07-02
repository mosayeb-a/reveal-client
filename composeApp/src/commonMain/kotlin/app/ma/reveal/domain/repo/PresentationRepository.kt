package app.ma.reveal.domain.repo

import app.ma.reveal.domain.Presentation
import app.ma.reveal.domain.Slide
import kotlinx.coroutines.flow.Flow

interface PresentationRepository {
    suspend fun save(
        presentationId: String,
        slides: List<Slide>,
        title: String
    ): Result<String>

    suspend fun get(
        id: String,
        fromAsset: Boolean = true,
        fromFiles: Boolean = true
    ): Result<Presentation>

    suspend fun getAllFrom(
        asset: Boolean = true,
        files: Boolean = true
    ): Result<List<Presentation>>
}