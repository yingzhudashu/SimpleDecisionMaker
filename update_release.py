#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
更新 GitHub Release 版本号为 v1.0
"""

import requests
import os

GITHUB_TOKEN = os.environ.get('GITHUB_TOKEN', '')
OWNER = 'yingzhudashu'
REPO = 'SimpleDecisionMaker'
OLD_TAG = 'v3.0.9'
NEW_TAG = 'v1.0'
APK_PATH = 'app/build/outputs/apk/debug/app-debug.apk'

RELEASE_NAME = 'v1.0 - 初始版本'
RELEASE_NOTES = """## 更新内容

### 功能特性
- ✅ 随机决策功能
- ✅ 预设模板功能（4 个模板）
- ✅ 权重设置功能
- ✅ 自定义选项功能
- ✅ 模板分享/导入功能
- ✅ 历史记录功能
- ✅ 系统分享功能

### 技术特点
- ✅ 完全离线，无广告
- ✅ Material Design 3 界面
- ✅ Android 8.0+ 支持
- ✅ 简洁易用

### 下载
- [app-debug.apk](https://github.com/yingzhudashu/SimpleDecisionMaker/releases/download/v1.0/app-debug.apk) (8.37 MB)
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

def delete_release(release_id):
    """删除 Release"""
    url = f'https://api.github.com/repos/{OWNER}/{REPO}/releases/{release_id}'
    
    headers = {
        'Authorization': f'token {GITHUB_TOKEN}',
        'Accept': 'application/vnd.github.v3+json'
    }
    
    response = requests.delete(url, headers=headers)
    
    if response.status_code == 204:
        print(f"[OK] 删除旧 Release 成功")
        return True
    else:
        print(f"[FAIL] 删除旧 Release 失败：{response.status_code}")
        return False

def create_release():
    """创建 Release"""
    # 先检查是否已存在
    existing_release = get_release_by_tag(NEW_TAG)
    
    if existing_release:
        print(f"[INFO] Release 已存在，使用现有 Release")
        return existing_release
    
    url = f'https://api.github.com/repos/{OWNER}/{REPO}/releases'
    
    headers = {
        'Authorization': f'token {GITHUB_TOKEN}',
        'Accept': 'application/vnd.github.v3+json'
    }
    
    data = {
        'tag_name': NEW_TAG,
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
        print(f"[FAIL] 文件不存在：{file_path}")
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
        print("[FAIL] 请设置 GITHUB_TOKEN 环境变量")
        exit(1)
    
    print(f"开始更新 Release 到 {NEW_TAG}")
    
    # 删除旧 Release
    old_release = get_release_by_tag(OLD_TAG)
    if old_release:
        print(f"找到旧 Release: {OLD_TAG} (ID: {old_release['id']})")
        delete_release(old_release['id'])
    
    # 创建新 Release
    release = create_release()
    
    if release:
        release_id = release['id']
        print(f"Release ID: {release_id}")
        
        # 上传 APK
        if upload_asset(release_id, APK_PATH):
            # 获取最新的 release 信息（包含上传的 assets）
            updated_release = get_release_by_tag(NEW_TAG)
            print(f"\n[OK] Release {NEW_TAG} 创建完成")
            if updated_release and updated_release.get('assets'):
                print(f"[APK] 下载地址：{updated_release['assets'][0]['browser_download_url']}")
            else:
                print(f"[APK] 下载地址：https://github.com/{OWNER}/{REPO}/releases/download/{NEW_TAG}/{os.path.basename(APK_PATH)}")
        else:
            print(f"\n[FAIL] APK 上传失败")
    else:
        print(f"\n[FAIL] Release 创建失败")
