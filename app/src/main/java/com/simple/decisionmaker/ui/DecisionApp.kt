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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.simple.decisionmaker.data.TemplateManager

// 顶层数据类，避免嵌套类引用问题
data class OptionItem(val text: String, val weight: Int = 1)
data class HistoryItem(
    val scenario: String, 
    val result: String, 
    val timestamp: Long = System.currentTimeMillis(),
    val optionsCount: Int = 0  // 记录当时有多少选项
)

/**
 * 决策器主界面 v3.0.2 - Bug 修复版
 */
@Composable
fun DecisionApp(darkTheme: Boolean = false, onThemeChange: (Boolean) -> Unit = {}) {
    // 默认显示"今天吃什么"模板的 6 个选项
    var options by remember { mutableStateOf(listOf(
        OptionItem("火锅", 1), OptionItem("烧烤", 1), OptionItem("日料", 1), 
        OptionItem("中餐", 1), OptionItem("西餐", 1), OptionItem("快餐", 1)
    )) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isChoosing by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showAddTemplateDialog by remember { mutableStateOf(false) }
    var newTemplateName by remember { mutableStateOf("") }
    var newTemplateOptions by remember { mutableStateOf("") }
    var showPasteTemplateDialog by remember { mutableStateOf(false) }
    var clipboardTemplateName by remember { mutableStateOf("") }
    var clipboardTemplateOptions by remember { mutableStateOf("") }
    var currentTemplateName by remember { mutableStateOf("今天吃什么") }
    var currentOptionsCount by remember { mutableStateOf(6) }

    var newOption by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<HistoryItem>() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val templateManager = remember { TemplateManager(context) }
    var allTemplates by remember { mutableStateOf(templateManager.getAllTemplates()) }
    
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = EaseInOut), repeatMode = RepeatMode.Reverse))
    
    fun saveHistory(scenario: String, result: String) { 
        history.add(0, HistoryItem(scenario, result, System.currentTimeMillis(), options.size)) 
    }
    
    fun shareResult(result: String) {
        // 创建分享 Intent，使用 Android 系统分享功能
        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, "🎲 我在用【简单决策器】做决定\n我的选择是：【$result】\n\n你也来试试吧！")
            putExtra(android.content.Intent.EXTRA_SUBJECT, "简单决策器 - 我的选择")
        }
        // 启动系统分享选择器
        context.startActivity(android.content.Intent.createChooser(shareIntent, "分享到"))
    }
    
    // 检查剪贴板是否有模板格式的内容
    fun checkClipboardForTemplate() {
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text.toString()
            // 检查是否是模板格式：📋 分享一个决策模板：【XXX】\n\n选项：XXX，XXX，XXX
            val templateRegex = Regex("📋 分享一个决策模板：【(.+?)】\\n\\n选项：(.+)")
            val match = templateRegex.find(text)
            if (match != null) {
                clipboardTemplateName = match.groupValues[1]
                clipboardTemplateOptions = match.groupValues[2]
                showPasteTemplateDialog = true
            }
        }
    }
    
    // 从剪贴板导入模板
    fun importTemplateFromClipboard() {
        val optionList = clipboardTemplateOptions.split("，").map { it.trim() }.filter { it.isNotBlank() }
        if (optionList.isNotEmpty()) {
            val newOptions = optionList.map { OptionItem(it, 1) }
            options = newOptions
        }
        showPasteTemplateDialog = false
    }
    
    fun weightedRandom(options: List<OptionItem>): String? {
        if (options.isEmpty()) return null
        val totalWeight = options.sumOf { it.weight }
        if (totalWeight == 0) return options.randomOrNull()?.text
        var random = (1..totalWeight).random()
        for (option in options) { random -= option.weight; if (random <= 0) return option.text }
        return options.lastOrNull()?.text
    }
    
    fun saveCurrentOptionsToTemplate(templateName: String) {
        val optionTexts = options.map { it.text }
        templateManager.modifyTemplate(templateName, optionTexts)
        allTemplates = templateManager.getAllTemplates()
    }
    
    fun addOption(text: String) { 
        if (text.isNotBlank()) {
            options = options + OptionItem(text, 1)
            currentOptionsCount = options.size
            // 自动保存到当前模板
            saveCurrentOptionsToTemplate(currentTemplateName)
        }
    }
    fun removeOption(index: Int) { 
        if (index in options.indices) {
            options = options.toMutableList().apply { removeAt(index) }
            currentOptionsCount = options.size
            // 自动保存到当前模板
            saveCurrentOptionsToTemplate(currentTemplateName)
        }
    }
    fun updateWeight(index: Int, newWeight: Int) { if (index in options.indices) options = options.toMutableList().apply { set(index, get(index).copy(weight = newWeight)) } }
    fun applyTemplate(template: List<String>, templateName: String = "今天吃什么") { 
        options = template.map { OptionItem(it, 1) }
        currentTemplateName = templateName
        currentOptionsCount = template.size
    }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(if (darkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally) {
        // 标题栏
        Column(modifier = Modifier.fillMaxWidth()) {
            // 第一行：标题和按钮
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎲 简单决策器", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Row {
                    // 历史记录按钮 - 小巧精致
                    OutlinedButton(
                        onClick = { showHistory = !showHistory }, 
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📜", fontSize = 14.sp)
                            Text("历史", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // 模板按钮 - 与历史按钮一致
                    OutlinedButton(
                        onClick = { showTemplateDialog = true }, 
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📋", fontSize = 14.sp)
                            Text("模板", fontSize = 12.sp)
                        }
                    }
                }
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
        Button(onClick = { if (!isChoosing && options.size > 1) { isChoosing = true; selectedOption = null; scope.launch { delay(1500); selectedOption = weightedRandom(options); if (selectedOption != null) saveHistory(currentTemplateName, selectedOption!!); isChoosing = false } } },
            enabled = !isChoosing && options.size > 1, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text(text = if (isChoosing) "🎲 选择中..." else "🎯 开始决定", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // 选项管理 - 两行布局，按钮靠右且垂直对齐
        Column(modifier = Modifier.fillMaxWidth()) {
            // 第一行：文字区域（左）+ 权重按钮（右）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 文字区域（占据剩余空间）
                Text(
                    text = "$currentTemplateName · $currentOptionsCount 个选项",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // 权重按钮（靠右）
                OutlinedButton(
                    onClick = { showWeightDialog = true },
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚖️", fontSize = 14.sp)
                        Text("权重", fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 第二行：输入框（左）+ 添加按钮（右）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 输入框（占据剩余空间）
                OutlinedTextField(
                    value = newOption,
                    onValueChange = { newOption = it },
                    label = { Text("新选项") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addOption(newOption); newOption = "" })
                )
                Spacer(modifier = Modifier.width(4.dp))
                // 添加按钮（靠右，与权重按钮垂直对齐）
                IconButton(onClick = { addOption(newOption); newOption = "" }) {
                    Icon(Icons.Default.Add, "添加")
                }
            }
        }
        
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
        // 设置对话框已移除，信息直接显示在标题下方
        if (showWeightDialog) WeightDialog(options = options, onWeightChange = { index, weight -> updateWeight(index, weight) }, onDismiss = { showWeightDialog = false })
        if (showHistory) HistoryDialog(history = history.toList(), onClear = { history.clear() }, onDismiss = { showHistory = false })
        // 分享模板功能
        fun shareTemplate(name: String, options: List<String>) {
            val optionsText = options.joinToString("，")
            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, "📋 分享一个决策模板：【$name】\n\n选项：$optionsText\n\n用【简单决策器】创建，你也来试试吧！")
                putExtra(android.content.Intent.EXTRA_SUBJECT, "决策模板：$name")
            }
            context.startActivity(android.content.Intent.createChooser(shareIntent, "分享到"))
        }
        
            // 粘贴导入对话框
        if (showPasteTemplateDialog) AlertDialog(
            onDismissRequest = { showPasteTemplateDialog = false },
            title = { Text("📋 导入模板") },
            text = {
                Column {
                    Text("从剪贴板导入模板：")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("【$clipboardTemplateName】", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("选项：$clipboardTemplateOptions", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("提示：如果没有检测到剪贴板内容，请先复制别人分享的模板文字。", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = { importTemplateFromClipboard() }) {
                    Text("导入")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasteTemplateDialog = false }) {
                    Text("取消")
                }
            }
        )
        
        if (showTemplateDialog) TemplateDialog(
            templates = allTemplates,
            isDefaultTemplate = { name -> templateManager.isDefaultTemplate(name) },
            onTemplateSelected = { templateName: String, template: List<String> -> applyTemplate(template, templateName); showTemplateDialog = false },
            onAddNewTemplate = { showAddTemplateDialog = true },
            onImportTemplate = { checkClipboardForTemplate() },
            onShareTemplate = { name: String, options: List<String> -> shareTemplate(name, options) },
            onTemplateRenamed = { oldName: String, newName: String -> 
                // 重命名模板：先复制旧模板内容，保存为新名称，然后删除旧模板
                val oldTemplate = allTemplates[oldName]
                if (oldTemplate != null) {
                    templateManager.modifyTemplate(newName, oldTemplate)
                    templateManager.deleteTemplate(oldName)
                    allTemplates = templateManager.getAllTemplates()
                }
            },
            onTemplateDeleted = { templateName: String -> templateManager.deleteTemplate(templateName); allTemplates = templateManager.getAllTemplates() },
            onDismiss = { showTemplateDialog = false }
        )
        if (showAddTemplateDialog) AddTemplateDialog(
            name = newTemplateName,
            onNameChange = { newTemplateName = it },
            options = newTemplateOptions,
            onOptionsChange = { newTemplateOptions = it },
            onSave = { 
                // 保存到持久化存储（包括修改预设模板）
                val optionList = newTemplateOptions.split("，").map { it.trim() }.filter { it.isNotBlank() }
                if (optionList.isNotEmpty()) {
                    templateManager.modifyTemplate(newTemplateName, optionList)
                    allTemplates = templateManager.getAllTemplates()
                }
                newTemplateName = ""
                newTemplateOptions = ""
                showAddTemplateDialog = false
            },
            onDismiss = { 
                newTemplateName = ""
                newTemplateOptions = ""
                showAddTemplateDialog = false
            }
        )
    }
}

@Composable fun WeightDialog(options: List<OptionItem>, onWeightChange: (Int, Int) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("⚖️ 设置权重") }, text = { Column { Text("权重越高，被选中的概率越大", modifier = Modifier.padding(bottom = 16.dp)); LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) { items(options, key = { it.text }) { option -> Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(option.text, modifier = Modifier.weight(1f)); Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { IconButton(onClick = { if (option.weight > 1) onWeightChange(options.indexOf(option), option.weight - 1) }, enabled = option.weight > 1) { Icon(Icons.Default.KeyboardArrowDown, null) }; Text("x${option.weight}", modifier = Modifier.width(30.dp), textAlign = TextAlign.Center); IconButton(onClick = { onWeightChange(options.indexOf(option), option.weight + 1) }) { Icon(Icons.Default.KeyboardArrowUp, null) } } } } } } }, confirmButton = { TextButton(onClick = onDismiss) { Text("完成") } })
}

