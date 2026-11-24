# Compose Mini Tasks — Study Notes

Small exercises done in QuickNotes, one concept each. Every solved line in the code has a
`// TASK n` comment under it — search the project for `// TASK` (Ctrl+Shift+F) to jump to them.

**Shortcuts used so far**

| Shortcut | What it does | VS Code equivalent |
|---|---|---|
| Alt+Enter | Quick fix / add missing import | Ctrl+. |
| Ctrl+Alt+L | Reformat file | Format Document (Prettier) |
| Ctrl+Click / Ctrl+B | Go to definition | F12 |
| Ctrl+Shift+N | Open file by name | Ctrl+P |
| Ctrl+Shift+F | Search whole project | Ctrl+Shift+F |

---

## Task 1 — Limit the snippet to 2 lines

**File:** `ui/notes/NotesListScreen.kt` → `NoteCard`

**Problem:** long notes made cards very tall.

**Solution:**
```kotlin
Text(
    text = note.snippet,
    style = MaterialTheme.typography.bodyMedium,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)
```
Import: `androidx.compose.ui.text.style.TextOverflow`

**Concept:** Compose splits RN's `<Text numberOfLines={2}>` into two parameters:
- `maxLines` — how many lines at most
- `overflow` — what to do with the rest (`Ellipsis` adds "…")

**Lesson:** red word = missing import → Alt+Enter.

---

## Task 2 — Gap between title and snippet

**File:** `NotesListScreen.kt` → snippet `Text`

**Solution:**
```kotlin
modifier = Modifier.padding(top = Spacing.sm)
```

**Concept:** `Modifier` ≈ RN's `style` prop, but it's a chain and **order matters**
(`.padding().background()` ≠ `.background().padding()`).

| Compose | RN |
|---|---|
| `Modifier.padding(top = 8.dp)` | `style={{ paddingTop: 8 }}` |
| `Modifier.fillMaxWidth()` | `style={{ width: '100%' }}` |

