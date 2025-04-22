package com.example.uiappchat.ui.theme.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmojiPicker(onEmojiSelected: (String) -> Unit, onDismiss: () -> Unit) {

    val emojiCategories = listOf(
        "Phổ biến" to listOf(
            "😀", "😂", "🥰", "😍", "😊", "😎", "🤩", "😢", "😭", "😡",
            "👍", "👎", "❤️", "💕", "🔥", "🎉", "🎂", "🎁", "🌹", "🌞"),
        "Mặt cười" to listOf("😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇"),
        "Tình cảm" to listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "💕", "💞", "💓"),
        "Động vật" to listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯"),
        "Thực phẩm" to listOf("🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈")
    )

    var selectedCategory by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2D3B4D))
            .padding(8.dp)
    ) {
        // Thanh tiêu đề
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Emoji",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Đóng",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDismiss() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Danh mục emoji
        ScrollableTabRow(
            selectedTabIndex = selectedCategory,
            backgroundColor = Color(0xFF2D3B4D),
            contentColor = Color.White,
            edgePadding = 0.dp
        ) {
            emojiCategories.forEachIndexed { index, (category, _) ->
                Tab(
                    selected = selectedCategory == index,
                    onClick = { selectedCategory = index },
                    text = { Text(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị emoji
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(emojiCategories[selectedCategory].second) { emoji ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(36.dp)
                        .background(Color(0xFF3D4B5D), shape = CircleShape)
                        .clickable { onEmojiSelected(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}