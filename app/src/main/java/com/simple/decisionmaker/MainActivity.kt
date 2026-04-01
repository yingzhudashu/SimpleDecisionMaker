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
     * 使用官方 requestPinAppWidget API (Android 8.0+)
     */
    private fun addWidgetToHomeScreen() {
        val appWidgetManager = getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0+ 使用官方 API
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val provider = ComponentName(this, DecisionWidgetProvider::class.java)
                
                // 调用 requestPinAppWidget，系统会弹出确认对话框
                // 用户确认后会自动添加到桌面
                appWidgetManager.requestPinAppWidget(provider, null, null)
                
                Toast.makeText(this, "✅ 请点击系统对话框确认添加", Toast.LENGTH_LONG).show()
            } else {
                // 设备不支持，显示手动引导
                showManualWidgetGuide()
            }
        } else {
            // Android 7.1 及以下，显示手动引导
            showManualWidgetGuide()
        }
    }
    
    /**
     * 显示手动添加小组件引导
     */
    private fun showManualWidgetGuide() {
        Toast.makeText(
            this, 
            "⚠️ 请长按桌面空白处 → 选择'小组件' → 找到'简单决策器'", 
            Toast.LENGTH_LONG
        ).show()
    }
}