`Spacing` scale (`ui/theme/Spacing.kt`): xs 4, sm 8, md 16, lg 24, xl 32 — all in `dp`
(density-independent pixels, same as RN's unitless numbers).

**Lesson:** copy patterns from nearby code — the `Row` below already used the same modifier.

---

## Task 3 — Softer colour for the date

**File:** `NotesListScreen.kt` → date `Text`

**Solution:**
```kotlin
Text(
    text = note.lastUpdated,
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant
)
```

**Concept:** `MaterialTheme.colorScheme` is the app's palette, defined in `ui/theme/Theme.kt`.
Like reading `theme.colors.textSecondary` from a ThemeProvider.
`onSurfaceVariant` = Slate600 in light mode, Slate300 in dark mode → **dark mode for free**.

**Lesson:** prefer theme colours over hard-coded ones like `Color.Gray`.

---

## Task 4 — Centre the date next to the tag chip

**File:** `NotesListScreen.kt` → `Row` in `NoteCard`

**Solution:**
```kotlin
Row(
    modifier = Modifier.padding(top = Spacing.sm),
    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    verticalAlignment = Alignment.CenterVertically
) { ... }
```
Import: `androidx.compose.ui.Alignment` (pick this one if several are offered)

**Concept:** layout is flexbox-like.

| Compose | RN |
|---|---|
| `Row` | `<View style={{ flexDirection: 'row' }}>` |
| `Column` | `<View>` (column is the default) |
| `verticalAlignment = Alignment.CenterVertically` (in a Row) | `alignItems: 'center'` |
| `horizontalArrangement = Arrangement.spacedBy(8.dp)` | `gap: 8` |

---

## Task 5 — "1 note" vs "2 notes"

**File:** `NotesListScreen.kt` → count text at the top of the list

**Solution:**
```kotlin
text = "${notes.size} ${if (notes.size > 1) "notes" else "note"}"
```

**Concepts:**
- String templates: `"${x}"` = JS `` `${x}` `` (normal quotes, not backticks). Simple names can skip braces: `"$name"`.
- `if` is an **expression** (returns a value). Kotlin has **no** `a ? b : c` — use `if (a) b else c`.

**Mistake made:** `if notes.size>1 ...` → won't compile. The condition **always** needs parentheses: `if (notes.size > 1)`.

**Why 0 is safe:** this code is in the `else` of `if (notes.isEmpty())`, so the list is never empty here.

---

## Task 6 — Character counter on the edit screen

**File:** `ui/noteedit/NoteEditScreen.kt` → "Note" `OutlinedTextField`

**Solution:**
```kotlin
OutlinedTextField(
    value = uiState.snippet,
    onValueChange = viewModel::onSnippetChange,
    label = { Text("Note") },
    modifier = Modifier.fillMaxWidth().weight(1f),
    supportingText = { Text("${uiState.snippet.length} characters") }
)
```

**Concept — slot parameters:** `label` and `supportingText` take a **composable lambda** `{ ... }`,
not a string. Like passing JSX as a prop: `<TextField label={<Text>Note</Text>} />`.
You can put any UI inside, not just `Text`.

**Why it updates live:** keystroke → `onSnippetChange` → ViewModel updates `uiState` →
Compose re-runs the screen → new length shown. Same idea as `setState` → re-render.

**Bonus (optional):** `"1 character"` vs `"2 characters"` using Task 5's pattern.

---

## Task 7 — "Show more / Show less"

**File:** `NotesListScreen.kt` → `NoteCard`

**Solution:**
```kotlin
@Composable
private fun NoteCard(note: Note, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = note.snippet,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Spacing.sm)
            )
            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Show less" else "Show more")
            }
            // Row(...) unchanged
        }
    }
}
```
Imports: `remember`, `mutableStateOf`, `setValue` (and `getValue`) from `androidx.compose.runtime`.

**Concept — local state = `useState`:**
```js
const [expanded, setExpanded] = useState(false)      // React
```
```kotlin
var expanded by remember { mutableStateOf(false) }   // Compose
```
- `mutableStateOf(false)` — creates state; changing it triggers a re-render (**recomposition**)
- `remember { }` — keeps the value across re-renders (without it, it resets every time)
- `var` + `by` — read as `expanded`, write as `expanded = true`; no setter function

Each card has its **own** state, so expanding one doesn't expand the others.

**Mistake made:** the state line was placed between `@Composable` and `fun`, i.e. outside the function.
Like hooks, it must be **inside** the function body, and `@Composable` must sit directly above `fun`.

---

## Task 8 — Only show the button when text is actually cut off

**File:** `NotesListScreen.kt` → `NoteCard`

**Problem:** short notes ("Groceries") showed a useless "Show more".

**Solution (additions to Task 7):**
```kotlin
var expanded by remember { mutableStateOf(false) }
var isOverflowing by remember { mutableStateOf(false) }

Text(
    text = note.snippet,
    style = MaterialTheme.typography.bodyMedium,
    maxLines = if (expanded) Int.MAX_VALUE else 2,
    overflow = TextOverflow.Ellipsis,
    modifier = Modifier.padding(top = Spacing.sm),
    onTextLayout = { result -> isOverflowing = result.hasVisualOverflow }
)
if (isOverflowing || expanded) {
    TextButton(onClick = { expanded = !expanded }) {
        Text(if (expanded) "Show less" else "Show more")
    }
}
```

**Concept 1 — `onTextLayout`:** `Text` calls it **after** measuring itself.

| Piece | Meaning | JS |
|---|---|---|
| `{ result -> ... }` | lambda | `(result) => ...` |
| `result.hasVisualOverflow` | `true` if text was cut off | |
| `isOverflowing = ...` | update state | `setIsOverflowing(...)` |

Timeline (all within one frame):
1. First render — `isOverflowing = false`, no button
2. `Text` measures itself within the card width and `maxLines = 2`
3. Callback fires with the result
4. State changes → `NoteCard` re-runs → button appears

**Concept 2 — conditional rendering** is a plain `if`: React `{cond && <Button />}` → Compose `if (cond) { Button() }`.

**Why `|| expanded`:** when expanded, everything fits → `isOverflowing` becomes `false`.
Without it, "Show less" disappears and you can't collapse the card. (Try removing it to see.)

---

## Roadmap — what's left

Tasks 1–8 covered **UI basics and local state**. Rough plan for the rest
(~45 more small tasks, so about 55 in total):

| Phase | Topics | Tasks |
|---|---|---|
| 1. UI basics ✅ | Text, Modifier, theme, Row/Column, slots, `remember` | 1–8 (done) |
| 1b. More UI | Box, Spacer, Icons, IconButton, Image, Surface, LazyColumn details | ~5 |
| 2. State | state hoisting, `rememberSaveable`, ViewModel + StateFlow, search/filter | ~10 |
| 3. Navigation | add a screen, pass arguments, back stack, Settings screen | ~6 |
| 4. Room (database) | delete note, swipe to delete, new column, queries, migrations | ~8 |
| 5. Coroutines + network | `suspend`, `launch`, Retrofit, Loading/Success/Error, retry | ~8 |
| 6. Kotlin language | null safety (`?`, `?.`, `?:`), data/sealed classes, extension functions, `let`/`apply` | spread through all phases |
| 7. Android platform | lifecycle, rotation, string resources, permissions, a first unit test | ~6 |

After that: build a small app of your own from scratch without these notes — that's the real test.
