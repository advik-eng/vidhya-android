package com.vidhya.app

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.webview.WebView
import com.vidhya.app.ui.theme.VidhyaTheme
import kotlinx.coroutines.delay
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VidhyaTheme {
                VidhyaApp()
            }
        }
    }
}

enum class AppTab(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    HOME("Home", Icons.Filled.Home),
    TUTOR("Tutor", Icons.Filled.SmartToy),
    NOTES("Notes", Icons.Filled.Create),
    READER("Reader", Icons.Filled.MenuBook),
    MORE("More", Icons.Filled.Bolt)
}

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val updatedAt: String
)

data class ChatMessage(
    val id: String,
    val text: String,
    val fromUser: Boolean
)

data class Flashcard(
    val question: String,
    val answer: String
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

data class ProgressMetric(
    val label: String,
    val value: Float,
    val color: ComposeColor
)

data class BrowserSite(
    val title: String,
    val url: String
)

private val sampleNotes = listOf(
    Note("n1", "Cell Structure", "Remember that mitochondria are the cell's energy factories and the nucleus stores DNA.", "Today"),
    Note("n2", "History Essay Plan", "Thesis: The industrial revolution changed labor, technology and city growth.", "Yesterday")
)

private val sampleMessages = listOf(
    ChatMessage("m1", "Can you explain the difference between mitosis and meiosis?", true),
    ChatMessage("m2", "Mitosis produces two genetically identical daughter cells for growth and repair. Meiosis produces four genetically different cells for reproduction. The key difference is that meiosis reduces chromosome number by half and involves two rounds of division.", false),
    ChatMessage("m3", "What is a good way to remember it?", true),
    ChatMessage("m4", "Think of it as: mitosis = copy, meiosis = mix and split. Use the phrase 'same for growth, different for gametes'.", false)
)

private val flashcards = listOf(
    Flashcard("What is the function of the Golgi apparatus?", "It modifies, packages, and transports proteins and lipids."),
    Flashcard("What does ATP provide to the cell?", "It stores and transfers usable energy for cellular work."),
    Flashcard("Why is diffusion important?", "It allows substances to move passively across membranes to balance concentrations.")
)

private val quizQuestions = listOf(
    QuizQuestion(
        "Which organelle is primarily responsible for ATP production?",
        listOf("Mitochondria", "Ribosome", "Nucleus", "Cell membrane"),
        0
    ),
    QuizQuestion(
        "Which process increases genetic variation in gametes?",
        listOf("Mitosis", "Osmosis", "Meiosis", "Photosynthesis"),
        2
    ),
    QuizQuestion(
        "A student wants to improve recall. Which strategy is most effective?",
        listOf("Highlighting only once", "Active recall and spaced repetition", "Reading the page without pausing", "Copying notes without checking"),
        1
    )
)

private val progressMetrics = listOf(
    ProgressMetric("Sessions", 14f, ComposeColor(0xFF1E3A8A)),
    ProgressMetric("Quiz score", 86f, ComposeColor(0xFF2E7D32)),
    ProgressMetric("Streak", 7f, ComposeColor(0xFFF59E0B))
)

private val browserSites = listOf(
    BrowserSite("Khan Academy", "https://www.khanacademy.org"),
    BrowserSite("OpenStax", "https://openstax.org"),
    BrowserSite("BBC Bitesize", "https://www.bbc.co.uk/bitesize")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VidhyaApp() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var noteList by remember { mutableStateOf(sampleNotes) }
    var notesLoading by remember { mutableStateOf(false) }
    var notesError by remember { mutableStateOf<String?>(null) }

    val tabs = listOf(
        AppTab.HOME,
        AppTab.TUTOR,
        AppTab.NOTES,
        AppTab.READER,
        AppTab.MORE
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vidhya") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(onNavigate = { selectedTab = it })
                1 -> TutorScreen()
                2 -> NotesScreen(
                    notes = noteList,
                    loading = notesLoading,
                    error = notesError,
                    onSave = { title, content ->
                        notesLoading = true
                        notesError = null
                        val updated = Note(
                            id = "n${System.currentTimeMillis()}",
                            title = title.ifBlank { "Untitled Note" },
                            content = content,
                            updatedAt = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
                        )
                        noteList = listOf(updated) + noteList
                        notesLoading = false
                    },
                    onDelete = { noteId ->
                        noteList = noteList.filterNot { it.id == noteId }
                    }
                )
                3 -> ReaderScreen()
                4 -> MoreScreen(onNavigate = { selectedTab = it })
            }
        }
    }
}

