package com.example.digimontcg

import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Context
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OpenPacksAdapter(
    private val cards: List<Card>,
    private val context: Context // Contexto necesario para vibración y sonido
) : RecyclerView.Adapter<OpenPacksAdapter.PackCardViewHolder>() {

    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(1).build()
    private val specialCardSound: Int = soundPool.load(context, R.raw.sr_card_sound, 1)

    class PackCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardQuantityText: TextView = view.findViewById(R.id.cardQuantityText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_card, parent, false)
        return PackCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackCardViewHolder, position: Int) {
        val card = cards[position]
        holder.cardQuantityText.visibility = View.GONE

        // Mostrar inicialmente el reverso de la carta
        Glide.with(context)
            .load("file:///android_asset/cards/card_back.png") // Reverso de la carta
            .fitCenter()
            .into(holder.cardImage)

        // Configurar clic para girar la carta
        holder.itemView.setOnClickListener {
            flipCard(context, holder.cardImage, card)
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    private fun flipCard(context: Context, cardImage: ImageView, card: Card) {
        val flipIn = AnimatorInflater.loadAnimator(context, R.animator.flip_in) as AnimatorSet
        val flipOut = AnimatorInflater.loadAnimator(context, R.animator.flip_out) as AnimatorSet

        flipOut.setTarget(cardImage)
        flipOut.start()

        flipOut.addListener(object : AnimatorListenerAdapter() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Cambiar la imagen al final de la animación
                Glide.with(context)
                    .load("file:///android_asset/cards/${card.card_id}.jpg") // Carta frontal
                    .fitCenter()
                    .into(cardImage)

                // Si es carta especial, vibrar y reproducir sonido
                if (card.rarity == "Super Rare") {
                    vibrateOnSpecialCard(context)
                    playSpecialCardSound()
                }

                // Iniciar animación de entrada
                flipIn.setTarget(cardImage)
                flipIn.start()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun vibrateOnSpecialCard(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createWaveform(
            longArrayOf(0, 200, 100, 300), // Patrón: espera 0ms, vibra 200ms, espera 100ms, vibra 300ms
            -1 // No repetir
        )
        vibrator.vibrate(vibrationEffect)
    }

    private fun playSpecialCardSound() {
        soundPool.play(specialCardSound, 1f, 1f, 0, 0, 1f)
    }
}
