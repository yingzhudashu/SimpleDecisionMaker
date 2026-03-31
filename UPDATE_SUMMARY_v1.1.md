# 📦 简单决策器 v1.1 版本更新总结

## 🆚 v1.0 vs v1.1 对比

### v1.0 基础功能
- ✅ 随机选择功能
- ✅ 预设模板（4 个）
- ✅ 权重设置
- ✅ 历史记录

### v1.1 新增功能

#### 1. 模板持久化存储 🎯
- ✅ 自动保存对模板的修改
- ✅ 添加/删除选项自动保存
- ✅ 切换模板时自动加载保存的内容
- ✅ 使用 SharedPreferences 持久化

#### 2. 模板编辑功能 ✏️
- ✅ 右上角编辑按钮
- ✅ 可修改任何模板（包括预设模板）
- ✅ 修改后自动保存
- ✅ 支持批量修改选项

#### 3. 动态模板显示 📊
- ✅ 显示当前模板名称和选项数量
- ✅ 实时更新（切换模板/增删选项时）
- ✅ 显示格式：`模板名称 · X 个选项`
- ✅ 字体优化（bodySmall，最多 2 行）

#### 4. UI 改进 🎨
- ✅ 权重按钮与历史/模板按钮格式统一
  - 使用 OutlinedButton (30dp 高度)
  - emoji + 文字格式 (⚖️ 权重)
  - 字体大小：emoji 14.sp，文字 12.sp
- ✅ 模板名称显示优化
  - 字体调小 (bodySmall)
  - 最多显示 2 行
  - 过长时显示省略号
- ✅ 移除版本号显示

### 技术实现

#### 新增文件
- `TemplateManager.kt` - 模板数据管理器
  - 使用 SharedPreferences 存储
  - 支持模板增删改查
  - 自动保存机制

#### 修改文件
- `DecisionApp.kt` - 主界面
  - 添加动态模板显示
  - 添加模板编辑功能
  - 统一按钮样式
  - 实时更新 UI

## 📊 APK 信息

| 项目 | v1.0 | v1.1 |
|------|------|------|
| 版本 | 1.0 | 1.1 |
| 大小 | 8.4 MB | 8.49 MB |
| 编译 | SUCCESS | SUCCESS |
| 最低版本 | Android 8.0+ | Android 8.0+ |

## 📥 下载地址

- **GitHub Release**: https://github.com/yingzhudashu/SimpleDecisionMaker/releases/tag/v1.1
- **APK 文件**: SimpleDecisionMaker-v1.1.apk

## 📝 使用说明

### 编辑模板
1. 点击右上角 ⚙️ 编辑按钮
2. 修改选项列表（用逗号分隔）
3. 点击保存
4. 修改自动保存

### 添加选项
1. 在输入框输入选项文字
2. 点击 ➕ 按钮
3. 自动保存到当前模板

### 删除选项
1. 点击选项右侧 🗑️ 按钮
2. 自动保存到当前模板

### 调整权重
1. 点击 ⚖️ 权重按钮
2. 使用上下箭头调整
3. 权重越高，被选中概率越大

## ⚠️ 注意事项

- 修改会自动保存到当前模板
- 预设模板也可修改
- 切换模板会加载保存的内容
- 删除选项会立即保存

---

**GitHub**: https://github.com/yingzhudashu/SimpleDecisionMaker  
**Release**: https://github.com/yingzhudashu/SimpleDecisionMaker/releases/tag/v1.1