@Composable
fun HomeScreen(onNavigate: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WelcomeCard()
        }
        item {
            FeatureGrid(onNavigate = onNavigate)
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Good evening, Aanya",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Today's plan: review biology notes, complete a quiz, and finish a 25-minute study block.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun FeatureGrid(onNavigate: (Int) -> Unit) {
    val items = listOf(
        FeatureCard("AI Tutor", Icons.Filled.SmartToy, 1),
        FeatureCard("Notes", Icons.Filled.Create, 2),
        FeatureCard("Reader", Icons.Filled.MenuBook, 3),
        FeatureCard("Flashcards", Icons.Filled.FlashOn, 4),
        FeatureCard("Quiz", Icons.Filled.Quiz, 5),
        FeatureCard("Focus", Icons.Filled.Timer, 6),
        FeatureCard("Browser", Icons.Filled.Search, 7),
        FeatureCard("Progress", Icons.Filled.ReceiptLong, 8),
        FeatureCard("Export PDF", Icons.Filled.ReceiptLong, 9)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { item ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(item.tabIndex) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(item.icon, contentDescription = item.title)
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

data class FeatureCard(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tabIndex: Int
)

@Composable
fun TutorScreen() {
    val messages = remember { mutableStateOf(sampleMessages.toMutableList()) }
    var input by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Demo AI mode: local sample responses only. Connect a real service when configured.")
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages.value) { message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (message.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(12.dp),
                            color = if (message.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.material3.TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask a question") }
            )
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        messages.value = messages.value + ChatMessage("m${System.currentTimeMillis()}", input, true)
                        val answer = when {
                            input.contains("mitosis", ignoreCase = true) -> "Mitosis produces two identical cells, used for growth and repair. Meiosis produces four genetically different cells, used for reproduction."
                            input.contains("photosynthesis", ignoreCase = true) -> "Photosynthesis converts carbon dioxide and water into glucose using light energy in chloroplasts."
                            else -> "Try breaking the idea into: definition, example, memory cue, and a simple question you can test yourself with."
                        }
                        messages.value = messages.value + ChatMessage("m${System.currentTimeMillis()}", answer, false)
                        input = ""
                    } else {
                        Toast.makeText(context, "Type a question first", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun NotesScreen(
    notes: List<Note>,
    loading: Boolean,
    error: String?,
    onSave: (String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    if (notes.isEmpty()) {
        EmptyState("No notes yet", "Create a study note to capture concepts, summaries, and reminders.")
        return
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedNote = note
                                title = note.title
                                content = note.content
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(note.title, style = MaterialTheme.typography.titleMedium)
                            Text(note.updatedAt, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(note.content.take(80), maxLines = 2)
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.weight(1.5f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Editor", style = MaterialTheme.typography.titleLarge)
                if (loading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving...")
                    }
                }
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
                androidx.compose.material3.TextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") }
                )
                androidx.compose.material3.TextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    label = { Text("Content") }
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onSave(title, content)
                            selectedNote = null
                        },
                        enabled = title.isNotBlank() || content.isNotBlank()
                    ) {
                        Text("Save")
                    }
                    OutlinedButton(
                        onClick = {
                            selectedNote?.let { onDelete(it.id) }
                            title = ""
                            content = ""
                            selectedNote = null
                        }
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun ReaderScreen() {
    var selectedTerm by remember { mutableStateOf<String?>(null) }
    var selectedParagraph by remember { mutableStateOf<String?>(null) }
    val documentText = listOf(
        "Cell structure is the foundation of biological understanding. Each organelle has a specific role, from the nucleus storing DNA to mitochondria generating usable energy.",
        "The nucleus controls the cell's activities and contains chromosomes, while the membrane regulates what enters and exits the cell.",
        "Photosynthesis uses chlorophyll to capture light energy, enabling plants to convert carbon dioxide and water into glucose and oxygen.",
        "In organisms, specialized cells work together to form tissues and perform coordinated functions. Recognizing these patterns makes the study of biology clearer and more memorable."
    )

    val terms = listOf("nucleus", "mitochondria", "membrane", "photosynthesis")
    val popupOffset = Offset(180f, 200f)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                terms.forEach { term ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Text(
                        text = term,
                        modifier = Modifier
                            .clickable { selectedTerm = term }
                            .padding(end = 12.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            documentText.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(paragraph) {
                            detectTapGestures(
                                onLongPress = { selectedParagraph = paragraph },
                                onTap = { selectedTerm = null }
                            )
                        }
                        .padding(vertical = 4.dp)
                )
            }
        }

        if (selectedTerm != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(18.dp)
                    .padding(top = 80.dp),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(selectedTerm ?: "", fontWeight = FontWeight.Bold)
                    Text("Definition: The structure that stores genetic information and directs cellular functions.", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { }) { Text("Define") }
                        TextButton(onClick = { }) { Text("Simplify") }
                        TextButton(onClick = { }) { Text("Explain") }
                        TextButton(onClick = { }) { Text("Ask AI") }
                    }
                }
            }
        }

        if (selectedParagraph != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Paragraph tools", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { }) { Text("Simplify") }
                        TextButton(onClick = { }) { Text("Summarize") }
                        TextButton(onClick = { }) { Text("Explain") }
                        TextButton(onClick = { }) { Text("Ask AI") }
                    }
                    TextButton(onClick = { selectedParagraph = null }) { Text("Close") }
                }
            }
        }
    }
}

@Composable
fun MoreScreen(onNavigate: (Int) -> Unit) {
    val items = listOf(
        Triple("Flashcards", Icons.Filled.FlashOn, 4),
        Triple("Quiz", Icons.Filled.Quiz, 5),
        Triple("Focus Mode", Icons.Filled.Timer, 6),
        Triple("Study Browser", Icons.Filled.Search, 7),
        Triple("Progress", Icons.Filled.ReceiptLong, 8),
        Triple("Export PDF", Icons.Filled.ReceiptLong, 9)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { (title, icon, destination) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigate(destination) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(icon, contentDescription = title)
                    Text(title, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun EmptyState(title: String, message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.School, contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun StudyBrowserScreen() {
    val context = LocalContext.current
    var selectedUrl by rememberSaveable { mutableStateOf(browserSites.first().url) }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                browserSites.forEach { site ->
                    OutlinedButton(
                        onClick = { selectedUrl = site.url },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(site.title)
                    }
                }
            }
        }

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(selectedUrl)
                }
            },
            update = { webView ->
                if (webView.url != selectedUrl) {
                    webView.loadUrl(selectedUrl)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun FlashcardsScreen() {
    var index by remember { mutableIntStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    val card = flashcards[index % flashcards.size]

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Flashcard ${index + 1}/${flashcards.size}")
                Text(card.question, style = MaterialTheme.typography.headlineSmall)
                if (revealed) {
                    Divider()
                    Text(card.answer, style = MaterialTheme.typography.bodyLarge)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { revealed = !revealed }) { Text(if (revealed) "Hide answer" else "Reveal answer") }
                    Button(onClick = {
                        revealed = false
                        index = (index + 1) % flashcards.size
                    }) { Text("Next") }
                }
            }
        }
    }
}

@Composable
fun QuizScreen() {
    val questions = quizQuestions
    var current by remember { mutableIntStateOf(0) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    val question = questions[current]

    if (finished) {
        Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Quiz complete", style = MaterialTheme.typography.headlineMedium)
                    Text("Your score: ${score}/${questions.size}")
                    Button(onClick = {
                        current = 0
                        selectedIndex = -1
                        score = 0
                        finished = false
                    }) { Text("Retake Quiz") }
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Question ${current + 1}/${questions.size}")
                Text(question.question, style = MaterialTheme.typography.titleLarge)
                question.options.forEachIndexed { index, option ->
                    val correct = index == question.correctIndex
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedIndex = index
                            if (correct) score += 1
                            if (current < questions.size - 1) {
                                current += 1
                                selectedIndex = -1
                            } else {
                                finished = true
                            }
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(option, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FocusScreen() {
    var secondsLeft by remember { mutableIntStateOf(25 * 60) }
    var running by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf("Focus") }

    LaunchedEffect(running) {
        while (running && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        if (secondsLeft == 0) {
            running = false
            mode = "Break"
        }
    }

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60

    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(mode, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { running = !running }) {
                        Icon(if (running) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (running) "Pause" else "Start")
                    }
                    OutlinedButton(onClick = { running = false; secondsLeft = 25 * 60; mode = "Focus" }) {
                        Icon(Icons.Filled.Replay, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Reset")
                    }
                }
                Text("Android app restrictions for distracting apps require system permissions and unsupported devices will gracefully fall back to a normal focus timer.")
            }
        }
    }
}

@Composable
fun ProgressScreen() {
    val metrics = progressMetrics
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            metrics.forEach { metric ->
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(metric.label, style = MaterialTheme.typography.labelMedium)
                        Text("${metric.value.toInt()}", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Study rhythm", style = MaterialTheme.typography.titleLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    listOf(3, 5, 7, 8, 6, 9, 4).forEach { value ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height((value * 12).dp)
                                .background(
                                    Brush.verticalGradient(listOf(ComposeColor(0xFF1E3A8A), ComposeColor(0xFF60A5FA))),
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                        )
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Streak summary", style = MaterialTheme.typography.titleLarge)
                Text("7-day streak • 3 quizzes completed this week • 4 focus sessions logged")
            }
        }
    }
}

@Composable
fun ExportPdfScreen() {
    val context = LocalContext.current
    var status by remember { mutableStateOf("Ready to export") }
    var exporting by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Handwriting Style", style = MaterialTheme.typography.titleLarge)
                Text("Kalam")
                if (exporting) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Generating PDF...")
                    }
                } else {
                    Text(status)
                }
                Button(onClick = {
                    exporting = true
                    status = "Export started"
                    val outputPath = context.filesDir.absolutePath + "/vidhya-export.pdf"
                    val document = PdfDocument()
                    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                    val page = document.startPage(pageInfo)
                    val canvas = page.canvas
                    val paint = Paint().apply {
                        color = Color.BLACK
                        textSize = 24f
                    }
                    val titlePaint = Paint().apply {
                        color = Color.BLACK
                        textSize = 32f
                        isFakeBoldText = true
                    }
                    canvas.drawText("Vidhya Study Export", 40f, 60f, titlePaint)
                    canvas.drawText("Kalam handwriting style selected for notes export.", 40f, 120f, paint)
                    canvas.drawText("This is a demonstration export created locally in the app sandbox.", 40f, 180f, paint)
                    document.finishPage(page)
                    try {
                        FileOutputStream(outputPath).use { output ->
                            document.writeTo(output)
                        }
                        status = "PDF exported to $outputPath"
                    } catch (e: Exception) {
                        status = "Export failed: ${e.localizedMessage}"
                    } finally {
                        document.close()
                        exporting = false
                    }
                }) {
                    Text("Export PDF")
                }
            }
        }
    }
}

@Composable
fun ScreenRouter(selectedScreen: Int) {
    when (selectedScreen) {
        0 -> HomeScreen(onNavigate = {})
        1 -> TutorScreen()
        2 -> NotesScreen(
            notes = sampleNotes,
            loading = false,
            error = null,
            onSave = { _, _ -> },
            onDelete = {}
        )
        3 -> ReaderScreen()
        4 -> FlashcardsScreen()
        5 -> QuizScreen()
        6 -> FocusScreen()
        7 -> StudyBrowserScreen()
        8 -> ProgressScreen()
        9 -> ExportPdfScreen()
        else -> HomeScreen(onNavigate = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell() {
    val screens = listOf(
        "Home", "Tutor", "Notes", "Reader", "Flashcards", "Quiz", "Focus", "Browser", "Progress", "Export"
    )
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vidhya") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { selectedIndex = 3 }) {
                Icon(Icons.Filled.Book, contentDescription = "Open Reader")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ScreenRouter(selectedIndex)
        }
    }
}

