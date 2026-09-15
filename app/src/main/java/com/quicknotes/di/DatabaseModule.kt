package com.quicknotes.di

import android.content.Context
import androidx.room.Room
import com.quicknotes.data.local.NoteDao
import com.quicknotes.data.local.QuickNotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QuickNotesDatabase =
        Room.databaseBuilder(context, QuickNotesDatabase::class.java, "quicknotes.db").build()

    @Provides
    fun provideNoteDao(database: QuickNotesDatabase): NoteDao = database.noteDao()
}
