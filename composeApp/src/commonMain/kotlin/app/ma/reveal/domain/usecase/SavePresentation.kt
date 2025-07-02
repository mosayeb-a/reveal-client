package app.ma.reveal.domain.usecase

import app.ma.reveal.domain.Slide
import app.ma.reveal.domain.repo.PresentationRepository

class SavePresentation(private val repository: PresentationRepository) {
    suspend operator fun invoke(
        presentationId: String,
        slides: List<Slide>,
        title: String
    ): Result<String> {
        return repository.save(presentationId, slides, title)
    }
}