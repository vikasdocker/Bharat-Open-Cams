package com.bharatopencams

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val Forest = Color(0xFF236B4C)
private val ForestDark = Color(0xFF174D36)
private val Paper = Color(0xFFF7F8F3)
private val Ink = Color(0xFF16231E)
private val Muted = Color(0xFF6B786F)
private val PaleGreen = Color(0xFFE6F1E6)
private val Border = Color(0xFFE2E9DF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Paper) {
                    DirectoryScreen(
                        onSuggestSource = {
                            openExternalUrl("https://github.com/vikasdocker/Bharat-Open-Cams/issues/new?template=feed-submission.yml")
                        },
                        onOpenSource = { feed ->
                            // Open the publisher's official page; the app does not proxy or record streams.
                            openExternalUrl(feed.sourcePageUrl)
                        },
                        onReportSource = {
                            openExternalUrl("https://github.com/vikasdocker/Bharat-Open-Cams/issues/new?template=feed-removal.yml")
                        }
                    )
                }
            }
        }
    }

    private fun openExternalUrl(value: String) {
        val uri = runCatching { Uri.parse(value) }.getOrNull() ?: return
        if (uri.scheme != "https" || uri.host.isNullOrBlank()) return
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, uri)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DirectoryScreen(
    onSuggestSource: () -> Unit,
    onOpenSource: (CameraFeed) -> Unit,
    onReportSource: (CameraFeed) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedCity by remember { mutableStateOf("All cities") }
    var cityMenuExpanded by remember { mutableStateOf(false) }
    var reloadKey by remember { mutableIntStateOf(0) }
    var feeds by remember { mutableStateOf<List<CameraFeed>>(emptyList()) }
    var loadState by remember { mutableStateOf(CatalogLoadState.Loading) }

    LaunchedEffect(reloadKey) {
        loadState = CatalogLoadState.Loading
        try {
            feeds = withContext(Dispatchers.IO) { FeedCatalogRepository.loadApprovedFeeds() }
            loadState = CatalogLoadState.Loaded
            if (selectedCity != "All cities" && feeds.none { it.city == selectedCity }) {
                selectedCity = "All cities"
            }
        } catch (_: Exception) {
            feeds = emptyList()
            loadState = CatalogLoadState.Failed
        }
    }

    val categories = listOf("All", "Traffic", "Tourism", "Weather", "Environment", "Public places")
    val cities = listOf("All cities") + feeds.map { it.city }.distinct().sorted()
    val filteredFeeds = feeds.filter { feed ->
        val normalizedQuery = query.trim().lowercase()
        val matchesQuery = normalizedQuery.isBlank() ||
            listOf(feed.name, feed.city, feed.area, feed.publisherName, feed.description, feed.categoryLabel)
                .any { it.lowercase().contains(normalizedQuery) }
        val matchesCity = selectedCity == "All cities" || feed.city == selectedCity
        val matchesCategory = selectedCategory == "All" || feed.categoryLabel == selectedCategory
        matchesQuery && matchesCity && matchesCategory
    }

    Scaffold(
        containerColor = Paper,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(37.dp).background(PaleGreen, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("◎", color = Forest, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text("Bharat Open Cams", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text("PUBLIC VIEWS · VERIFIED SOURCES", color = Muted, fontSize = 8.sp, letterSpacing = 1.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Paper)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            Spacer(Modifier.height(2.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Explore public views.\nRespect every boundary.",
                    color = Ink,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Discover public views shared by their publishers. Only reviewed and authorized sources appear here.",
                    color = Muted,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                label = { Text("Search city, area or source") },
                placeholder = { Text("Enter a city or camera name") }
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("SELECT CITY", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                Box {
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { cityMenuExpanded = true },
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(selectedCity, color = Ink, fontSize = 13.sp)
                            Text("⌄", color = Forest, fontSize = 18.sp)
                        }
                    }
                    DropdownMenu(
                        expanded = cityMenuExpanded,
                        onDismissRequest = { cityMenuExpanded = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city) },
                                onClick = {
                                    selectedCity = city
                                    cityMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("BROWSE BY CATEGORY", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 11.sp) }
                        )
                    }
                }
            }

            when (loadState) {
                CatalogLoadState.Loading -> LoadingCard()
                CatalogLoadState.Failed -> StatusCard(
                    title = "Could not load the public catalog",
                    body = "Check your connection and try again. The app never falls back to unreviewed camera URLs.",
                    actionLabel = "Retry",
                    onAction = { reloadKey++ }
                )
                CatalogLoadState.Loaded -> {
                    if (feeds.isEmpty()) {
                        StatusCard(
                            title = "No approved feeds published yet",
                            body = "The public catalog is empty while official camera access and permitted viewing terms are verified. We will not list private or merely exposed CCTV.",
                            actionLabel = "Suggest an official source",
                            onAction = onSuggestSource
                        )
                    } else if (filteredFeeds.isEmpty()) {
                        StatusCard(
                            title = "No matching public views",
                            body = "Try a different city, category or search term.",
                            actionLabel = "Clear filters",
                            onAction = {
                                query = ""
                                selectedCategory = "All"
                                selectedCity = "All cities"
                            }
                        )
                    } else {
                        Text(
                            "${filteredFeeds.size} VERIFIED ${if (filteredFeeds.size == 1) "SOURCE" else "SOURCES"}",
                            color = Forest,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.4.sp
                        )
                        filteredFeeds.forEach { feed ->
                            FeedCard(
                                feed = feed,
                                onOpen = { onOpenSource(feed) },
                                onReport = { onReportSource(feed) }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().background(PaleGreen, RoundedCornerShape(16.dp)).padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("✓", color = ForestDark, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Permission first", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("The source's official page opens separately. No feeds are proxied or recorded.", color = Muted, fontSize = 11.sp, lineHeight = 17.sp)
                }
            }

            Text(
                "Bharat Open Cams is an independent project and is not affiliated with a government authority.",
                modifier = Modifier.padding(top = 3.dp),
                color = Muted,
                fontSize = 10.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            CircularProgressIndicator(color = Forest, modifier = Modifier.size(30.dp), strokeWidth = 3.dp)
            Text("Loading verified public sources…", color = Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.padding(23.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.size(52.dp).background(PaleGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("◎", color = Forest, fontSize = 29.sp)
            }
            Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 25.sp)
            Text(body, color = Muted, fontSize = 13.sp, lineHeight = 21.sp)
            Text("A camera being online is not permission to republish it.", color = ForestDark, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(actionLabel, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun FeedCard(feed: CameraFeed, onOpen: () -> Unit, onReport: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = PaleGreen, shape = RoundedCornerShape(50)) {
                    Text(feed.categoryLabel, color = ForestDark, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                Spacer(Modifier.weight(1f))
                Text("✓ Verified", color = Forest, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text(feed.name, color = Ink, fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                listOfNotNull(feed.city, feed.area.takeIf { it.isNotBlank() }).joinToString(" · "),
                color = Muted,
                fontSize = 11.sp
            )
            Text(feed.description.ifBlank { "Open the publisher's official page for current viewing information." }, color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
            Text("Publisher: ${feed.publisherName}", color = Muted, fontSize = 10.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Verified ${feed.verifiedAt}", color = Muted, fontSize = 9.sp)
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = Forest),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Open official view ↗", fontSize = 11.sp)
                }
            }
            TextButton(
                onClick = onReport,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp, vertical = 0.dp)
            ) {
                Text("Report listing / request removal", color = Muted, fontSize = 10.sp)
            }
        }
    }
}

private enum class CatalogLoadState { Loading, Loaded, Failed }
