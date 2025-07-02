package app.ma.reveal.domain.usecase

import app.ma.reveal.domain.Presentation
import app.ma.reveal.domain.repo.PresentationRepository
import io.github.aakira.napier.Napier

class GetPresentations(private val repository: PresentationRepository) {
    suspend operator fun invoke(
        asset: Boolean = true,
        files: Boolean = true
    ): Result<List<Presentation>> {
        return try {
            repository.getAllFrom(asset, files).also { result ->
                result.fold(
                    onSuccess = { presentations ->
                        Napier.d("loaded ${presentations.size} presentations")
                    },
                    onFailure = { e ->
                        Napier.e("failed to load presentations: ${e.message}", e)
                    }
                )
            }
        } catch (e: Exception) {
            Napier.e("error in getPresentations: ${e.message}", e)
            Result.failure(e)
        }
    }
}