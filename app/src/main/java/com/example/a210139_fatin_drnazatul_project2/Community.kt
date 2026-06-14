package com.example.a210139_fatin_drnazatul_project2

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavHostController, viewModel: ResQBiteViewModel) {
    val posts by viewModel.communityPosts.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val savedMeals by viewModel.savedMealsCount.collectAsState()
    var userStory by remember { mutableStateOf("") }
    var showCelebration by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = colorScheme.tertiary
                        )
                    }
                    Text(
                        text = "COMMUNITY",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.tertiary
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // impact card
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.tertiaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Total Impact", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("You've saved $savedMeals meals this month!")
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { (savedMeals % 20) / 20f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = colorScheme.tertiary,
                                    trackColor = colorScheme.onTertiaryContainer.copy(alpha = 0.2f)
                                )
                                Text(
                                    "${savedMeals % 20}/20 meals to next badge",
                                    fontSize = 11.sp,
                                    color = colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (showCelebration) "🎉" else "🏆", fontSize = 40.sp)
                                Button(
                                    onClick = {
                                        viewModel.incrementSavedMeals()
                                        showCelebration = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("+1 Meal", fontSize = 11.sp)
                                }
                            }
                        }
                        if (showCelebration) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "🎊 Amazing! Keep rescuing food!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            // share your rescue
            item {
                Text("Share Your Rescue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = userStory,
                    onValueChange = { userStory = it },
                    placeholder = { Text("What did you save today?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (userStory.isNotEmpty()) {
                                viewModel.addStory("Me", userStory)
                                userStory = ""
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = colorScheme.primary)
                        }
                    }
                )
            }

            // recent activity
            item {
                Text(
                    "Recent Activity",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = colorScheme.primary
                )
            }

            if (posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                }
            } else {
                items(posts) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        border = BorderStroke(1.dp, colorScheme.outlineVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(colorScheme.secondaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(post.userName.take(1), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(post.userName, fontWeight = FontWeight.Bold)
                                    Text(post.time, fontSize = 12.sp, color = colorScheme.outline)
                                }
                                IconButton(onClick = { viewModel.toggleLike(post.id) }) {
                                    Icon(
                                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (post.isLiked) Color.Red else colorScheme.outline
                                    )
                                }
                                Text("${post.likes}", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = post.action, fontSize = 15.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}