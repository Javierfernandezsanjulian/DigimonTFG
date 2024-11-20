package com.example.digimontcg

data class Card(
    val name: String,        // Nombre de la carta (si lo necesitas)
    val cardnumber: String,   // Número de la carta (ejemplo: BT1-001)
    var quantity: Int = 0    // Cantidad en la colección del usuario
)
