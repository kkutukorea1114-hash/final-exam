package com.example.finalexam

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
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

    // [NEW] 새로 추가된 변수들
    private lateinit var ipAddressText: TextView
    private lateinit var timer: Chronometer

    private var isConnected = false
    private val handler = Handler(Looper.getMainLooper())

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

        // [NEW] 뷰 연결
        ipAddressText = findViewById(R.id.ipAddressText)
        timer = findViewById(R.id.timer)
    }

    private fun setupCountrySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isConnected) {
                    var targetCountry = countries[position]
                    var toastMsg = "$targetCountry 서버로 이동했습니다."

                    // [핵심 로직] 연결 중 '자동'을 선택하면 또 랜덤 추첨
                    if (targetCountry.contains("자동")) {
                        val randomIndex = (1 until countries.size).random()
                        targetCountry = countries[randomIndex]
                        toastMsg = "최적 서버($targetCountry)로 재연결했습니다."
                    }

                    // 1. IP 변경
                    val newFakeIp = generateFakeIp(targetCountry)
                    ipAddressText.text = "보호된 IP: $newFakeIp"

                    // 2. 속도 및 텍스트 변경
                    val newDownSpeed = (50..100).random()
                    speedText.text = "서버: $targetCountry\n다운로드: $newDownSpeed.5 Mbps\n업로드: 30.2 Mbps"

                    // 3. 안내 메시지
                    Toast.makeText(applicationContext, toastMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
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
        connectButton.isEnabled = false
        statusText.text = "최적의 서버 찾는 중..." // 멘트 수정 (디테일!)
        progressBar.visibility = View.VISIBLE

        handler.postDelayed({
            isConnected = true
            connectButton.isEnabled = true
            connectButton.text = "연결 해제"

            // 버튼 배경 빨간색 그라데이션
            connectButton.setBackgroundResource(R.drawable.bg_button_red)

            // [핵심 로직] 자동 선택 시 랜덤 나라 지정
            var selectedCountry = countrySpinner.selectedItem.toString()
            var displayCountryName = selectedCountry

            if (selectedCountry.contains("자동")) {
                // 0번(자동)을 제외한 나머지 나라 중 하나 랜덤 뽑기
                val randomIndex = (1 until countries.size).random()
                val randomCountry = countries[randomIndex]

                // 실제 연결된 나라는 이걸로 설정
                selectedCountry = randomCountry
                displayCountryName = "$randomCountry (최적 서버)"
            }

            statusText.text = "VPN 연결됨"
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            progressBar.visibility = View.GONE

            connectionIcon.setImageResource(android.R.drawable.presence_online)
            val rotation = ObjectAnimator.ofFloat(connectionIcon, "rotation", 0f, 360f)
            rotation.duration = 500
            rotation.start()

            timer.visibility = View.VISIBLE
            timer.base = SystemClock.elapsedRealtime()
            timer.start()

            // 랜덤으로 뽑힌 나라에 맞는 IP 생성
            val fakeIp = generateFakeIp(selectedCountry)

            ipAddressText.text = "보호된 IP: $fakeIp"
            ipAddressText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))

            speedText.visibility = View.VISIBLE
            // 속도도 "자동"일 땐 더 빠르게 나오게 설정 (90~150 Mbps)
            val downSpeed = if(displayCountryName.contains("최적")) (90..150).random() else 45
            speedText.text = "서버: $displayCountryName\n다운로드: $downSpeed.2 Mbps\n업로드: 23.1 Mbps"

            Toast.makeText(this, "가장 빠른 $selectedCountry 서버에 연결되었습니다!", Toast.LENGTH_SHORT).show()
        }, 2000)
    }

    private fun disconnect() {
        connectButton.isEnabled = false
        statusText.text = "연결 해제 중..."
        progressBar.visibility = View.VISIBLE

        handler.postDelayed({
            isConnected = false
            connectButton.isEnabled = true
            connectButton.text = "연결하기"

            // [NEW] 버튼 배경을 파란색 그라데이션으로 원상복구
            connectButton.setBackgroundResource(R.drawable.bg_button_blue)

            statusText.text = "연결 안 됨"
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            progressBar.visibility = View.GONE

            connectionIcon.setImageResource(android.R.drawable.presence_offline)

            // [NEW] 타이머 정지 및 초기화
            timer.stop()
            timer.visibility = View.GONE

            // [NEW] IP 원상복구
            ipAddressText.text = "현재 IP: 192.168.0.1 (노출됨)"
            ipAddressText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))

            speedText.visibility = View.GONE

            Toast.makeText(this, "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show()
        }, 1000)
    }

    // [NEW] 국가별 그럴싸한 가짜 IP를 만들어주는 함수
    private fun generateFakeIp(country: String): String {
        return when {
            country.contains("미국") -> "104.23.11.${(10..99).random()}"
            country.contains("일본") -> "203.11.89.${(10..99).random()}"
            country.contains("독일") -> "188.44.22.${(10..99).random()}"
            country.contains("영국") -> "51.15.90.${(10..99).random()}"
            country.contains("싱가포르") -> "128.199.20.${(10..99).random()}"
            country.contains("프랑스") -> "37.187.14.${(10..99).random()}"
            country.contains("대한민국") -> "211.55.10.${(10..99).random()}"
            else -> "45.11.23.${(10..99).random()}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}