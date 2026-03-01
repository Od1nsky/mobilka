package com.pacificapp.burnout.di

import android.content.Context
import com.pacificapp.burnout.data.local.TokenManager
import com.pacificapp.burnout.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager = TokenManager(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        tokenManager: TokenManager
    ): AuthRepository = AuthRepository(tokenManager)

    @Provides
    @Singleton
    fun provideDashboardRepository(): DashboardRepository = DashboardRepository()

    @Provides
    @Singleton
    fun provideStressRepository(): StressRepository = StressRepository()

    @Provides
    @Singleton
    fun provideSleepRepository(): SleepRepository = SleepRepository()

    @Provides
    @Singleton
    fun provideWorkRepository(): WorkRepository = WorkRepository()

    @Provides
    @Singleton
    fun provideRecommendationRepository(): RecommendationRepository = RecommendationRepository()
}
