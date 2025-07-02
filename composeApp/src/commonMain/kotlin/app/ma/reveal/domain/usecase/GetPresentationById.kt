package app.ma.reveal.domain.usecase

import app.ma.reveal.domain.Presentation
import app.ma.reveal.domain.repo.PresentationRepository
import io.github.aakira.napier.Napier

class GetPresentationById(private val repository: PresentationRepository) {
    suspend operator fun invoke(
        id: String,
        fromAsset: Boolean = true,
        fromFiles: Boolean = true
    ): Result<Presentation> {
        return try {
            repository.get(id, fromAsset, fromFiles).also { result ->
                result.fold(
                    onSuccess = { presentation ->
                        Napier.d("loaded presentation with id $id")
                    },
                    onFailure = { e ->
                        Napier.e("failed to load presentation with id $id: ${e.message}", e)
                    }
                )
            }
        } catch (e: Exception) {
            Napier.e("error in GetPresentationById for id $id: ${e.message}", e)
            Result.failure(e)
        }
    }
}