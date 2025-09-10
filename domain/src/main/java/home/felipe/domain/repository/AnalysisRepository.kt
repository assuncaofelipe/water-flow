package home.felipe.domain.repository

import home.felipe.domain.vo.AnalysisSession

interface AnalysisRepository {
    fun create(session: AnalysisSession): String
    fun get(sessionId: String): AnalysisSession?
    fun update(session: AnalysisSession)
    fun remove(sessionId: String)
}