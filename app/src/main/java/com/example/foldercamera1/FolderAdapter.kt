package com.example.foldercamera1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foldercamera1.databinding.ItemFolderBinding

class FolderAdapter(
    private val folders: List<FolderItem>,
    private val onFolderSelected: (String) -> Unit,
    private val onFolderDelete: (String) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(private val binding: ItemFolderBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(folder: FolderItem) {
            binding.tvFolderName.text = folder.name
            binding.rbFolder.isChecked = folder.isSelected
            
            // 设置点击事件
            binding.rbFolder.setOnClickListener {
                onFolderSelected(folder.name)
            }
            
            binding.root.setOnClickListener {
                binding.rbFolder.isChecked = true
                onFolderSelected(folder.name)
            }
            
            // 删除按钮点击事件
            binding.btnDelete.setOnClickListener {
                onFolderDelete(folder.name)
            }
            
            // 默认文件夹不显示删除按钮
            binding.btnDelete.visibility = if (folder.name == "文件夹相机默认") {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(folders[position])
    }

    override fun getItemCount(): Int = folders.size
}