package com.simple.decisionmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.simple.decisionmaker.ui.DecisionApp
import com.simple.decisionmaker.ui.theme.SimpleDecisionTheme

/**
 * 简单决策器 - 主活动
 * 帮助用户做日常小决定
 */
class MainActivity : ComponentActivity() {
    
    private var showWidgetGuideDialog = mutableStateOf(false)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 启用边到边显示，但正确处理状态栏
        enableEdgeToEdge()
        
        setContent {
            SimpleDecisionTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        DecisionApp(
                            onAddWidgetRequested = { showWidgetGuideDialog.value = true }
                        )
                    }
                }
                
                // 显示引导对话框
                if (showWidgetGuideDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showWidgetGuideDialog.value = false },
                        title = { Text("添加桌面小组件") },
                        text = { Text("请长按桌面空白处 → 选择'小组件' → 找到'简单决策器' → 拖动到桌面") },
                        confirmButton = {
                            TextButton(onClick = { showWidgetGuideDialog.value = false }) {
                                Text("知道了")
                            }
                        }
                    )
                }
            }
        }
    }
}
