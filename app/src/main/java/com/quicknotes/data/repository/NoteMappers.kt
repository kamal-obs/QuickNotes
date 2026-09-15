package com.quicknotes.data.repository

import com.quicknotes.data.local.NoteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val displayDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

fun NoteEntity.toNote(): Note = Note(
    id = id,
    title = title,
    snippet = snippet,
    tag = tag,
    lastUpdated = displayDateFormat.format(Date(lastUpdated))
)
