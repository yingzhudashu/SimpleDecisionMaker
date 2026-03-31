#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
创建 GitHub Release v1.1 并上传 APK
"""

import requests
import os

GITHUB_TOKEN = os.environ.get('GITHUB_TOKEN', '')
OWNER = 'yingzhudashu'
REPO = 'SimpleDecisionMaker'
TAG = 'v1.1'
APK_FILENAME = 'SimpleDecisionMaker-v1.1.apk'
APK_PATH = 'app/build/outputs/apk/debug/app-debug.apk'

RELEASE_NAME = 'v1.1 - 功能增强版'
RELEASE_NOTES = """## v1.1 vs v1.0 更新内容

### 🎯 新功能

#### 1. 模板持久化存储
- ✅ 自动保存对模板的修改
- ✅ 添加/删除选项自动保存
- ✅ 切换模板时自动加载

#### 2. 模板编辑功能  
- ✅ 右上角编辑按钮
- ✅ 可修改任何模板（包括预设）
- ✅ 修改后自动保存

#### 3. 动态模板显示
- ✅ 显示当前模板名称和选项数量
- ✅ 实时更新（切换/增删选项时）
- ✅ 格式：`模板名称 · X 个选项`

### 🎨 UI 改进

#### 1. 权重按钮统一格式
- ✅ 与历史/模板按钮样式统一
- ✅ 使用 OutlinedButton (30dp 高度)
- ✅ emoji + 文字格式 (⚖️ 权重)

#### 2. 模板名称显示优化
- ✅ 字体调小 (bodySmall)
- ✅ 最多显示 2 行
- ✅ 过长时显示省略号

#### 3. 其他优化
- ✅ 移除版本号显示
- ✅ 界面更简洁

### 📦 技术实现

- 新增 TemplateManager 数据管理器
- 使用 SharedPreferences 持久化存储
- 自动保存机制
- 实时更新 UI

### 📊 APK 信息

- **版本**: v1.1
- **大小**: 8.49 MB
- **编译**: BUILD SUCCESSFUL
- **最低版本**: Android 8.0+

### 📝 使用说明

1. **选择模板**: 点击右上角 📋 模板按钮
2. **编辑模板**: 点击右上角 ⚙️ 编辑按钮
3. **添加选项**: 输入后点击 ➕ 按钮
4. **删除选项**: 点击选项右侧 🗑️ 按钮
5. **调整权重**: 点击 ⚖️ 权重按钮

### ⚠️ 注意事项

- 修改会自动保存到当前模板
- 预设模板也可修改
- 切换模板会加载保存的内容

---

**GitHub**: https://github.com/yingzhudashu/SimpleDecisionMaker
"""

def get_release_by_tag(tag_name):
    """根据 tag 获取 Release"""
    url = f'https://api.github.com/repos/{OWNER}/{REPO}/releases/tags/{tag_name}'
    headers = {'Authorization': f'token {GITHUB_TOKEN}', 'Accept': 'application/vnd.github.v3+json'}
    response = requests.get(url, headers=headers)
    return response.json() if response.status_code == 200 else None

def create_or_update_release():
    """创建或更新 Release"""
    # 先删除旧的 v1.1 release（如果存在）
    existing = get_release_by_tag(TAG)
    if existing:
        print(f"Deleting existing release {TAG}...")
        url = f'https://api.github.com/repos/{OWNER}/{REPO}/releases/{existing["id"]}'
        headers = {'Authorization': f'token {GITHUB_TOKEN}', 'Accept': 'application/vnd.github.v3+json'}
        requests.delete(url, headers=headers)
    
    # 创建新 release
    url = f'https://api.github.com/repos/{OWNER}/{REPO}/releases'
    headers = {'Authorization': f'token {GITHUB_TOKEN}', 'Accept': 'application/vnd.github.v3+json'}
    data = {'tag_name': TAG, 'name': RELEASE_NAME, 'body': RELEASE_NOTES, 'draft': False, 'prerelease': False}
    
    response = requests.post(url, headers=headers, json=data)
    return response.json()

def upload_asset(release_id):
    """上传 APK"""
    if not os.path.exists(APK_PATH):
        print(f"APK not found: {APK_PATH}")
        return False
    
    url = f'https://uploads.github.com/repos/{OWNER}/{REPO}/releases/{release_id}/assets?name={APK_FILENAME}'
    headers = {'Authorization': f'token {GITHUB_TOKEN}', 'Accept': 'application/vnd.github.v3+json', 'Content-Type': 'application/vnd.android.package-archive'}
    
    with open(APK_PATH, 'rb') as f:
        response = requests.post(url, headers=headers, data=f)
    
    if response.status_code == 201:
        print(f"APK uploaded: {response.json()['browser_download_url']}")
        return True
    else:
        print(f"Upload failed: {response.status_code}")
        return False

if __name__ == '__main__':
    print(f"Creating/Updating Release {TAG}...")
    release = create_or_update_release()
    
    if release:
        print(f"Release {'updated' if 'id' in release else 'created'}: {release['html_url']}")
        
        print(f"Uploading APK...")
        if upload_asset(release['id']):
            print("APK uploaded successfully!")
        else:
            print("Failed to upload APK")
    else:
        print("Failed to create/update release")
