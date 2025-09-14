package com.example.foldercamera1

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foldercamera1.databinding.ActivitySettingsBinding
import java.io.File

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var folderAdapter: FolderAdapter
    private val folders = mutableListOf<FolderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        loadFolders()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        folderAdapter = FolderAdapter(
            folders = folders,
            onFolderSelected = { folderName ->
                saveSelectedFolder(folderName)
            },
            onFolderDelete = { folderName ->
                showDeleteConfirmDialog(folderName)
            }
        )
        
        binding.rvFolders.apply {
            layoutManager = LinearLayoutManager(this@SettingsActivity)
            adapter = folderAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnCreateFolder.setOnClickListener {
            createNewFolder()
        }
    }

    private fun loadFolders() {
        folders.clear()
        
        val sharedPref = getSharedPreferences("folder_camera_prefs", MODE_PRIVATE)
        val currentFolder = sharedPref.getString("current_folder", "文件夹相机默认") ?: "文件夹相机默认"
        val appCreatedFolders = sharedPref.getStringSet("app_created_folders", setOf()) ?: setOf()
        
        // 确保默认文件夹存在
        ensureDefaultFolder()

        // 只加载本应用创建的文件夹
        val picturesDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath)
        if (picturesDir.exists() && picturesDir.isDirectory) {
            picturesDir.listFiles()?.forEach { file ->
                if (file.isDirectory && appCreatedFolders.contains(file.name)) {
                    folders.add(FolderItem(file.name, currentFolder == file.name))
                }
            }
        }

        // 如果没有找到任何文件夹，添加默认文件夹
        if (folders.isEmpty()) {
            folders.add(FolderItem("文件夹相机默认", true))
        }

        folderAdapter.notifyDataSetChanged()
    }

    private fun createNewFolder() {
        val folderName = binding.etFolderName.text.toString().trim()
        
        if (folderName.isEmpty()) {
            Toast.makeText(this, getString(R.string.folder_name_empty), Toast.LENGTH_SHORT).show()
            return
        }

        // 检查文件夹是否已存在
        if (folders.any { it.name == folderName }) {
            Toast.makeText(this, getString(R.string.folder_exists), Toast.LENGTH_SHORT).show()
            return
        }

        // 创建文件夹
        val newFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            folderName
        )
        
        if (newFolder.mkdirs() || newFolder.exists()) {
            // 记录应用创建的文件夹
            val sharedPref = getSharedPreferences("folder_camera_prefs", MODE_PRIVATE)
            val appCreatedFolders = sharedPref.getStringSet("app_created_folders", setOf())?.toMutableSet() ?: mutableSetOf()
            appCreatedFolders.add(folderName)
            
            with(sharedPref.edit()) {
                putStringSet("app_created_folders", appCreatedFolders)
                apply()
            }
            
            binding.etFolderName.text?.clear()
            Toast.makeText(this, getString(R.string.folder_created), Toast.LENGTH_SHORT).show()
            loadFolders()
        } else {
            Toast.makeText(this, "创建文件夹失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSelectedFolder(folderName: String) {
        val sharedPref = getSharedPreferences("folder_camera_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("current_folder", folderName)
            apply()
        }
        
        // 更新选中状态
        folders.forEach { it.isSelected = it.name == folderName }
        folderAdapter.notifyDataSetChanged()
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
        
        with(sharedPref.edit()) {
            putStringSet("app_created_folders", appCreatedFolders)
            apply()
        }
    }

    private fun showDeleteConfirmDialog(folderName: String) {
        if (folderName == "文件夹相机默认") {
            Toast.makeText(this, "无法删除默认文件夹", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("删除文件夹")
            .setMessage("确定要删除文件夹 '$folderName' 及其所有内容吗？")
            .setPositiveButton("删除") { _, _ ->
                deleteFolder(folderName)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteFolder(folderName: String) {
        val folderToDelete = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            folderName
        )
        
        if (folderToDelete.exists() && folderToDelete.deleteRecursively()) {
            // 从应用创建的文件夹列表中移除
            val sharedPref = getSharedPreferences("folder_camera_prefs", MODE_PRIVATE)
            val appCreatedFolders = sharedPref.getStringSet("app_created_folders", setOf())?.toMutableSet() ?: mutableSetOf()
            appCreatedFolders.remove(folderName)
            
            with(sharedPref.edit()) {
                putStringSet("app_created_folders", appCreatedFolders)
                apply()
            }
            
            Toast.makeText(this, getString(R.string.folder_deleted), Toast.LENGTH_SHORT).show()
            
            // 如果删除的是当前选中的文件夹，切换到默认文件夹
            val currentFolder = sharedPref.getString("current_folder", "文件夹相机默认")
            if (currentFolder == folderName) {
                saveSelectedFolder("文件夹相机默认")
            }
            
            loadFolders()
        } else {
            Toast.makeText(this, "删除文件夹失败", Toast.LENGTH_SHORT).show()
        }
    }
}