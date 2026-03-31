# 🎲 简单决策器 - Simple Decision Maker

**告别选择困难，让随机帮你做决定！**

[![Version](https://img.shields.io/badge/version-1.0-blue.svg)](https://github.com/yingzhudashu/SimpleDecisionMaker/releases)
[![Android](https://img.shields.io/badge/Android-8.0+-green.svg)](https://www.android.com/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![GitHub Release](https://img.shields.io/github/v/release/yingzhudashu/SimpleDecisionMaker)](https://github.com/yingzhudashu/SimpleDecisionMaker/releases)

---

## 📱 应用简介

**简单决策器** 是一款帮助你快速做决定的 Android 小工具。

**适用场景**:
- 🍽️ 今天吃什么？
- 🎬 周末去哪玩？
- 🥤 喝什么饮料？
- ❓ 任何难以抉择的事情

**核心特点**:
- ✅ 完全免费，无广告
- ✅ 完全离线，不耗流量
- ✅ 开箱即用，简单易用
- ✅ 自定义选项，灵活自由
- ✅ 模板分享，好友共用

---

## 🎯 功能特性

### 基础功能
- **随机选择**: 从多个选项中随机选择一个
- **动画效果**: 1.5 秒选择动画，增加仪式感
- **结果分享**: 一键分享到微信/QQ 等应用

### 高级功能
- **预设模板**: 内置 4 个常用模板，开箱即用
- **自定义选项**: 自由添加/删除选项
- **权重设置**: 为每个选项设置不同权重
- **模板分享**: 分享模板给好友使用
- **模板导入**: 导入好友分享的模板
- **历史记录**: 查看所有历史决策记录

---

## 🚀 快速开始

### 下载安装

1. **下载 APK**
   - 前往 [Releases](https://github.com/yingzhudashu/SimpleDecisionMaker/releases) 页面
   - 下载最新版本的 APK 文件

2. **安装应用**
   - 在手机上打开下载的 APK 文件
   - 允许"安装未知来源"（如果需要）
   - 点击"安装"

3. **开始使用**
   - 打开应用
   - 点击"开始决定"按钮
   - 等待随机结果

### 使用示例

#### 场景 1: 今天吃什么

1. 打开应用，默认显示"今天吃什么"模板
2. 点击"开始决定"
3. 等待 1.5 秒
4. 显示结果：比如"火锅"

#### 场景 2: 自定义选项

1. 在"新选项"输入框输入"麻辣烫"
2. 点击"+"按钮添加
3. 点击"开始决定"
4. 可能选到"麻辣烫"

#### 场景 3: 分享模板

1. 点击右上角"📋 模板"
2. 找到"今天吃什么"模板
3. 点击右边的"↗️"分享图标
4. 选择微信/QQ 发送给好友

#### 场景 4: 导入模板

1. 复制好友分享的模板文字
2. 打开应用点击"📋 模板"
3. 点击"📥 导入"按钮
4. 点击"导入"即可使用

---

## 📸 应用截图

> 截图待添加

---

## 📋 使用说明

详细使用说明请查看：[用户使用说明](USER_GUIDE.md)

### 快速上手

1. **基本使用**: 点击"开始决定" → 等待结果
2. **使用模板**: 点击"模板" → 选择模板 → 开始决定
3. **自定义**: 输入选项 → 点击"+" → 开始决定
4. **设置权重**: 点击"权重" → 调整权重 → 完成
5. **分享模板**: 点击模板右边"↗️" → 选择应用
6. **导入模板**: 复制分享文字 → 点击"导入" → 导入
7. **查看历史**: 点击"历史" → 查看记录

---

## 📝 更新日志

### v1.0 (2026-03-31)
- ✅ 初始版本发布
- ✅ 随机决策功能
- ✅ 预设模板功能（4 个模板）
- ✅ 权重设置功能
- ✅ 自定义选项功能
- ✅ 模板分享/导入功能
- ✅ 历史记录功能
- ✅ 系统分享功能
- ✅ 完全离线，无广告

---

## 🛠️ 技术栈

- **开发语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架构模式**: MVVM
- **最低版本**: Android 8.0 (API 26)
- **目标版本**: Android 14 (API 34)

---

## 📦 构建说明

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高
- JDK 17 或更高
- Android SDK 34

### 编译步骤
```bash
# 克隆项目
git clone https://github.com/yingzhudashu/SimpleDecisionMaker.git
cd SimpleDecisionMaker

# 编译 Debug 版本
./gradlew assembleDebug

# APK 输出位置
app/build/outputs/apk/debug/app-debug.apk
```

---

## ❓ 常见问题

### Q: 应用需要联网吗？
**A**: 不需要，应用完全离线运行，不消耗流量。

### Q: 支持什么版本的 Android？
**A**: 支持 Android 8.0 及以上版本。

### Q: 如何删除自定义模板？
**A**: 目前版本暂不支持删除，将在后续版本更新。

### Q: 权重如何设置？
**A**: 点击"⚖️ 权重"按钮，使用上下箭头调整，点击"完成"保存。

更多问题请查看：[用户使用说明](USER_GUIDE.md#常见问题)

---

## 📞 反馈与支持

如果你有任何问题或建议，欢迎通过以下方式反馈：

- **GitHub Issues**: https://github.com/yingzhudashu/SimpleDecisionMaker/issues
- **邮箱**: [待添加]

---

## 📄 开源协议

本项目采用 **MIT License** 开源协议。

详见：[LICENSE](LICENSE)

---

## 👨‍💻 作者

- **GitHub**: [@yingzhudashu](https://github.com/yingzhudashu)

---

## ⭐ Star History

如果这个应用对你有帮助，请给个 Star 支持一下！

---

**感谢使用简单决策器！** 🎉

**告别选择困难，让随机帮你做决定！**
