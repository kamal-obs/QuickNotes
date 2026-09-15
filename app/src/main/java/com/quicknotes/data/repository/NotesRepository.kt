package com.quicknotes.data.repository

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun observeAll(): Flow<List<Note>>
    suspend fun getById(id: Long): Note?
    suspend fun save(id: Long?, title: String, snippet: String, tag: String)
    suspend fun delete(id: Long)
}
