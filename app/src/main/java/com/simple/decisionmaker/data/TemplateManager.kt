package com.simple.decisionmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.simple.decisionmaker.ui.OptionItem

/**
 * 模板数据管理器
 * 使用 SharedPreferences 保存用户自定义模板
 */
class TemplateManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "decision_templates", 
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    // 预设模板
    private val defaultTemplates = mapOf(
        "今天吃什么" to listOf("火锅", "烧烤", "日料", "中餐", "西餐", "快餐"),
        "周末去哪玩" to listOf("公园", "电影院", "商场", "博物馆", "咖啡厅", "图书馆"),
        "喝什么" to listOf("奶茶", "咖啡", "果汁", "可乐", "矿泉水", "茶"),
        "晚餐吃什么" to listOf("炒菜", "汤面", "饺子", "粥", "沙拉", "三明治")
    )
    
    /**
     * 获取所有模板（预设 + 自定义）
     */
    fun getAllTemplates(): Map<String, List<String>> {
        val savedTemplatesJson = prefs.getString("custom_templates", null)
        
        return if (savedTemplatesJson != null) {
            // 有保存的模板，合并预设和自定义
            val savedTemplates: Map<String, List<String>> = gson.fromJson(savedTemplatesJson, Map::class.java) as Map<String, List<String>>
            defaultTemplates + savedTemplates
        } else {
            // 没有保存的模板，只返回预设
            defaultTemplates
        }
    }
    
    /**
     * 保存自定义模板
     */
    fun saveTemplate(name: String, options: List<String>) {
        val savedTemplatesJson = prefs.getString("custom_templates", null)
        
        val customTemplates: MutableMap<String, List<String>> = if (savedTemplatesJson != null) {
            val saved: Map<String, List<String>> = gson.fromJson(savedTemplatesJson, Map::class.java) as Map<String, List<String>>
            saved.toMutableMap()
        } else {
            mutableMapOf()
        }
        
        // 保存或更新模板
        customTemplates[name] = options
        
        // 保存到 SharedPreferences
        prefs.edit()
            .putString("custom_templates", gson.toJson(customTemplates))
            .apply()
    }
    
    /**
     * 删除模板（预设模板通过创建空自定义模板来覆盖）
     */
    fun deleteTemplate(name: String) {
        if (isDefaultTemplate(name)) {
            // 预设模板：创建一个空的自定义模板来覆盖
            saveTemplate(name, emptyList())
        } else {
            // 自定义模板：直接删除
            val savedTemplatesJson = prefs.getString("custom_templates", null)
            
            if (savedTemplatesJson != null) {
                val saved: Map<String, Any> = gson.fromJson(savedTemplatesJson, Map::class.java) as Map<String, Any>
                val savedTemplates: MutableMap<String, List<String>> = saved.mapValues { entry ->
                    entry.value as List<String>
                }.toMutableMap()
                savedTemplates.remove(name)
                
                prefs.edit()
                    .putString("custom_templates", gson.toJson(savedTemplates))
                    .apply()
            }
        }
    }
    
    /**
     * 修改模板（包括预设模板）
     */
    fun modifyTemplate(name: String, options: List<String>) {
        saveTemplate(name, options)
    }
    
    /**
     * 检查是否是预设模板
     */
    fun isDefaultTemplate(name: String): Boolean {
        return defaultTemplates.containsKey(name)
    }
    
    /**
     * 获取预设模板
     */
    fun getDefaultTemplates(): Map<String, List<String>> {
        return defaultTemplates
    }
}
