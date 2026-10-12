package com.bharatopencams

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Forest = Color(0xFF236B4C)
private val ForestDark = Color(0xFF174D36)
private val Paper = Color(0xFFF7F8F3)
private val Ink = Color(0xFF16231E)
private val Muted = Color(0xFF6B786F)
private val PaleGreen = Color(0xFFE6F1E6)
private val Lime = Color(0xFFD4F080)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Paper
                ) {
                    DirectoryScreen(
                        onSuggestSource = {
                            val url = Uri.parse(
                                "https://github.com/vikasdocker/Bharat-Open-Cams/issues/new?template=feed-submission.yml"
                            )
                            startActivity(Intent(Intent.ACTION_VIEW, url))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DirectoryScreen(onSuggestSource: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Traffic", "Tourism", "Weather", "Environment")

    Scaffold(
        containerColor = Paper,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(37.dp)
                                .background(PaleGreen, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("◎", color = Forest, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text(
                                "Bharat Open Cams",
                                color = Ink,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Text("PUBLIC VIEWS · VERIFIED SOURCES", color = Muted, fontSize = 8.sp, letterSpacing = 1.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Paper)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
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
                    "Discover traffic, tourism and weather cameras intentionally shared by their official publishers.",
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
                label = { Text("Search city or area") },
                placeholder = { Text("For example, a city name") }
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("BROWSE BY CATEGORY", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 11.sp) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.drop(3).forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 11.sp) }
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(23.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(PaleGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("◎", color = Forest, fontSize = 29.sp)
                    }
                    Text(
                        "The verified directory is getting ready",
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        lineHeight = 25.sp
                    )
                    Text(
                        "No approved public feeds are listed yet. We will add a source only after checking the publisher, terms and permitted way to view it.",
                        color = Muted,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )
                    Text(
                        "A camera being online is not permission to republish it.",
                        color = ForestDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Button(
                        onClick = onSuggestSource,
                        colors = ButtonDefaults.buttonColors(containerColor = Forest),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Suggest an official source", modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PaleGreen, RoundedCornerShape(16.dp))
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("✓", color = ForestDark, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Permission first", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Official links, clear attribution, easy removal requests.", color = Muted, fontSize = 11.sp, lineHeight = 17.sp)
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