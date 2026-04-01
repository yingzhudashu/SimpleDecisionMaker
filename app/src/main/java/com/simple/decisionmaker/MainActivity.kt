package com.simple.decisionmaker

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.simple.decisionmaker.ui.DecisionApp
import com.simple.decisionmaker.ui.theme.SimpleDecisionTheme
import com.simple.decisionmaker.widget.DecisionWidgetProvider

/**
 * 简单决策器 - 主活动
 * 帮助用户做日常小决定
 */
class MainActivity : ComponentActivity() {
    
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
                            onAddWidgetRequested = { addWidgetToHomeScreen() }
                        )
                    }
                }
            }
        }
    }
    
    /**
     * 添加小组件到桌面
     */
    private fun addWidgetToHomeScreen() {
        try {
            val appWidgetManager = getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
            val myProvider = ComponentName(this, DecisionWidgetProvider::class.java)
            
            // 检查是否支持直接添加小组件
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                // 使用 Pin 方式添加小组件（Android 8.0+）
                appWidgetManager.requestPinAppWidget(myProvider, null, null)
                
                // 显示成功提示
                Toast.makeText(this, "✅ 小组件已添加到桌面！", Toast.LENGTH_LONG).show()
            } else {
                // 不支持自动添加，尝试直接发送广播
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                    putExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, myProvider)
                }
                startActivity(intent)
                Toast.makeText(this, "📌 请在桌面选择小组件位置", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            // 如果都失败了，引导用户手动添加
            Toast.makeText(this, "⚠️ 请长按桌面→小组件→选择简单决策器", Toast.LENGTH_LONG).show()
        }
    }
}
