package home.felipe.data.repository

import home.felipe.domain.repository.LoggerRepository
import timber.log.Timber
import javax.inject.Inject

class LoggerRepositoryImpl @Inject constructor() : LoggerRepository {
    override fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    override fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }

    override fun e(tag: String, message: String) {
        Timber.tag(tag).e(message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).e(throwable, message)
    }
}