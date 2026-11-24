package com.quicknotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.quicknotes.ui.navigation.QuickNotesNavGraph
import com.quicknotes.ui.theme.QuickNotesTheme
import dagger.hilt.android.AndroidEntryPoint

// STARTUP PATH — STEP 2: the Activity, the real UI entry point.
// Android opens this when the user taps the app icon (the LAUNCHER entry in
// AndroidManifest.xml). In RN terms, this is index.js + AppRegistry.registerComponent.
// The app has only ONE Activity. Screens are composables swapped by the NavHost,
// not separate Activities.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // onCreate is called when the Activity starts, like a "mount" for the whole app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContent { } is the Compose root, like ReactDOM.render / the root <App />.
        // Everything inside it is a composable tree:
        //   QuickNotesTheme   (ui/theme/Theme.kt)          ~ <ThemeProvider>
        //     QuickNotesNavGraph (ui/navigation/NavGraph.kt) ~ <NavigationContainer><Stack.Navigator>
        // Next step: NavGraph.kt
        setContent {
            QuickNotesTheme {
                QuickNotesNavGraph()
            }
        }
    }
}
