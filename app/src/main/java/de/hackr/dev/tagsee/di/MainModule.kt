package de.hackr.dev.tagsee.di

import android.content.Context
import de.hackr.dev.tagsee.data.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.hackr.dev.tagsee.network.TagseeApi
import de.hackr.dev.tagsee.network.TagseeApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Provides
    @Singleton
    fun provideRepository(
        @ApplicationContext context: Context
    ) = DataStoreRepository(context = context)
}

@Module
@InstallIn(ViewModelComponent::class)
object ApiModule {
    @Provides
    fun provideApiService() = TagseeApi.mockService as TagseeApiService
}
