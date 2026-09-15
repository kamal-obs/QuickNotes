package com.quicknotes.data.repository

data class Note(
    val id: Long,
    val title: String,
    val snippet: String,
    val tag: String,
    val lastUpdated: String
)
