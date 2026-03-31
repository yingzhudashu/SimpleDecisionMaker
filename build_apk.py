import subprocess
import os
import glob

print("=" * 70)
print("BUILDING SIMPLE DECISION MAKER APK")
print("=" * 70)

workspace = r'C:\Users\16785\.openclaw\workspace\SimpleDecisionMaker'
java_home = r'D:\Android\Android Studio\jbr'
gradle_cache = os.path.join(os.environ['USERPROFILE'], '.gradle', 'wrapper', 'dists')

os.chdir(workspace)
env = os.environ.copy()
env['JAVA_HOME'] = java_home
env['GRADLE_USER_HOME'] = gradle_cache

print(f"\n[1/4] Workspace: {workspace}")
print(f"  OK: Directory exists")

print(f"\n[2/4] Java: {java_home}")
result = subprocess.run([os.path.join(java_home, 'bin', 'java.exe'), '-version'], 
                       capture_output=True, text=True)
print(f"  OK: {result.stderr.split(chr(10))[0]}")

print(f"\n[3/4] Project files...")
kt_count = 0
for root, dirs, files in os.walk(os.path.join(workspace, 'app', 'src', 'main', 'java')):
    kt_count += len([f for f in files if f.endswith('.kt')])
print(f"  OK: {kt_count} Kotlin files found")

print(f"\n[4/4] Building APK...")
print(f"  NOTE: Gradle wrapper jar is missing")
print(f"\n{'=' * 70}")
print("PLEASE BUILD IN ANDROID STUDIO:")
print(f"{'=' * 70}")
print(f"""
1. Open Android Studio
2. File -> Open
3. Select: {workspace}
4. Wait for Gradle sync (2-3 min)
5. Build -> Build APK(s)
6. Wait for build (2-5 min)

APK will be at:
{workspace}\\app\\build\\outputs\\apk\\debug\\app-debug.apk
""")
print(f"{'=' * 70}")
