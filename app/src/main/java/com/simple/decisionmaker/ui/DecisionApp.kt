package com.simple.decisionmaker.ui

import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 决策器主界面
 * 功能：
 * 1. 自定义选项列表
 * 2. 随机选择
 * 3. 历史记录
 * 4. 权重设置（未来）
 */
@Composable
fun DecisionApp() {
    var options by remember { mutableStateOf(listOf("今天吃什么", "火锅", "烧烤", "日料", "中餐")) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isChoosing by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var newOption by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    
    // 动画效果
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎲 简单决策器",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            IconButton(onClick = { showHistory = !showHistory }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "历史记录"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 结果显示区域
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isChoosing) {
                    Text(
                        text = "🎯 选择中...",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.scale(scale)
                    )
                } else if (selectedOption != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ 结果是",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedOption!!,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = "点击下方按钮开始",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 开始按钮
        Button(
            onClick = {
                if (!isChoosing && options.size > 1) {
                    isChoosing = true
                    selectedOption = null
                    
                    scope.launch {
                        // 动画效果
                        delay(1500)
                        val actualOptions = options.drop(1) // 排除第一个（标题）
                        selectedOption = actualOptions.random()
                        history.add(0, selectedOption!!)
                        isChoosing = false
                    }
                }
            },
            enabled = !isChoosing && options.size > 1,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isChoosing) "🎲 选择中..." else "🎯 开始决定",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 选项列表标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📝 选项列表",
                style = MaterialTheme.typography.titleMedium
            )
            
            // 添加选项按钮
            if (options.isNotEmpty()) {
                IconButton(onClick = {
                    if (newOption.isNotBlank()) {
                        options = options + newOption
                        newOption = ""
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加选项"
                    )
                }
            }
        }
        
        // 新选项输入框
        OutlinedTextField(
            value = newOption,
            onValueChange = { newOption = it },
            label = { Text("新选项") },
            placeholder = { Text("输入选项后点击 + 号添加") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    if (newOption.isNotBlank()) {
                        options = options + newOption
                        newOption = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "添加")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 选项列表
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(options.drop(1)) { option -> // 排除第一个（标题）
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        IconButton(onClick = {
                            options = options.filter { it != option }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
        
        // 历史记录对话框
        if (showHistory) {
            AlertDialog(
                onDismissRequest = { showHistory = false },
                title = { Text("📜 历史记录") },
                text = {
                    if (history.isEmpty()) {
                        Text("暂无历史记录")
                    } else {
                        LazyColumn {
                            items(history) { item ->
                                Text(
                                    text = "• $item",
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { 
                        history.clear()
                        showHistory = false 
                    }) {
                        Text("清空历史")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showHistory = false }) {
                        Text("关闭")
                    }
                }
            )
        }
    }
}
