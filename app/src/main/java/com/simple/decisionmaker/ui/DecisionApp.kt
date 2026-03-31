package com.simple.decisionmaker.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 顶层数据类，避免嵌套类引用问题
data class OptionItem(val text: String, val weight: Int = 1)
data class HistoryItem(val scenario: String, val result: String, val timestamp: Long = System.currentTimeMillis())

/**
 * 决策器主界面 v3.0.2 - Bug 修复版
 */
@Composable
fun DecisionApp(darkTheme: Boolean = false, onThemeChange: (Boolean) -> Unit = {}) {
    var options by remember { mutableStateOf(listOf(
        OptionItem("火锅", 2), OptionItem("烧烤", 1), OptionItem("日料", 1), OptionItem("中餐", 1)
    )) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isChoosing by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var newOption by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<HistoryItem>() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = EaseInOut), repeatMode = RepeatMode.Reverse))
    
    fun saveHistory(scenario: String, result: String) { history.add(0, HistoryItem(scenario, result)) }
    
    fun shareResult(result: String) {
        scope.launch {
            android.widget.Toast.makeText(context, "🎲 简单决策器\n我的选择是：$result", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    fun weightedRandom(options: List<OptionItem>): String? {
        if (options.isEmpty()) return null
        val totalWeight = options.sumOf { it.weight }
        if (totalWeight == 0) return options.randomOrNull()?.text
        var random = (1..totalWeight).random()
        for (option in options) { random -= option.weight; if (random <= 0) return option.text }
        return options.lastOrNull()?.text
    }
    
    fun addOption(text: String) { if (text.isNotBlank()) options = options + OptionItem(text, 1) }
    fun removeOption(index: Int) { if (index in options.indices) options = options.toMutableList().apply { removeAt(index) } }
    fun updateWeight(index: Int, newWeight: Int) { if (index in options.indices) options = options.toMutableList().apply { set(index, get(index).copy(weight = newWeight)) } }
    fun applyTemplate(template: List<String>) { options = template.map { OptionItem(it, 1) } }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(if (darkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally) {
        // 标题栏
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🎲 简单决策器 v3.0.2", style = MaterialTheme.typography.headlineSmall, color = if (darkTheme) Color.White else MaterialTheme.colorScheme.primary)
            Row {
                IconButton(onClick = { showSettingsDialog = true }) { Icon(Icons.Default.Settings, "设置", tint = if (darkTheme) Color.White else Color.Unspecified) }
                IconButton(onClick = { showTemplateDialog = true }) { Icon(Icons.Default.AddCircle, "模板", tint = if (darkTheme) Color.White else Color.Unspecified) }
                IconButton(onClick = { showHistory = !showHistory }) { Icon(Icons.Default.List, "历史", tint = if (darkTheme) Color.White else Color.Unspecified) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // 结果显示
        Card(modifier = Modifier.fillMaxWidth().height(200.dp), colors = CardDefaults.cardColors(containerColor = if (darkTheme) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.primaryContainer)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (isChoosing) Text("🎯 选择中...", style = MaterialTheme.typography.titleLarge, modifier = Modifier.scale(scale))
                else if (selectedOption != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✨ 结果是", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(selectedOption!!, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { shareResult(selectedOption!!) }) { Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("分享") }
                    }
                } else Text("点击下方按钮开始", style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // 开始按钮
        Button(onClick = { if (!isChoosing && options.size > 1) { isChoosing = true; selectedOption = null; scope.launch { delay(1500); selectedOption = weightedRandom(options); if (selectedOption != null) saveHistory("自定义", selectedOption!!); isChoosing = false } } },
            enabled = !isChoosing && options.size > 1, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text(text = if (isChoosing) "🎲 选择中..." else "🎯 开始决定", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // 选项管理
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("📝 选项列表（带权重）", style = MaterialTheme.typography.titleMedium)
            Row {
                OutlinedButton(onClick = { showWeightDialog = true }) { Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("权重") }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { addOption(newOption); newOption = "" }) { Icon(Icons.Default.Add, "添加") }
            }
        }
        OutlinedTextField(value = newOption, onValueChange = { newOption = it }, label = { Text("新选项") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { addOption(newOption); newOption = "" }))
        Spacer(modifier = Modifier.height(8.dp))
        
        // 选项列表
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(options, key = { it.text }) { option ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(option.text); if (option.weight > 1) Badge { Text("权重 x${option.weight}") }
                        }
                        IconButton(onClick = { removeOption(options.indexOf(option)) }) { Icon(Icons.Default.Delete, "删除", tint = if (darkTheme) Color.Red else MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
        
        // 对话框
        if (showSettingsDialog) AlertDialog(onDismissRequest = { showSettingsDialog = false }, title = { Text("⚙️ 设置") }, text = { Column { Text("版本：3.0.2"); Text("作者：Robot Claw"); Spacer(modifier = Modifier.height(8.dp)); Text("功能：\n- 权重设置\n- 历史记录\n- 预设模板\n- 分享功能") } }, confirmButton = { TextButton(onClick = { showSettingsDialog = false }) { Text("关闭") } })
        if (showWeightDialog) WeightDialog(options = options, onWeightChange = { index, weight -> updateWeight(index, weight) }, onDismiss = { showWeightDialog = false })
        if (showHistory) HistoryDialog(history = history.toList(), onClear = { history.clear() }, onDismiss = { showHistory = false })
        if (showTemplateDialog) TemplateDialog(onTemplateSelected = { template -> applyTemplate(template); showTemplateDialog = false }, onDismiss = { showTemplateDialog = false })
    }
}

@Composable fun WeightDialog(options: List<OptionItem>, onWeightChange: (Int, Int) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("⚖️ 设置权重") }, text = { Column { Text("权重越高，被选中的概率越大", modifier = Modifier.padding(bottom = 16.dp)); LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) { items(options, key = { it.text }) { option -> Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(option.text, modifier = Modifier.weight(1f)); Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { IconButton(onClick = { if (option.weight > 1) onWeightChange(options.indexOf(option), option.weight - 1) }, enabled = option.weight > 1) { Icon(Icons.Default.KeyboardArrowDown, null) }; Text("x${option.weight}", modifier = Modifier.width(30.dp), textAlign = TextAlign.Center); IconButton(onClick = { onWeightChange(options.indexOf(option), option.weight + 1) }) { Icon(Icons.Default.KeyboardArrowUp, null) } } } } } } }, confirmButton = { TextButton(onClick = onDismiss) { Text("完成") } })
}

@Composable fun HistoryDialog(history: List<HistoryItem>, onClear: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📜 历史记录") },
        text = {
            if (history.isEmpty()) Text("暂无历史记录")
            else LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(history, key = { it.timestamp }) { item ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("• ${item.result}")
                        Text("  场景：${item.scenario}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("  时间：${java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp))}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onClear(); onDismiss() }) { Text("清空历史") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("关闭") } }
    )
}

@Composable fun TemplateDialog(onTemplateSelected: (List<String>) -> Unit, onDismiss: () -> Unit) {
    val templates = mapOf(
        "今天吃什么" to listOf("火锅", "烧烤", "日料", "中餐", "西餐", "快餐"),
        "周末去哪玩" to listOf("公园", "电影院", "商场", "博物馆", "咖啡厅", "图书馆"),
        "喝什么" to listOf("奶茶", "咖啡", "果汁", "可乐", "矿泉水", "茶"),
        "晚餐吃什么" to listOf("炒菜", "汤面", "饺子", "粥", "沙拉", "三明治")
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📋 选择预设模板") },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(templates.entries.toList(), key = { it.key }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), onClick = { onTemplateSelected(entry.value) }) {
                        Text(entry.key, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
