package com.quicknotes.data.repository

import com.quicknotes.data.local.NoteDao
import com.quicknotes.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NotesRepository {

    override fun observeAll(): Flow<List<Note>> =
        noteDao.observeAll().map { entities -> entities.map { it.toNote() } }

    override suspend fun getById(id: Long): Note? =
        noteDao.getById(id)?.toNote()

    override suspend fun save(id: Long?, title: String, snippet: String, tag: String) {
        noteDao.upsert(
            NoteEntity(
                id = id ?: 0,
                title = title,
                snippet = snippet,
                tag = tag,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    override suspend fun delete(id: Long) {
        noteDao.getById(id)?.let { noteDao.delete(it) }
    }
}
