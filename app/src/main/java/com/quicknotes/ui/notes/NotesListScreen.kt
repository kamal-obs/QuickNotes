package com.quicknotes.ui.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quicknotes.data.repository.Note
import com.quicknotes.ui.theme.Spacing
import com.quicknotes.ui.theme.tagColor

// SCREEN: home / notes list. Route: NOTES_LIST in ui/navigation/NavGraph.kt.
// @Composable fun = a function component. Params = props.
// The on...Click params are callbacks. NavGraph.kt decides where they navigate.
// `viewModel` is injected by Hilt (like a custom hook that returns a store).
// Its data comes from NotesListViewModel.kt.
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun NotesListScreen(
    onNoteClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onOpenUsersDemo: () -> Unit,
    viewModel: NotesListViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuickNotes") },
                actions = { TextButton(onClick = onOpenUsersDemo) { Text("API Demo") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        }
    ) { innerPadding ->

      if(notes.isEmpty()){
          Text(
              text = "No notes yet — tap + to add one",
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(innerPadding)
                  .padding(Spacing.md)
          )
      }
        else {
          LazyColumn(

              modifier = Modifier
                  .fillMaxWidth()
                  .padding(innerPadding),
              contentPadding = PaddingValues(Spacing.md),
              verticalArrangement = Arrangement.spacedBy(Spacing.md)
          )

          {
              item {
                  Text(
                      text = "${notes.size} ${if (notes.size>1) "notes" else "note"}",
                      // TASK 5 — string template + `if` as a value
                      // "${...}" = JS template literal `${...}` (but with normal quotes).
                      // Kotlin has no `a ? b : c`; `if (cond) b else c` returns a value instead.
                      // `if` conditions always need parentheses: if (x > 1), never if x > 1.
                      // 0 notes can't reach here: the `if (notes.isEmpty())` branch above handles it.
                      style = MaterialTheme.typography.labelMedium
                  )
              }
              items(notes, key = { it.id }) { note ->
                  NoteCard(note = note, onClick = { onNoteClick(note.id) })
              }
          }
      }
    }
}

// One card in the list, rendered by the items(...) block above.
// `private` = only usable in this file, like a component you don't export.
@Composable
private fun NoteCard(note: Note, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }
    // TASK 7 + 8 — local state, Compose's useState
    // React:   const [expanded, setExpanded] = useState(false)
    // Compose: var expanded by remember { mutableStateOf(false) }
    //   mutableStateOf(false) → creates the state; changing it triggers a re-render (recomposition)
    //   remember { }          → keeps the value between re-renders (without it, resets every time)
    //   var + by              → read as `expanded`, write as `expanded = true` (no setter function)
    // Must be INSIDE the function body (like hooks), and @Composable goes directly above `fun`.
    // Each NoteCard gets its own copy, so expanding one card doesn't expand the others.
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            Text(text = note.snippet, style = MaterialTheme.typography.bodyMedium, maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                // TASK 1 — maxLines + overflow = RN's <Text numberOfLines={2}>
                //   maxLines = 2               → stop after 2 lines
                //   TextOverflow.Ellipsis      → add "…" where the text is cut
                // TASK 7 — maxLines now depends on state:
                //   expanded → Int.MAX_VALUE (no limit), otherwise 2
                modifier = Modifier.padding(top = Spacing.sm),
                // TASK 2 — Modifier ≈ RN's `style` prop
                //   padding(top = Spacing.sm) ≈ style={{ paddingTop: 8 }}
                //   Spacing.sm = 8.dp (see ui/theme/Spacing.kt). dp = density-independent pixels.
                onTextLayout = { result -> isOverflowing = result.hasVisualOverflow },
                // TASK 8 — onTextLayout: Text calls this AFTER measuring itself.
                //   { result -> ... }        = JS (result) => ...
                //   result.hasVisualOverflow = true if text was cut off by maxLines
                //   Timeline: render (isOverflowing = false) → Text measures → callback sets
                //   isOverflowing → state changed → NoteCard re-runs → button appears. All in one frame.
 )
            if (isOverflowing || expanded) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Show less" else "Show more")
                }
            }
            // TASK 7 — the button flips the state: expanded = !expanded → re-render with new value.
            // TASK 8 — conditional rendering is a plain `if` (React: {cond && <Button />}).
            //   Why `|| expanded`: when expanded, all text fits → isOverflowing becomes false.
            //   Without `|| expanded`, "Show less" would disappear and you couldn't collapse the card.
            Row(
                modifier = Modifier.padding(top = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
                // TASK 4 — Row = <View style={{ flexDirection: 'row' }}>
                //   verticalAlignment = Alignment.CenterVertically ≈ alignItems: 'center'
                //   horizontalArrangement = Arrangement.spacedBy(...) ≈ gap
                //   Import: androidx.compose.ui.Alignment (Alt+Enter on red words)
            ) {
                val color = tagColor(note.tag)
                AssistChip(
                    onClick = {},
                    label = { Text(note.tag) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = color.copy(alpha = 0.15f),
                        labelColor = color
                    )
                )
                Text(text = note.lastUpdated, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                // TASK 3 — theme colours ≈ reading theme.colors.textSecondary from a ThemeProvider
                //   MaterialTheme.colorScheme.* comes from ui/theme/Theme.kt
                //   onSurfaceVariant = Slate600 (light) / Slate300 (dark) → dark mode for free.
                //   Prefer theme colours over hard-coded ones like Color.Gray.
            }
        }
    }
}
