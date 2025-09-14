# 📸 文件夹相机 (Folder Camera)

<div align="center">

![App Icon](icon.png)

**一个功能强大的Android相机应用，支持自定义文件夹存储和多种拍摄比例**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![API](https://img.shields.io/badge/API-29%2B-brightgreen.svg)](https://android-arsenal.com/api?level=29)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

## ✨ 功能特性

### 🎯 核心功能
- **📁 智能文件夹管理** - 自动创建专属默认文件夹，支持自定义文件夹分类存储
- **📷 多比例拍摄** - 支持1:1、4:3、16:9和全屏四种拍摄比例
- **⚡ 连续拍摄** - 快速连拍模式，无需确认保存
- **🔊 原生快门音** - 保留系统相机快门声音体验
- **🎨 现代化UI** - Material Design 3设计语言

### 📱 界面功能
- **全屏取景器** - 沉浸式拍照体验
- **实时文件夹显示** - 左上角显示当前存储位置
- **快速设置入口** - 右上角一键进入设置
- **比例切换** - 动态调整预览框比例，媲美系统相机效果

### ⚙️ 设置管理
- **文件夹创建** - 简单输入即可创建新文件夹
- **单选列表** - 直观的文件夹选择界面
- **安全删除** - 支持删除自定义文件夹（保护默认文件夹）
- **实时同步** - 删除文件夹时同步清理内容

## 🎥 预览框比例特性

### 📐 支持的拍摄比例
| 比例 | 效果 | 适用场景 |
|------|------|----------|
| **1:1** | 正方形预览，上下大黑边 | Instagram风格，艺术摄影 |
| **4:3** | 标准矩形，上下适中黑边 | 经典照片比例，日常拍摄 |
| **16:9** | 宽屏矩形，上下大黑边 | 电影风格，风景摄影 |
| **全屏** | 填满屏幕，无黑边 | 最大取景范围 |

### 🔧 技术实现
- **容器化设计** - 使用FrameLayout精确控制预览区域
- **动态比例调整** - 实时切换预览框尺寸
- **居中显示** - 预览内容始终保持屏幕中央
- **流畅过渡** - 无闪烁的比例切换动画

## 🛠️ 技术栈

- **开发语言**: Kotlin 100%
- **最低版本**: Android 10 (API 29)
- **目标版本**: Android 14 (API 36)
- **相机框架**: CameraX
- **架构模式**: MVVM + ViewBinding
- **UI设计**: Material Design 3

## 📦 核心依赖

```kotlin
// CameraX 相机框架
implementation("androidx.camera:camera-core:1.3.1")
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")
implementation("androidx.camera:camera-view:1.3.1")

// UI 组件
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.activity:activity-ktx:1.8.2")
implementation("androidx.fragment:fragment-ktx:1.6.2")
```

## 🚀 快速开始

### 环境要求
- Android Studio Arctic Fox 或更高版本
- JDK 11 或更高版本
- Android SDK API 36

### 构建步骤

```bash
# 克隆项目
git clone https://github.com/input111/FolderCamera.git
cd FolderCamera1

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease

# 安装到设备
./gradlew installDebug
```

## 📱 使用指南

### 首次使用
1. 启动应用后授权相机和存储权限
2. 默认照片保存到Pictures/文件夹相机默认目录
3. 点击拍照按钮开始拍摄

### 文件夹管理
1. 点击右上角设置按钮
2. 输入文件夹名称，点击"+"创建
3. 选择单选框切换存储位置
4. 使用删除按钮移除不需要的文件夹

### 比例调整
1. 在设置中选择拍摄比例
2. 返回拍摄界面查看预览框变化
3. 不同比例下黑边效果不同，完全模拟系统相机

## 📁 项目结构

```
app/src/main/
├── java/com/example/foldercamera1/
│   ├── MainActivity.kt          # 主相机界面
│   ├── SettingsActivity.kt      # 设置界面
│   ├── SettingsMenuActivity.kt  # 设置菜单
│   ├── FolderAdapter.kt         # 文件夹列表适配器
│   └── FolderItem.kt           # 文件夹数据模型
├── res/
│   ├── layout/                 # 界面布局文件
│   ├── drawable/               # 图标和背景资源
│   └── values/                 # 字符串和颜色资源
└── AndroidManifest.xml        # 应用清单文件
```

## 🔐 权限说明

```xml
<!-- 相机权限 -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- 存储权限 (Android 10以下) -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />

<!-- 媒体权限 (Android 13+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## 🎨 设计亮点

- **沉浸式体验** - 全屏相机界面，专注拍摄
- **直观操作** - 简洁的图标和布局设计
- **响应式设计** - 适配不同屏幕尺寸
- **无障碍支持** - 完整的内容描述标签

## 🔧 技术亮点

### 相机功能
- **高性能预览** - CameraX优化的相机预览
- **智能权限处理** - 完整的运行时权限管理
- **原生音效** - MediaActionSound集成
- **自动保存** - 无需确认的快速保存机制

### 存储管理
- **分区存储适配** - 完美支持Android 10+分区存储
- **智能路径管理** - 自动处理不同版本存储路径
- **安全文件操作** - 防止误删系统重要文件夹
- **数据持久化** - SharedPreferences保存用户设置

### 预览框比例系统
- **精确比例控制** - 使用ConstraintLayout的dimensionRatio
- **动态调整** - 实时切换预览容器尺寸
- **视觉一致性** - 与系统相机完全一致的显示效果
- **流畅动画** - 平滑的比例切换过渡

## 📋 功能清单

- ✅ 全屏相机取景界面
- ✅ 连续拍摄模式
- ✅ 快门音效保留
- ✅ 自动文件夹存储
- ✅ 文件夹创建和管理
- ✅ 多种拍摄比例支持
- ✅ 预览框动态调整
- ✅ 现代化Material Design界面
- ✅ 完整权限处理
- ✅ Android 10+分区存储适配

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

1. Fork 这个项目
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情



<div align="center">

**⭐ 如果这个项目对你有帮助，请给它一个星标！**

Made with ❤️ by [Your Name]

</div>