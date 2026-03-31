package com.simple.decisionmaker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 简单决策器主界面
 * 极简设计，只保留核心功能
 */
@Composable
fun SimpleDecisionApp() {
    var options by remember { mutableStateOf(listOf("选项 A", "选项 B", "选项 C")) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isChoosing by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 标题
        Text(
            text = "简单决策器",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // 结果显示
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isChoosing) {
                    Text(
                        text = "正在选择...",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else if (selectedOption != null) {
                    Text(
                        text = selectedOption!!,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Text(
                        text = "点击开始按钮",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 开始按钮
        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                if (!isChoosing) {
                    isChoosing = true
                    selectedOption = null
                    
                    // 模拟选择动画
                    scope.launch {
                        delay(1500)
                        selectedOption = options.random()
                        isChoosing = false
                    }
                }
            },
            enabled = !isChoosing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (isChoosing) "选择中..." else "开始选择",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