@Composable fun HistoryDialog(history: List<HistoryItem>, onClear: () -> Unit, onDismiss: () -> Unit) {
    val dateFormat = remember { java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault()) }
    
    // 统计信息
    val totalDecisions = history.size
    val mostChosenResult = history.groupBy { it.result }.maxByOrNull { it.value.size }?.key ?: "无"
    val favoriteScenario = history.groupBy { it.scenario }.maxByOrNull { it.value.size }?.key ?: "无"
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Column {
                Text("📜 历史记录")
                if (totalDecisions > 0) {
                    Text("共 $totalDecisions 次决策", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        },
        text = {
            if (history.isEmpty()) {
                Text("暂无历史记录\n开始你的第一次决策吧！")
            } else {
                // 统计卡片
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("📊 统计", style = MaterialTheme.typography.titleSmall)
                        Text("最常选：$mostChosenResult", style = MaterialTheme.typography.bodySmall)
                        Text("常用场景：$favoriteScenario", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                // 历史列表
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(history, key = { it.timestamp }) { item ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("• ${item.result}", style = MaterialTheme.typography.bodyLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                    if (item.optionsCount > 0) {
                                        Text("${item.optionsCount}选 1", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                                Text("场景：${item.scenario}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("时间：${dateFormat.format(java.util.Date(item.timestamp))}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { 
            if (history.isNotEmpty()) {
                TextButton(onClick = { onClear(); onDismiss() }) { 
                    Text("清空历史", color = MaterialTheme.colorScheme.error)
                } 
            } else {
                TextButton(onClick = onDismiss) { Text("关闭") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("关闭") } }
    )
}

@Composable fun TemplateDialog(
    templates: Map<String, List<String>>,
    isDefaultTemplate: (String) -> Boolean = { false },
    onTemplateSelected: (String, List<String>) -> Unit,
    onAddNewTemplate: () -> Unit,
    onImportTemplate: () -> Unit,
    onShareTemplate: (String, List<String>) -> Unit,
    onTemplateRenamed: (String, String) -> Unit,  // 新增：模板重命名回调
    onTemplateDeleted: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var templateToRename by remember { mutableStateOf<String?>(null) }
    var newName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📋 选择模板") },
        text = {
            Column {
                // 第一行：创建和导入按钮
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // 新建模板按钮
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = onAddNewTemplate,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Text("创建", style = MaterialTheme.typography.bodySmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                    // 导入模板按钮
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = onImportTemplate,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📥", fontSize = 16.sp)
                            Text("导入", style = MaterialTheme.typography.bodySmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 预设模板列表
                Text("预设模板", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(templates.entries.toList(), key = { it.key }) { entry ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            onClick = { onTemplateSelected(entry.key, entry.value) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(entry.key, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text("${entry.value.size}个选项", style = MaterialTheme.typography.bodySmall, color = if (isDefaultTemplate(entry.key)) Color.Gray else MaterialTheme.colorScheme.primary)
                                }
                                // 分享按钮
                                IconButton(onClick = { onShareTemplate(entry.key, entry.value) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Share, "分享", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                                
                                // 编辑名称按钮（只有非预设模板可以编辑）
                                if (!isDefaultTemplate(entry.key)) {
                                    IconButton(onClick = { 
                                        templateToRename = entry.key
                                        newName = entry.key
                                        showRenameDialog = true
                                    }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.Edit, "编辑名称", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                    }
                                }
                                
                                // 所有模板都显示删除按钮（包括预设模板）
                                IconButton(onClick = { onTemplateDeleted(entry.key) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
    
    // 重命名对话框
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("✏️ 修改模板名称") },
            text = {
                Column {
                    Text("请输入新的模板名称：")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("模板名称") },
                        placeholder = { Text("如：午餐吃什么") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank() && templateToRename != null) {
                            onTemplateRenamed(templateToRename!!, newName)
                        }
                        showRenameDialog = false
                    },
                    enabled = newName.isNotBlank()
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable fun AddTemplateDialog(
    name: String,
    onNameChange: (String) -> Unit,
    options: String,
    onOptionsChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("➕ 创建新模板") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("模板名称") },
                    placeholder = { Text("如：午餐吃什么") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = options,
                    onValueChange = onOptionsChange,
                    label = { Text("选项列表") },
                    placeholder = { Text("用逗号分隔，如：火锅，烧烤，日料") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Text("提示：用逗号分隔每个选项", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = name.isNotBlank() && options.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
