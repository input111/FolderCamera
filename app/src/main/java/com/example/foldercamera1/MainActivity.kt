package com.example.foldercamera1

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaActionSound
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Size
import android.view.Gravity
import android.view.OrientationEventListener
import android.view.Surface
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.example.foldercamera1.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var mediaActionSound: MediaActionSound
    private var currentFolder: String = "文件夹相机默认"
    private var timerDelay: Int = 0 // 延时秒数
    private var photoRatio: String = "4:3" // 照片比例
    private val handler = Handler(Looper.getMainLooper())
    private var orientationEventListener: OrientationEventListener? = null
    private var currentRotation = Surface.ROTATION_0

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value)
                permissionGranted = false
        }
        if (!permissionGranted) {
            showTopToast(getString(R.string.permission_camera_denied))
        } else {
            startCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化快门音效
        mediaActionSound = MediaActionSound()
        mediaActionSound.load(MediaActionSound.SHUTTER_CLICK)

        // 请求权限
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // 设置按钮点击事件
        binding.btnCapture.setOnClickListener { takePhoto() }
        binding.btnSettings.setOnClickListener { 
            startActivity(Intent(this, SettingsMenuActivity::class.java))
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        
        // 初始化方向监听器
        setupOrientationListener()
        
        // 确保默认文件夹存在
        ensureDefaultFolder()
        
        // 更新当前文件夹显示
        updateCurrentFolderDisplay()
    }

    override fun onResume() {
        super.onResume()
        val oldRatio = photoRatio
        updateCurrentFolderDisplay()
        
        // 启动方向监听器
        orientationEventListener?.enable()
        
        // 如果比例设置发生变化，重新启动相机
        if (oldRatio != photoRatio && allPermissionsGranted()) {
            startCamera()
            // 显示比例变化提示
            showTopToast("已切换到 $photoRatio 比例")
        }
    }

    override fun onPause() {
        super.onPause()
        // 停止方向监听器
        orientationEventListener?.disable()
    }

    private fun ensureDefaultFolder() {
        val defaultFolderName = "文件夹相机默认"
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val defaultFolder = File(picturesDir, defaultFolderName)
        
        // 创建默认文件夹（如果不存在）
        if (!defaultFolder.exists()) {
            defaultFolder.mkdirs()
        }
        
        val sharedPref = getSharedPreferences("folder_camera_prefs", MODE_PRIVATE)
        
        // 记录这是应用创建的文件夹
        val appCreatedFolders = sharedPref.getStringSet("app_created_folders", setOf())?.toMutableSet() ?: mutableSetOf()
        appCreatedFolders.add(defaultFolderName)
        
        // 如果用户还没有选择过文件夹，设置为默认文件夹
        val currentSelectedFolder = sharedPref.getString("current_folder", null)
        
        with(sharedPref.edit()) {
            putStringSet("app_created_folders", appCreatedFolders)
            if (currentSelectedFolder == null) {
                putString("current_folder", defaultFolderName)
            }
            apply()
        }
    }

    private fun updateCurrentFolderDisplay() {
        val sharedPref = getSharedPreferences("folder_camera_prefs", MODE_PRIVATE)
        currentFolder = sharedPref.getString("current_folder", "文件夹相机默认") ?: "文件夹相机默认"
        timerDelay = sharedPref.getInt("timer_delay", 0)
        photoRatio = sharedPref.getString("photo_ratio", "4:3") ?: "4:3"
        binding.tvCurrentFolder.text = currentFolder
        
        // 调整预览框比例
        adjustPreviewRatio()
    }

    private fun adjustPreviewRatio() {
        val previewContainer = binding.previewContainer
        val layoutParams = previewContainer.layoutParams as ConstraintLayout.LayoutParams
        
        when (photoRatio) {
            "1:1" -> {
                // 1:1 正方形比例 - 以屏幕宽度为准
                layoutParams.dimensionRatio = "1:1"
                layoutParams.width = 0
                layoutParams.height = 0
            }
            "4:3" -> {
                // 4:3 比例 - 以屏幕宽度为准
                layoutParams.dimensionRatio = "3:4"
                layoutParams.width = 0
                layoutParams.height = 0
            }
            "16:9" -> {
                // 16:9 比例 - 以屏幕宽度为准
                layoutParams.dimensionRatio = "9:16"
                layoutParams.width = 0
                layoutParams.height = 0
            }
            "全屏" -> {
                // 全屏模式，移除比例限制
                layoutParams.dimensionRatio = null
                layoutParams.width = 0
                layoutParams.height = 0
            }
        }
        
        // 确保容器居中显示
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        
        previewContainer.layoutParams = layoutParams
        previewContainer.requestLayout()
    }

    private fun showTopToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.show()
    }

    private fun setupOrientationListener() {
        orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return
                
                val rotation = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                
                if (rotation != currentRotation) {
                    currentRotation = rotation
                    imageCapture?.targetRotation = rotation
                }
            }
        }
    }

    private fun getAspectRatio(): Int {
        return when (photoRatio) {
            "1:1" -> AspectRatio.RATIO_4_3 // 使用4:3作为1:1的基础，后续裁剪
            "4:3" -> AspectRatio.RATIO_4_3
            "16:9" -> AspectRatio.RATIO_16_9
            "全屏" -> AspectRatio.RATIO_16_9 // 全屏使用16:9
            else -> AspectRatio.RATIO_4_3 // 默认4:3
        }
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetAspectRatio(getAspectRatio())
                .build().also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(getAspectRatio())
                .setTargetRotation(binding.previewView.display.rotation)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                
                // 相机启动完成后调整预览框比例
                adjustPreviewRatio()
            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        if (timerDelay > 0) {
            // 延时拍摄 - 显示倒计时
            startCountdown(imageCapture)
        } else {
            // 立即拍摄
            capturePhoto(imageCapture)
        }
    }

    private fun startCountdown(imageCapture: ImageCapture) {
        var countdown = timerDelay
        binding.tvCountdown.visibility = android.view.View.VISIBLE
        
        val countdownRunnable = object : Runnable {
            override fun run() {
                if (countdown > 0) {
                    binding.tvCountdown.text = countdown.toString()
                    // 添加缩放动画效果
                    binding.tvCountdown.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(200)
                        .withEndAction {
                            binding.tvCountdown.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(200)
                                .start()
                        }
                        .start()
                    
                    countdown--
                    handler.postDelayed(this, 1000)
                } else {
                    // 倒计时结束，隐藏倒计时显示并拍照
                    binding.tvCountdown.visibility = android.view.View.GONE
                    capturePhoto(imageCapture)
                }
            }
        }
        
        countdownRunnable.run()
    }

    private fun capturePhoto(imageCapture: ImageCapture) {
        // 播放快门音效
        mediaActionSound.play(MediaActionSound.SHUTTER_CLICK)

        // 更新目标旋转角度以确保照片方向正确
        imageCapture.targetRotation = currentRotation

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                // 所有照片都保存到Pictures下的子文件夹中
                val relativePath = "${Environment.DIRECTORY_PICTURES}/$currentFolder"
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    showTopToast(getString(R.string.photo_save_failed))
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    showTopToast(getString(R.string.photo_saved))
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        mediaActionSound.release()
        orientationEventListener?.disable()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }.toTypedArray()
    }
}