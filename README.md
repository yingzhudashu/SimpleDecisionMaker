# 随机决策器 RandomDecisionMaker

一个简洁实用的随机决策工具，帮助你快速做出选择。

[![Version](https://img.shields.io/badge/version-1.2.2-blue.svg)](https://github.com/yingzhudashu/SimpleDecisionMaker/releases)
[![Release](https://img.shields.io/github/release/yingzhudashu/SimpleDecisionMaker.svg)](https://github.com/yingzhudashu/SimpleDecisionMaker/releases)
[![Android](https://img.shields.io/badge/Android-8.0+-green.svg)](https://www.android.com/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📱 应用简介

随机决策器是一款帮助你快速做决定的小工具。无论是今天吃什么、周末去哪玩，还是其他难以抉择的事情，它都能帮你随机选择一个答案。

## ✨ 核心功能

- 🎲 **随机选择**: 从多个选项中随机选择一个
- 📋 **预设模板**: 内置常用场景模板
- ✏️ **模板编辑**: 可自定义和修改任何模板
- 💾 **自动保存**: 修改自动保存，下次使用自动加载
- ⚖️ **权重设置**: 可为选项设置不同权重
- 📊 **动态显示**: 实时显示模板名称和选项数量

## 🚀 快速开始

### 下载安装

1. 前往 [Releases](https://github.com/yingzhudashu/SimpleDecisionMaker/releases) 页面
2. 下载最新版本的 APK 文件
3. 在手机上安装 APK
4. 打开应用，开始使用

### 使用方法

1. **选择模板**: 点击右上角 📋 模板按钮
2. **编辑模板**: 点击右上角 ⚙️ 编辑按钮
3. **添加选项**: 输入选项文字后点击 ➕ 按钮
4. **删除选项**: 点击选项右侧 🗑️ 按钮
5. **调整权重**: 点击 ⚖️ 权重按钮

## 📝 更新日志

### v1.2.2 (2026-04-01)

#### 新功能
- ✅ 模板名称可修改（编辑按钮）
- ✅ 桌面小组件（110x110dp，一键决策）

#### 布局优化
- ✅ 按钮靠右对齐且垂直对齐
- ✅ 文字和输入框占据左侧空间

### v1.2.1 (2026-04-01)

#### 布局优化
- ✅ 按钮靠右对齐且垂直对齐
- ✅ 文字和输入框占据左侧空间

### v1.1 (2026-03-31)

#### 新功能
- ✅ 模板持久化存储（自动保存修改）
- ✅ 模板编辑功能（右上角编辑按钮）
- ✅ 动态显示模板名称和选项数量
- ✅ 权重按钮 UI 统一

#### UI 改进
- ✅ 权重按钮与历史/模板按钮格式统一
- ✅ 模板名称显示优化（小字体，最多 2 行，溢出省略）
- ✅ 移除版本号显示

#### 技术实现
- 新增 TemplateManager 数据管理器
- 使用 SharedPreferences 持久化存储
- 自动保存机制
- 实时更新 UI

### v1.0 (2026-03-31)

- ✅ 初始版本发布
- ✅ 随机决策功能
- ✅ 预设模板功能
- ✅ 权重设置功能

## 📊 技术栈

- **开发语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架构模式**: MVVM
- **最低版本**: Android 8.0 (API 26)
- **目标版本**: Android 14 (API 34)

## 📸 应用截图

> 待添加

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 📞 联系方式

- **GitHub**: https://github.com/yingzhudashu/SimpleDecisionMaker
- **Issues**: https://github.com/yingzhudashu/SimpleDecisionMaker/issues

---

**感谢使用！** 🎉
