package com.example.a210139_fatin_drnazatul_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class ChatMessage(
    val text: String,
    val isMe: Boolean,
    val time: String
)

@Composable
fun MessagesScreen(navController: NavHostController) {
    val colorScheme = MaterialTheme.colorScheme
    var searchQuery by remember { mutableStateOf("") }
    var selectedChat by remember { mutableStateOf<ChatPreview?>(null) }

    val chats = listOf(
        ChatPreview("NearBakery", "Your croissants are ready for pickup!", "10:30 AM", isUnread = true),
        ChatPreview("Mira", "Is the tuna still available?", "Yesterday"),
        ChatPreview("Amar", "Thanks for the bread!", "Monday")
    )

    // initial messages per chat
    val chatMessages = remember {
        mutableStateMapOf(
            "NearBakery" to mutableStateListOf(
                ChatMessage("Hi! I saw your croissant listing.", true, "10:25 AM"),
                ChatMessage("Yes! They're fresh from this morning.", false, "10:26 AM"),
                ChatMessage("Your croissants are ready for pickup!", false, "10:30 AM")
            ),
            "Mira" to mutableStateListOf(
                ChatMessage("Hey, is the tuna still available?", false, "Yesterday"),
                ChatMessage("Yes it is! Come pick it up anytime.", true, "Yesterday")
            ),
            "Amar" to mutableStateListOf(
                ChatMessage("Thanks for the bread!", false, "Monday"),
                ChatMessage("No problem! Glad it helped.", true, "Monday")
            )
        )
    }

    if (selectedChat != null) {
        // chat detail screen
        val chat = selectedChat!!
        val messages = chatMessages[chat.name] ?: mutableStateListOf()
        var newMessage by remember { mutableStateOf("") }
        val listState = rememberLazyListState()

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        Scaffold(
            topBar = {
                Surface(
                    color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        IconButton(onClick = { selectedChat = null }) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = colorScheme.tertiary)
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(chat.name.take(1), fontWeight = FontWeight.Bold, color = colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(chat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Online", fontSize = 12.sp, color = Color.Green)
                        }
                    }
                }
            },
            bottomBar = {
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newMessage,
                            onValueChange = { newMessage = it },
                            placeholder = { Text("Type a message...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(25.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colorScheme.surface,
                                unfocusedContainerColor = colorScheme.surface
                            )
                        )
                        IconButton(
                            onClick = {
                                if (newMessage.isNotEmpty()) {
                                    chatMessages[chat.name]?.add(
                                        ChatMessage(newMessage, true, "Now")
                                    )
                                    newMessage = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(colorScheme.primary, CircleShape)
                        ) {
                            Icon(Icons.Default.Send, null, tint = Color.White)
                        }
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(messages) { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (message.isMe) 16.dp else 4.dp,
                                    bottomEnd = if (message.isMe) 4.dp else 16.dp
                                ),
                                color = if (message.isMe) colorScheme.primary else colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = message.text,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    color = if (message.isMe) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = message.time,
                                fontSize = 10.sp,
                                color = colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    } else {
        // chat list screen
        Scaffold(
            topBar = {
                Surface(
                    color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = colorScheme.tertiary)
                        }
                        Text(
                            text = "MESSAGES",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.tertiary
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(colorScheme.background)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    placeholder = { Text("Search conversations...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorScheme.outlineVariant,
                        focusedContainerColor = colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(chats.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.lastMsg.contains(searchQuery, ignoreCase = true)
                    }) { chat ->
                        ChatItem(chat, colorScheme, onClick = { selectedChat = chat })
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: ChatPreview, colorScheme: ColorScheme, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.name.take(1), fontWeight = FontWeight.Bold, color = colorScheme.onPrimaryContainer)
            }
            Box(
                modifier = Modifier.size(12.dp).background(Color.Green, CircleShape)
                    .border(2.dp, colorScheme.background, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.name,
                fontWeight = if (chat.isUnread) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = chat.lastMsg,
                fontSize = 14.sp,
                color = if (chat.isUnread) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                maxLines = 1,
                fontWeight = if (chat.isUnread) FontWeight.Medium else FontWeight.Normal
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = chat.time,
                fontSize = 12.sp,
                color = if (chat.isUnread) colorScheme.primary else colorScheme.outline
            )
            if (chat.isUnread) {
                Box(
                    modifier = Modifier.padding(top = 4.dp).size(18.dp)
                        .background(colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}