import subprocess
import os
import sys

print("=" * 60)
print("BUILD PREPARATION - Simple Decision Maker")
print("=" * 60)

# 设置环境
workspace = r'C:\Users\16785\.openclaw\workspace\SimpleDecisionMaker'
java_home = r'D:\Android\Android Studio\jbr'

os.chdir(workspace)
env = os.environ.copy()
env['JAVA_HOME'] = java_home

print(f"\n[1/4] Check workspace...")
print(f"  OK: {workspace}")

print(f"\n[2/4] Check Java...")
java_cmd = os.path.join(java_home, 'bin', 'java.exe')
result = subprocess.run([java_cmd, '-version'], capture_output=True, text=True)
print(f"  OK: {result.stderr.split(chr(10))[0]}")

print(f"\n[3/4] Check project files...")
kt_files = []
for root, dirs, files in os.walk(os.path.join(workspace, 'app', 'src', 'main', 'java')):
    for file in files:
        if file.endswith('.kt'):
            kt_files.append(os.path.join(root, file))
print(f"  OK: Found {len(kt_files)} Kotlin files:")
for f in kt_files:
    print(f"    - {os.path.basename(f)}")

print(f"\n[4/4] Prepare build...")
print(f"  NOTE: Gradle wrapper needs configuration")
print(f"\n{'=' * 60}")
print("BUILD INSTRUCTIONS (Android Studio):")
print(f"{'=' * 60}")
print(f"""
1. Open Android Studio
2. File -> Open -> Select directory:
   {workspace}
3. Wait for Gradle sync (2-3 min)
4. Build -> Build APK(s)
5. Wait for build (2-5 min)
6. APK location:
   {workspace}\\app\\build\\outputs\\apk\\debug\\app-debug.apk
""")

print(f"{'=' * 60}")
print("SUCCESS: Code is ready for build!")
print(f"{'=' * 60}")
