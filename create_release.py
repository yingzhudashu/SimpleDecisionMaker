#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
创建 GitHub Release 并上传 APK
"""

import requests
import os
import json

# 配置
GITHUB_TOKEN = os.environ.get('GITHUB_TOKEN', '')
OWNER = 'yingzhudashu'
REPO = 'SimpleDecisionMaker'
TAG = 'v3.0.9'
RELEASE_NAME = 'v3.0.9 - 导入按钮优化'
APK_PATH = 'app/build/outputs/apk/debug/app-debug.apk'

RELEASE_NOTES = """## 更新内容

### 新增功能
- ✅ 新增导入模板按钮，更方便导入别人分享的模板
- ✅ 优化按钮大小，历史记录和模板按钮大小一致
- ✅ 优化按钮文字，显示 emoji+ 文字说明

### 功能说明
- 📥 导入按钮：点击模板对话框中的'导入'按钮，自动检测剪贴板中的模板分享
- 📜 历史按钮：小巧精致，显示 emoji+ 文字
- 📋 模板按钮：与历史按钮大小一致，显示 emoji+ 文字

### 使用方法
1. 复制别人分享的模板文字
2. 打开 App 点击'模板'
3. 点击'导入'按钮
4. 自动检测并显示模板信息
5. 点击'导入'即可使用

### 技术改进
- 按钮高度：36dp → 30dp
- 字体大小：emoji 16sp/文字 14sp → emoji 14sp/文字 12sp
- 内边距：12dp/4dp → 8dp/2dp

### 下载
- [app-debug.apk](https://github.com/yingzhudashu/SimpleDecisionMaker/releases/download/v3.0.9/app-debug.apk) (8.37 MB)
"""

def get_release_by_tag(tag_name):
    """根据 tag 获取 Release"""
    url = f'https://api.github.com/repos/{OWNER}/{REPO}/releases/tags/{tag_name}'
    
    headers = {
        'Authorization': f'token {GITHUB_TOKEN}',
        'Accept': 'application/vnd.github.v3+json'
    }
    
    response = requests.get(url, headers=headers)
    
    if response.status_code == 200:
        return response.json()
    else:
        return None

def create_release():
    """创建 Release"""
    # 先检查是否已存在
    existing_release = get_release_by_tag(TAG)
    
    if existing_release:
        print(f"[INFO] Release 已存在，使用现有 Release")
        return existing_release
    
    url = f'https://api.github.com/repos/{OWNER}/{REPO}/releases'
    
    headers = {
        'Authorization': f'token {GITHUB_TOKEN}',
        'Accept': 'application/vnd.github.v3+json'
    }
    
    data = {
        'tag_name': TAG,
        'name': RELEASE_NAME,
        'body': RELEASE_NOTES,
        'draft': False,
        'prerelease': False
    }
    
    response = requests.post(url, headers=headers, json=data)
    
    if response.status_code == 201:
        print(f"[OK] Release 创建成功")
        return response.json()
    else:
        print(f"[FAIL] Release 创建失败：{response.status_code}")
        print(response.text)
        return None

def upload_asset(release_id, file_path):
    """上传 APK 到 Release"""
    if not os.path.exists(file_path):
        print(f"❌ 文件不存在：{file_path}")
        return False
    
    file_size = os.path.getsize(file_path)
    file_name = os.path.basename(file_path)
    
    url = f'https://uploads.github.com/repos/{OWNER}/{REPO}/releases/{release_id}/assets?name={file_name}'
    
    headers = {
        'Authorization': f'token {GITHUB_TOKEN}',
        'Accept': 'application/vnd.github.v3+json',
        'Content-Type': 'application/vnd.android.package-archive'
    }
    
    with open(file_path, 'rb') as f:
        response = requests.post(url, headers=headers, data=f)
    
    if response.status_code == 201:
        print(f"[OK] APK 上传成功 ({file_size / 1024 / 1024:.2f} MB)")
        return True
    else:
        print(f"[FAIL] APK 上传失败：{response.status_code}")
        print(response.text)
        return False

if __name__ == '__main__':
    if not GITHUB_TOKEN:
        print("❌ 请设置 GITHUB_TOKEN 环境变量")
        exit(1)
    
    print(f"开始创建 Release: {TAG}")
    
    # 创建 Release
    release = create_release()
    
    if release:
        release_id = release['id']
        print(f"Release ID: {release_id}")
        
        # 上传 APK
        if upload_asset(release_id, APK_PATH):
            # 获取最新的 release 信息（包含上传的 assets）
            updated_release = get_release_by_tag(TAG)
            print(f"\n[OK] Release {TAG} 创建完成")
            if updated_release and updated_release.get('assets'):
                print(f"[APK] 下载地址：{updated_release['assets'][0]['browser_download_url']}")
            else:
                print(f"[APK] 下载地址：https://github.com/{OWNER}/{REPO}/releases/download/{TAG}/{os.path.basename(APK_PATH)}")
        else:
            print(f"\n[FAIL] APK 上传失败")
    else:
        print(f"\n[FAIL] Release 创建失败")
