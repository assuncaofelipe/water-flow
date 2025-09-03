package home.felipe.data.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import home.felipe.data.repository.AnalysisRepositoryImpl
import home.felipe.data.repository.AssetsRepositoryImpl
import home.felipe.data.repository.CsvRepositoryImpl
import home.felipe.data.repository.LoggerRepositoryImpl
import home.felipe.data.repository.ReportRepositoryImpl
import home.felipe.data.repository.TFLiteRepositoryImpl
import home.felipe.domain.json.GsonProvider
import home.felipe.domain.repository.AnalysisRepository
import home.felipe.domain.repository.AssetsRepository
import home.felipe.domain.repository.CsvRepository
import home.felipe.domain.repository.LoggerRepository
import home.felipe.domain.repository.ReportRepository
import home.felipe.domain.repository.TFLiteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideAnalysisRepo(): AnalysisRepository {
        return AnalysisRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideLoggerRepository(): LoggerRepository {
        return LoggerRepositoryImpl()
    }

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideGson(): GsonProvider {
        return GsonProvider
    }

    @Provides
    @Singleton
    fun providesAssetsRepo(
        app: Application
    ): AssetsRepository {
        return AssetsRepositoryImpl(app)
    }

    @Provides
    @Singleton
    fun provideCsvRepo(): CsvRepository {
        return CsvRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideTFLiteRepo(app: Application): TFLiteRepository {
        return TFLiteRepositoryImpl(app)
    }

    @Provides
    @Singleton
    fun provideReportRepo(app: Application): ReportRepository {
        return ReportRepositoryImpl(app)
    }
}