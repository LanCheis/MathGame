package com.example.homework2

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var mainScreen: LinearLayout
    private lateinit var gameScreen: LinearLayout
    private lateinit var resultScreen: LinearLayout

    // Main screen
    private lateinit var btnStart: Button
    private lateinit var tvWelcome: TextView

    // Game screen
    private lateinit var tvLives: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvWrongAnswers: TextView // Add wrong answers display
    private lateinit var tvQuestion: TextView
    private lateinit var etAnswer: EditText
    private lateinit var btnSubmit: Button

    // Result screen
    private lateinit var tvFinalScore: TextView
    private lateinit var tvCongratulations: TextView
    private lateinit var btnPlayAgain: Button
    private lateinit var btnExit: Button

    // Game variables
    private var lives = 3
    private var score = 0
    private var wrongAnswers = 0 // Count wrong answers
    private var timeLeft = 60 // 60 seconds per game
    private var totalTime = 60 // Total game time
    private var currentNum1 = 0
    private var currentNum2 = 0
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupMainScreen()
    }

    private fun initializeViews() {
        // Main screen
        mainScreen = findViewById(R.id.mainScreen)
        btnStart = findViewById(R.id.btnStart)
        tvWelcome = findViewById(R.id.tvWelcome)

        // Game screen
        gameScreen = findViewById(R.id.gameScreen)
        tvLives = findViewById(R.id.tvLives)
        tvTimer = findViewById(R.id.tvTimer)
        tvScore = findViewById(R.id.tvScore)
        tvWrongAnswers = findViewById(R.id.tvWrongAnswers) // Initialize wrong answers display
        tvQuestion = findViewById(R.id.tvQuestion)
        etAnswer = findViewById(R.id.etAnswer)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Result screen
        resultScreen = findViewById(R.id.resultScreen)
        tvFinalScore = findViewById(R.id.tvFinalScore)
        tvCongratulations = findViewById(R.id.tvCongratulations)
        btnPlayAgain = findViewById(R.id.btnPlayAgain)
        btnExit = findViewById(R.id.btnExit)
    }

    private fun setupMainScreen() {
        showScreen("main")

        btnStart.setOnClickListener {
            startGame()
        }

        btnSubmit.setOnClickListener {
            checkAnswer()
        }

        btnPlayAgain.setOnClickListener {
            resetGame()
            startGame()
        }

        btnExit.setOnClickListener {
            finish()
        }
    }

    private fun showScreen(screen: String) {
        when (screen) {
            "main" -> {
                mainScreen.visibility = View.VISIBLE
                gameScreen.visibility = View.GONE
                resultScreen.visibility = View.GONE
            }
            "game" -> {
                mainScreen.visibility = View.GONE
                gameScreen.visibility = View.VISIBLE
                resultScreen.visibility = View.GONE
            }
            "result" -> {
                mainScreen.visibility = View.GONE
                gameScreen.visibility = View.GONE
                resultScreen.visibility = View.VISIBLE
            }
        }
    }

    private fun startGame() {
        resetGame()
        showScreen("game")
        generateNewQuestion()
        startTimer()
        updateUI()
    }

    private fun resetGame() {
        lives = 3
        score = 0
        wrongAnswers = 0 // Reset wrong answers counter
        timeLeft = 60
        totalTime = 60
        timer?.cancel()
    }

    private fun generateNewQuestion() {
        currentNum1 = Random.nextInt(1, 50)
        currentNum2 = Random.nextInt(1, 50)
        tvQuestion.text = "$currentNum1 + $currentNum2 = ?"
        etAnswer.setText("")
        etAnswer.requestFocus()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeft * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
                updateTimerDisplay()

                // Warning effect when time is running low
                if (timeLeft <= 10) {
                    tvTimer.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                    if (timeLeft <= 5) {
                        // Blink effect for last 5 seconds
                        tvTimer.animate()
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(200)
                            .withEndAction {
                                tvTimer.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(200)
                                    .start()
                            }
                            .start()
                    }
                } else if (timeLeft <= 20) {
                    tvTimer.setTextColor(resources.getColor(android.R.color.holo_orange_dark))
                } else {
                    tvTimer.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                }
            }

            override fun onFinish() {
                timeLeft = 0
                updateTimerDisplay()
                Toast.makeText(this@MainActivity, "Hết thời gian!", Toast.LENGTH_SHORT).show()
                endGame()
            }
        }.start()
    }

    private fun checkAnswer() {
        val userAnswer = etAnswer.text.toString().trim()

        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đáp án!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val answer = userAnswer.toInt()
            val correctAnswer = currentNum1 + currentNum2

            if (answer == correctAnswer) {
                score += 10
                Toast.makeText(this, "Chính xác! +10 điểm", Toast.LENGTH_SHORT).show()
                generateNewQuestion()
            } else {
                lives--
                wrongAnswers++ // Increment wrong answers counter
                Toast.makeText(this, "Sai rồi! Đáp án đúng là $correctAnswer", Toast.LENGTH_SHORT).show()

                if (lives <= 0) {
                    endGame()
                    return
                } else {
                    generateNewQuestion()
                }
            }

            updateUI()

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Vui lòng nhập một số hợp lệ!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        tvLives.text = "❤️ $lives"
        tvScore.text = "🏆 $score"
        tvWrongAnswers.text = "❌ $wrongAnswers"
        updateTimerDisplay()
    }

    private fun updateTimerDisplay() {
        val minutes = timeLeft / 60
        val seconds = timeLeft % 60
        val timeString = if (minutes > 0) {
            String.format("%d:%02d", minutes, seconds)
        } else {
            String.format("%ds", seconds)
        }
        tvTimer.text = "⏰ $timeString"

        // Add progress indication
        val progress = (timeLeft.toFloat() / totalTime.toFloat() * 100).toInt()
        when {
            progress > 66 -> tvTimer.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            progress > 33 -> tvTimer.setTextColor(resources.getColor(android.R.color.holo_orange_dark))
            else -> tvTimer.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }
    }

    private fun endGame() {
        timer?.cancel()
        showResultScreen()
    }

    private fun showResultScreen() {
        tvFinalScore.text = "Điểm cuối cùng: $score"

        val congratsMessage = when {
            score >= 100 -> "Xuất sắc! Bạn là thiên tài toán học!"
            score >= 50 -> "Tốt lắm! Bạn làm rất tốt!"
            score >= 20 -> "Khá ổn! Cố gắng thêm nhé!"
            else -> "Không sao, lần sau sẽ tốt hơn!"
        }

        tvCongratulations.text = congratsMessage
        showScreen("result")
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}