package com.example.digimontcg

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Asegúrate de que el diseño sea el correcto

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val registerBackButton = findViewById<Button>(R.id.registerBackButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                Toast.makeText(
                    this,
                    "Por favor, ingresa el email y la contraseña",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        registerBackButton.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        createUserInFirestore(userId, email)
                    } else {
                        Toast.makeText(this, "Error al obtener UID del usuario", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Error en el registro: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun createUserInFirestore(userId: String, email: String) {
        val userMap = mapOf(
            "email" to email
        )

        // Crea la colección del usuario con el UID como nombre
        val userCollectionRef = firestore.collection("users").document(userId)
        userCollectionRef.set(userMap)
            .addOnSuccessListener {
                // Crea la colección interna para el usuario
                val userCollection = userCollectionRef.collection(userId) // Usa el UID como nombre
                userCollection.add(mapOf("nombre" to "Nombre del usuario", "edad" to 25))
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            this,
                            "Usuario registrado exitosamente en Firestore",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // Vuelve a la actividad principal
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Error al registrar usuario en Firestore: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al registrar usuario en Firestore: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
