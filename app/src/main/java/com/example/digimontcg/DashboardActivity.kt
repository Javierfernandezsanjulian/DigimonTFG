package com.example.digimontcg

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var packImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var timerTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore

    private var isPackOpened = false // Variable para rastrear el estado del sobre

    companion object {
        const val LAST_OPENED_TIMESTAMP_KEY = "last_opened_timestamp"
        const val TIME_LIMIT_MS = 2 * 60 * 60 * 1000 // 2 horas en milisegundos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        firestore = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("OpenPacksPrefs", MODE_PRIVATE)

        // Inicializar componentes
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        packImage = findViewById(R.id.packImage)
        progressBar = findViewById(R.id.progressBar)
        timerTextView = findViewById(R.id.timerTextView)

        // Configurar listeners
        packImage.setOnClickListener {
            if (canOpenPack()) {
                playOpenPackAnimation()
            } else {
                Toast.makeText(this, "Espera a que termine el temporizador para abrir otro sobre.", Toast.LENGTH_SHORT).show()
            }
        }
        bottomNavigationView.setOnItemSelectedListener { navigateTo(it.itemId) }

        ensureDigitalCollectionExists()

        // Verificar si el sobre está disponible
        if (canOpenPack()) {
            enablePackOpening()
        } else {
            disablePackOpening()
        }
    }

    private fun canOpenPack(): Boolean {
        val lastOpenedTimestamp = sharedPreferences.getLong(LAST_OPENED_TIMESTAMP_KEY, 0)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastOpenedTimestamp) >= TIME_LIMIT_MS
    }

    private fun enablePackOpening() {
        packImage.isEnabled = true
        packImage.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        timerTextView.visibility = View.GONE
    }

    private fun disablePackOpening() {
        packImage.isEnabled = false
        progressBar.visibility = View.VISIBLE
        timerTextView.visibility = View.VISIBLE

        val lastOpenedTimestamp = sharedPreferences.getLong(LAST_OPENED_TIMESTAMP_KEY, 0)
        val timeRemaining = TIME_LIMIT_MS - (System.currentTimeMillis() - lastOpenedTimestamp)

        startCountdownTimer(timeRemaining)
    }

    private fun startCountdownTimer(timeRemaining: Long) {
        progressBar.max = TIME_LIMIT_MS.toInt()
        progressBar.progress = (TIME_LIMIT_MS - timeRemaining).toInt()

        object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60

                timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                // Actualizar barra de progreso
                progressBar.progress = (TIME_LIMIT_MS - millisUntilFinished).toInt()
            }

            override fun onFinish() {
                enablePackOpening()
            }
        }.start()
    }

    private fun playOpenPackAnimation() {
        val animatorSet = AnimatorInflater.loadAnimator(this, R.animator.pack_open_animation) as AnimatorSet
        animatorSet.setTarget(packImage)

        packImage.isClickable = false // Desactivar clic durante la animación

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                isPackOpened = true
                packImage.visibility = View.INVISIBLE

                // Guardar el timestamp al abrir el sobre
                sharedPreferences.edit().putLong(LAST_OPENED_TIMESTAMP_KEY, System.currentTimeMillis()).apply()

                // Redirigir a OpenPacksActivity
                startActivityForResult(Intent(this@DashboardActivity, OpenPacksActivity::class.java), 1001)
            }

            override fun onAnimationCancel(animation: Animator) {
                packImage.isClickable = true
                packImage.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSet.start()
    }

    override fun onResume() {
        super.onResume()
        if (!isPackOpened && canOpenPack()) {
            enablePackOpening()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            disablePackOpening()
        }
    }

    private fun navigateTo(itemId: Int): Boolean {
        when (itemId) {
            R.id.bottom_home -> return true
            R.id.bottom_collection -> {
                startActivity(Intent(this, CollectionActivity::class.java))
                finish()
                return true
            }
            R.id.bottom_deck -> {
                startActivity(Intent(this, DeckCollectionActivity::class.java))
                finish()
                return true
            }
            R.id.bottom_profile -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                finish()
                return true
            }
        }
        return false
    }

    private fun ensureDigitalCollectionExists() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val digitalCollectionRef = firestore
            .collection("users")
            .document(userId)
            .collection("digital")

        digitalCollectionRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    val initialData = mapOf(
                        "message" to "Colección inicializada",
                        "timestamp" to System.currentTimeMillis()
                    )
                    digitalCollectionRef.document("init_document").set(initialData)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar colección digital: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
