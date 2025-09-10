package home.felipe.data.repository

import home.felipe.domain.repository.AnalysisRepository
import home.felipe.domain.vo.AnalysisSession
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepositoryImpl @Inject constructor() : AnalysisRepository {
    private val store = ConcurrentHashMap<String, AnalysisSession>()
    override fun create(session: AnalysisSession): String {
        store[session.id] = session
        return session.id
    }

    override fun get(sessionId: String): AnalysisSession? = store[sessionId]
    override fun update(session: AnalysisSession) {
        store[session.id] = session
    }

    override fun remove(sessionId: String) {
        store.remove(sessionId)
    }
}