package com.example.digimontcg

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var packImage: ImageView
    private lateinit var firestore: FirebaseFirestore
    private var isPackOpened = false // Variable para rastrear el estado del sobre

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        firestore = FirebaseFirestore.getInstance()

        // Inicializar componentes
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        packImage = findViewById(R.id.packImage)

        // Configurar listeners
        packImage.setOnClickListener {
            playOpenPackAnimation()
        }
        bottomNavigationView.setOnItemSelectedListener { navigateTo(it.itemId) }

        ensureDigitalCollectionExists()
    }

    private fun playOpenPackAnimation() {
        val animatorSet = AnimatorInflater.loadAnimator(this, R.animator.pack_open_animation) as AnimatorSet
        animatorSet.setTarget(packImage)

        packImage.isClickable = false // Desactivar clic durante la animación
        
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Nada que hacer al inicio
            }

            override fun onAnimationEnd(animation: Animator) {
                // Al finalizar la animación, ocultamos el sobre
                isPackOpened = true
                packImage.visibility = View.INVISIBLE

                // Abrir la actividad para abrir sobres
                val intent = Intent(this@DashboardActivity, OpenPacksActivity::class.java)
                startActivityForResult(intent, 1001)
            }

            override fun onAnimationCancel(animation: Animator) {
                // Restaurar clic y visibilidad si se cancela
                packImage.isClickable = true
                packImage.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSet.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Volver a mostrar el sobre al presionar el botón de retroceso
        packImage.visibility = View.VISIBLE
        isPackOpened = false // Restablecer el estado del sobre
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val isPackOpened = data?.getBooleanExtra("isPackOpened", false) ?: false
            packImage.visibility = if (isPackOpened) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isPackOpened) {
            packImage.visibility = View.VISIBLE
        }
    }

    private fun navigateTo(itemId: Int): Boolean {
        when (itemId) {
            R.id.bottom_home -> return true
            R.id.bottom_collection -> {
                startActivity(Intent(this, CollectionActivity::class.java))
                overridePendingTransition(R.anim.to_left, R.anim.from_right)
                finish()
                return true
            }
            R.id.bottom_deck -> {
                startActivity(Intent(this, DeckCollectionActivity::class.java))
                overridePendingTransition(R.anim.to_left, R.anim.from_right)
                finish()
                return true
            }
            R.id.bottom_profile -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                overridePendingTransition(R.anim.to_left, R.anim.from_right)
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
                    digitalCollectionRef.document("init_document")
                        .set(initialData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Colección digital creada exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error al crear colección digital: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    //Toast.makeText(this, "Colección digital ya existe", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al verificar colección digital: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
