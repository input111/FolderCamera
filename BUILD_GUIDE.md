# 文件夹相机 - 构建指南

## 环境配置要求

### 1. Java开发环境
- **JDK版本**: JDK 11 或更高版本
- **推荐**: Oracle JDK 11 或 OpenJDK 11

#### Windows环境配置Java
1. 下载并安装JDK 11+
2. 设置环境变量：
   ```cmd
   # 设置JAVA_HOME
   set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
   
   # 添加到PATH
   set PATH=%JAVA_HOME%\bin;%PATH%
   ```

3. 验证安装：
   ```cmd
   java -version
   javac -version
   ```

### 2. Android开发环境
- **Android Studio**: Arctic Fox (2020.3.1) 或更高版本
- **Android SDK**: API Level 36
- **Build Tools**: 30.0.3 或更高版本

## 构建步骤

### 方法一：使用Android Studio（推荐）
1. 打开Android Studio
2. 选择 "Open an existing Android Studio project"
3. 导航到项目根目录并选择
4. 等待Gradle同步完成
5. 点击 "Build" → "Make Project" 或使用快捷键 Ctrl+F9
6. 运行项目：点击绿色播放按钮或使用 Shift+F10

### 方法二：命令行构建
1. 确保Java环境已正确配置
2. 在项目根目录打开终端
3. 执行构建命令：

#### Windows PowerShell
```powershell
# 构建Debug版本
.\gradlew.bat assembleDebug

# 构建Release版本
.\gradlew.bat assembleRelease

# 安装到设备
.\gradlew.bat installDebug
```

#### Linux/macOS
```bash
# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease

# 安装到设备
./gradlew installDebug
```

## 常见问题解决

### 1. JAVA_HOME未设置错误
**错误信息**: `ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH`

**解决方案**:
- Windows: 按照上述Java环境配置步骤设置JAVA_HOME
- 或者在Android Studio中使用内置JDK

### 2. SDK版本问题
**错误信息**: `compileSdk` 版本相关错误

**解决方案**:
- 确保Android SDK Manager中已安装API Level 36
- 更新Android SDK Build-Tools到最新版本

### 3. 依赖下载失败
**错误信息**: 网络连接或依赖下载错误

**解决方案**:
- 检查网络连接
- 配置代理（如果需要）
- 清理并重新构建：`./gradlew clean build`

### 4. 权限问题
**错误信息**: gradlew权限被拒绝

**解决方案**:
```bash
# Linux/macOS
chmod +x gradlew
```

## 项目结构验证

构建成功后，应该生成以下文件：
```
app/build/outputs/apk/debug/
├── app-debug.apk          # Debug版本APK
└── output-metadata.json   # 构建元数据

app/build/outputs/apk/release/
├── app-release-unsigned.apk  # Release版本APK（未签名）
└── output-metadata.json      # 构建元数据
```

## 安装和测试

### 1. 通过Android Studio
- 连接Android设备或启动模拟器
- 点击运行按钮直接安装并启动应用

### 2. 通过ADB命令
```bash
# 安装APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.example.foldercamera1/.MainActivity
```

### 3. 手动安装
- 将APK文件传输到Android设备
- 在设备上启用"未知来源"安装
- 点击APK文件进行安装

## 功能测试清单

安装完成后，请验证以下功能：

### 主界面测试
- [ ] 相机预览正常显示
- [ ] 拍照按钮响应正常
- [ ] 快门音效播放
- [ ] 照片自动保存
- [ ] 设置按钮可点击

### 设置界面测试
- [ ] 文件夹列表正常显示
- [ ] 新建文件夹功能
- [ ] 文件夹选择功能
- [ ] 文件夹删除功能
- [ ] 返回主界面正常

### 权限测试
- [ ] 相机权限请求
- [ ] 存储权限请求
- [ ] 权限拒绝处理

## 发布准备

### 1. 生成签名APK
1. 在Android Studio中选择 "Build" → "Generate Signed Bundle / APK"
2. 选择APK选项
3. 创建或选择密钥库
4. 选择release构建类型
5. 完成签名过程

### 2. 优化设置
- 启用代码混淆（ProGuard/R8）
- 优化APK大小
- 测试release版本功能

## 技术支持

如果遇到构建问题，请检查：
1. Android Studio版本是否符合要求
2. Gradle版本是否兼容
3. 网络连接是否正常
4. 磁盘空间是否充足

更多技术细节请参考项目根目录的 `README.md` 文件。