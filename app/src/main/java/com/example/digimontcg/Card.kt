package com.example.digimontcg

import android.os.Parcel
import android.os.Parcelable

// Clase de datos que representa una carta de Digimon TCG.
// Implementa la interfaz Parcelable para que los objetos puedan ser transferidos entre actividades o fragmentos.
// Cada carta tiene varios atributos: un ID único, colores, descripción, nombre, edición, rareza, tipo, y cantidad.
data class Card(
    val card_id: String = "", // ID único de la carta.
    val color1: String = "", // Primer color de la carta (puede estar vacío si no aplica).
    val color2: String = "", // Segundo color de la carta (puede estar vacío si no aplica).
    val description: String = "", // Descripción de la carta, como su efecto o habilidades.
    val name: String = "", // Nombre de la carta.
    val pack: String = "", // Paquete o edición a la que pertenece la carta.
    val rarity: String = "", // Rareza de la carta (por ejemplo, común, rara, ultra rara, etc.).
    val type: String = "", // Tipo de carta (por ejemplo, Digimon, Tamer, Option, etc.).
    var quantity: Int = 0 // Cantidad de esta carta en la colección del usuario.
) : Parcelable {
    // Constructor secundario que permite reconstruir un objeto `Card` a partir de un Parcel.
    // Cada atributo se lee desde el Parcel utilizando sus métodos correspondientes.
    constructor(parcel: Parcel) : this(
        card_id = parcel.readString() ?: "", // Lee el ID único; si es null, usa una cadena vacía.
        color1 = parcel.readString() ?: "", // Lee el primer color; si es null, usa una cadena vacía.
        color2 = parcel.readString() ?: "", // Lee el segundo color; si es null, usa una cadena vacía.
        description = parcel.readString() ?: "", // Lee la descripción; si es null, usa una cadena vacía.
        name = parcel.readString() ?: "", // Lee el nombre; si es null, usa una cadena vacía.
        pack = parcel.readString() ?: "", // Lee el paquete o edición; si es null, usa una cadena vacía.
        rarity = parcel.readString() ?: "", // Lee la rareza; si es null, usa una cadena vacía.
        type = parcel.readString() ?: "", // Lee el tipo de carta; si es null, usa una cadena vacía.
        quantity = parcel.readInt() // Lee la cantidad como un entero.
    )

    // Método que define cómo serializar los datos de un objeto `Card` para almacenarlos en un Parcel.
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(card_id) // Escribe el ID único de la carta en el Parcel.
        parcel.writeString(color1) // Escribe el primer color de la carta en el Parcel.
        parcel.writeString(color2) // Escribe el segundo color de la carta en el Parcel.
        parcel.writeString(description) // Escribe la descripción de la carta en el Parcel.
        parcel.writeString(name) // Escribe el nombre de la carta en el Parcel.
        parcel.writeString(pack) // Escribe el paquete o edición de la carta en el Parcel.
        parcel.writeString(rarity) // Escribe la rareza de la carta en el Parcel.
        parcel.writeString(type) // Escribe el tipo de carta en el Parcel.
        parcel.writeInt(quantity) // Escribe la cantidad de esta carta en el Parcel.
    }

    // Método que describe los contenidos especiales del objeto. En este caso, retorna 0 porque no hay contenidos especiales.
    override fun describeContents(): Int {
        return 0
    }

    // Objeto Companion que implementa la interfaz `Parcelable.Creator`.
    // Este objeto es responsable de deserializar objetos `Card` desde un Parcel y de crear arrays de objetos `Card`.
    companion object CREATOR : Parcelable.Creator<Card> {
        // Crea un objeto `Card` a partir de un Parcel.
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        // Crea un array de objetos `Card` con un tamaño especificado.
        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
