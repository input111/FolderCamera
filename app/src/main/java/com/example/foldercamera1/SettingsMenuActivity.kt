package com.example.foldercamera1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.foldercamera1.databinding.ActivitySettingsMenuBinding

class SettingsMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsMenuBinding
    private lateinit var sharedPref: SharedPreferences
    
    // 当前选中的比例和延时
    private var currentRatio = "4:3"
    private var currentTimer = 0 // 0=无延时, 3=3秒, 5=5秒, 10=10秒
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPref = getSharedPreferences("folder_camera_prefs", MODE_PRIVATE)
        
        // 加载保存的设置
        loadSettings()
        
        // 设置点击事件
        setupClickListeners()
        
        // 更新UI显示
        updateUI()
    }
    
    private fun loadSettings() {
        currentRatio = sharedPref.getString("photo_ratio", "4:3") ?: "4:3"
        currentTimer = sharedPref.getInt("timer_delay", 0)
    }
    
    private fun setupClickListeners() {
        // 关闭按钮
        binding.btnClose.setOnClickListener {
            finish()
        }
        
        // 存储设置按钮
        binding.btnStorageSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // 比例选择
        binding.ratio1to1.setOnClickListener { selectRatio("1:1") }
        binding.ratio4to3.setOnClickListener { selectRatio("4:3") }
        binding.ratio16to9.setOnClickListener { selectRatio("16:9") }
        binding.ratioFull.setOnClickListener { selectRatio("全屏") }
        
        // 延时选择
        binding.timerOff.setOnClickListener { selectTimer(0) }
        binding.timer3s.setOnClickListener { selectTimer(3) }
        binding.timer5s.setOnClickListener { selectTimer(5) }
        binding.timer10s.setOnClickListener { selectTimer(10) }
    }
    
    private fun selectRatio(ratio: String) {
        currentRatio = ratio
        saveSettings()
        updateUI()
        
        // 选择比例后自动退出设置界面
        finish()
    }
    
    private fun selectTimer(timer: Int) {
        currentTimer = timer
        saveSettings()
        updateUI()
    }
    
    private fun saveSettings() {
        with(sharedPref.edit()) {
            putString("photo_ratio", currentRatio)
            putInt("timer_delay", currentTimer)
            apply()
        }
    }
    
    private fun updateUI() {
        // 重置所有比例按钮颜色
        resetRatioButtons()
        
        // 设置选中的比例按钮
        when (currentRatio) {
            "1:1" -> setSelectedRatio(binding.ratio1to1)
            "4:3" -> setSelectedRatio(binding.ratio4to3)
            "16:9" -> setSelectedRatio(binding.ratio16to9)
            "全屏" -> setSelectedRatio(binding.ratioFull)
        }
        
        // 重置所有延时按钮颜色
        resetTimerButtons()
        
        // 设置选中的延时按钮
        when (currentTimer) {
            0 -> setSelectedTimer(binding.timerOff)
            3 -> setSelectedTimer(binding.timer3s)
            5 -> setSelectedTimer(binding.timer5s)
            10 -> setSelectedTimer(binding.timer10s)
        }
    }
    
    private fun resetRatioButtons() {
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        binding.ratio1to1.setCardBackgroundColor(whiteColor)
        binding.ratio4to3.setCardBackgroundColor(whiteColor)
        binding.ratio16to9.setCardBackgroundColor(whiteColor)
        binding.ratioFull.setCardBackgroundColor(whiteColor)
    }
    
    private fun resetTimerButtons() {
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        binding.timerOff.setCardBackgroundColor(whiteColor)
        binding.timer3s.setCardBackgroundColor(whiteColor)
        binding.timer5s.setCardBackgroundColor(whiteColor)
        binding.timer10s.setCardBackgroundColor(whiteColor)
    }
    
    private fun setSelectedRatio(cardView: CardView) {
        val yellowColor = ContextCompat.getColor(this, R.color.yellow)
        cardView.setCardBackgroundColor(yellowColor)
    }
    
    private fun setSelectedTimer(cardView: CardView) {
        val yellowColor = ContextCompat.getColor(this, R.color.yellow)
        cardView.setCardBackgroundColor(yellowColor)
    }
}