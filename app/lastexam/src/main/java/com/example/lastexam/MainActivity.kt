package com.example.vpnprototype

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var connectButton: Button
    private lateinit var statusText: TextView
    private lateinit var countrySpinner: Spinner
    private lateinit var connectionIcon: ImageView
    private lateinit var speedText: TextView
    private lateinit var progressBar: ProgressBar

    private var isConnected = false
    private val handler = Handler(Looper.getMainLooper())

    // 국가 목록
    private val countries = arrayOf(
        "자동 (가장 빠른 서버)",
        "대한민국 🇰🇷",
        "미국 🇺🇸",
        "일본 🇯🇵",
        "싱가포르 🇸🇬",
        "영국 🇬🇧",
        "독일 🇩🇪",
        "프랑스 🇫🇷"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupCountrySpinner()
        setupConnectButton()
    }

    private fun initViews() {
        connectButton = findViewById(R.id.connectButton)
        statusText = findViewById(R.id.statusText)
        countrySpinner = findViewById(R.id.countrySpinner)
        connectionIcon = findViewById(R.id.connectionIcon)
        speedText = findViewById(R.id.speedText)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupCountrySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter
    }

    private fun setupConnectButton() {
        connectButton.setOnClickListener {
            if (isConnected) {
                disconnect()
            } else {
                connect()
            }
        }
    }

    private fun connect() {
        // 연결 중 상태
        connectButton.isEnabled = false
        statusText.text = "연결 중..."
        progressBar.visibility = View.VISIBLE

        // 연결 애니메이션 (2초)
        handler.postDelayed({
            isConnected = true
            connectButton.isEnabled = true
            connectButton.text = "연결 해제"
            connectButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            statusText.text = "연결됨"
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            progressBar.visibility = View.GONE

            // 연결 아이콘 애니메이션
            connectionIcon.setImageResource(android.R.drawable.presence_online)
            val rotation = ObjectAnimator.ofFloat(connectionIcon, "rotation", 0f, 360f)
            rotation.duration = 500
            rotation.start()

            // 가짜 속도 표시
            speedText.visibility = View.VISIBLE
            val selectedCountry = countrySpinner.selectedItem.toString()
            speedText.text = "서버: $selectedCountry\n다운로드: 45.2 Mbps\n업로드: 23.1 Mbps"

            Toast.makeText(this, "VPN 연결 완료!", Toast.LENGTH_SHORT).show()
        }, 2000)
    }

    private fun disconnect() {
        // 연결 해제 중 상태
        connectButton.isEnabled = false
        statusText.text = "연결 해제 중..."
        progressBar.visibility = View.VISIBLE

        // 연결 해제 애니메이션 (1초)
        handler.postDelayed({
            isConnected = false
            connectButton.isEnabled = true
            connectButton.text = "연결하기"
            connectButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
            statusText.text = "연결 안 됨"
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            progressBar.visibility = View.GONE

            // 연결 아이콘 변경
            connectionIcon.setImageResource(android.R.drawable.presence_offline)

            // 속도 정보 숨김
            speedText.visibility = View.GONE

            Toast.makeText(this, "VPN 연결 해제됨", Toast.LENGTH_SHORT).show()
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}