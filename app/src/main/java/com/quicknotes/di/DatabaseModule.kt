package com.quicknotes.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
        Room.databaseBuilder(context, QuickNotesDatabase::class.java, "quicknotes.db")
            .addCallback(SeedSampleNotes)
            .build()

    @Provides
    fun provideNoteDao(database: QuickNotesDatabase): NoteDao = database.noteDao()
}

// Sample data so the list isn't empty while learning (title, snippet, tag).
// Mix of short and long snippets to show the maxLines = 2 ellipsis on the cards.
private val sampleNotes = listOf(
    Triple("Groceries", "Milk, eggs, bread.", "personal"),
    Triple(
        "Meeting notes",
        "Discussed the Q4 roadmap, hiring plans, the new onboarding flow, the database " +
            "migration timeline, and who owns the release checklist. Follow-ups due Friday.",
        "work"
    ),
    Triple(
        "Book ideas",
        "A story about a lighthouse keeper who finds a radio that picks up messages from " +
            "the future. Each chapter is one message. The twist is that the messages are " +
            "from the keeper themself, trying to warn about something they can't name.",
        "personal"
    ),
    Triple("Two-liner", "This one is just about long enough to wrap onto a second line of the card.", "ideas"),
    Triple(
        "Kotlin learning log",
        "Learned about composables, Modifiers, StateFlow, Hilt, and how NavHost works. " +
            "Next up: state hoisting, remember vs ViewModel state, and Room queries with Flow.",
        "work"
    )
)

// Runs every time the database opens. Inserts the samples only if the table is empty,
// so it won't duplicate them. Note: if you delete every note, they come back on the next launch.
private object SeedSampleNotes : RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        val isEmpty = db.query("SELECT COUNT(*) FROM notes").use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0) == 0
        }
        if (!isEmpty) return

        val now = System.currentTimeMillis()
        sampleNotes.forEachIndexed { index, (title, snippet, tag) ->
            db.execSQL(
                "INSERT INTO notes (title, snippet, tag, lastUpdated) VALUES (?, ?, ?, ?)",
                arrayOf<Any>(title, snippet, tag, now - index * 60_000L)
            )
        }
    }
}
