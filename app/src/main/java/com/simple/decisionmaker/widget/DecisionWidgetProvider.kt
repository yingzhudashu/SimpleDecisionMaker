package com.simple.decisionmaker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.simple.decisionmaker.MainActivity
import com.simple.decisionmaker.R
import com.simple.decisionmaker.data.TemplateManager

/**
 * 简单决策器 - 桌面小组件
 * 功能：快速决策，无需打开 App
 */
class DecisionWidgetProvider : AppWidgetProvider() {
    
    companion object {
        const val ACTION_MAKE_DECISION = "com.simple.decisionmaker.MAKE_DECISION"
        const val ACTION_OPEN_APP = "com.simple.decisionmaker.OPEN_APP"
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_MAKE_DECISION -> {
                // 执行决策
                val appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    // 获取模板并随机选择
                    val templateManager = TemplateManager(context)
                    val templates = templateManager.getAllTemplates()
                    val currentTemplate = templates["今天吃什么"] ?: listOf("火锅", "烧烤", "日料", "中餐", "西餐", "快餐")
                    val result = currentTemplate.random()
                    
                    // 更新小组件显示结果
                    updateWidgetWithResult(context, appWidgetId, result)
                    
                    // 显示 Toast 提示
                    Toast.makeText(context, "✨ 结果是：$result", Toast.LENGTH_LONG).show()
                }
            }
            
            ACTION_OPEN_APP -> {
                // 打开应用
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                context.startActivity(openIntent)
            }
        }
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_decision)
        
        // 设置决策按钮点击事件
        val decisionIntent = Intent(context, DecisionWidgetProvider::class.java).apply {
            action = ACTION_MAKE_DECISION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val decisionPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            decisionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_decision_button, decisionPendingIntent)
        
        // 设置整个小组件点击打开应用
        val openAppIntent = Intent(context, DecisionWidgetProvider::class.java).apply {
            action = ACTION_OPEN_APP
        }
        val openAppPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId + 1000,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_layout, openAppPendingIntent)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    private fun updateWidgetWithResult(
        context: Context,
        appWidgetId: Int,
        result: String
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val views = RemoteViews(context.packageName, R.layout.widget_decision)
        
        // 显示结果
        views.setTextViewText(R.id.widget_result, "✨ $result")
        views.setViewVisibility(R.id.widget_result, android.view.View.VISIBLE)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
