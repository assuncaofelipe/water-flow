package home.felipe.domain.usecase
interface UseCase<R, P> {
    suspend fun execute(params: P): R
}