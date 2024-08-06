package com.yaabelozerov.moodb.data.model


import com.squareup.moshi.JsonClass
import com.yaabelozerov.moodb.R

@JsonClass(generateAdapter = false)
enum class IconTheme(
    val nameRes: Int,
    val ANXIOUS: Int,
    val UNCOMFY: Int,
    val SAD: Int,
    val DEPRESSED: Int,
    val CHILL: Int,
    val CALM: Int,
    val BORED: Int,
    val NUMB: Int,
    val CONFUSED: Int,
    val GRATEFUL: Int,
    val HAPPY: Int,
    val EXCITED: Int,
    val HOPEFUL: Int,
    val ANNOYED: Int,
    val ANGRY: Int,
    val OUTRAGED: Int
) {
    SIMPLE(
        R.string.theme_simple,
        R.drawable.mood_apple_anxious,
        R.drawable.mood_apple_uncomfy,
        R.drawable.mood_apple_sad,
        R.drawable.mood_apple_depressed,
        R.drawable.mood_apple_chill,
        R.drawable.mood_apple_calm,
        R.drawable.mood_apple_bored,
        R.drawable.mood_apple_numb,
        R.drawable.mood_apple_confused,
        R.drawable.mood_apple_grateful,
        R.drawable.mood_apple_happy,
        R.drawable.mood_apple_excited,
        R.drawable.mood_apple_hopeful,
        R.drawable.mood_apple_annoyed,
        R.drawable.mood_apple_angry,
        R.drawable.mood_apple_outraged,
    ),
    APPLE(
        R.string.theme_apple,
        R.drawable.mood_apple_anxious,
        R.drawable.mood_apple_uncomfy,
        R.drawable.mood_apple_sad,
        R.drawable.mood_apple_depressed,
        R.drawable.mood_apple_chill,
        R.drawable.mood_apple_calm,
        R.drawable.mood_apple_bored,
        R.drawable.mood_apple_numb,
        R.drawable.mood_apple_confused,
        R.drawable.mood_apple_grateful,
        R.drawable.mood_apple_happy,
        R.drawable.mood_apple_excited,
        R.drawable.mood_apple_hopeful,
        R.drawable.mood_apple_annoyed,
        R.drawable.mood_apple_angry,
        R.drawable.mood_apple_outraged,
    );

    fun mapToIconResource(type: DefaultMoodType): Int {
        return when (type) {
            DefaultMoodType.ANXIOUS -> this.ANXIOUS
            DefaultMoodType.UNCOMFY -> this.UNCOMFY
            DefaultMoodType.SAD -> this.SAD
            DefaultMoodType.DEPRESSED -> this.DEPRESSED
            DefaultMoodType.CHILL -> this.CHILL
            DefaultMoodType.CALM -> this.CALM
            DefaultMoodType.BORED -> this.BORED
            DefaultMoodType.NUMB -> this.NUMB
            DefaultMoodType.CONFUSED -> this.CONFUSED
            DefaultMoodType.GRATEFUL -> this.GRATEFUL
            DefaultMoodType.HAPPY -> this.HAPPY
            DefaultMoodType.EXCITED -> this.EXCITED
            DefaultMoodType.HOPEFUL -> this.HOPEFUL
            DefaultMoodType.ANNOYED -> this.ANNOYED
            DefaultMoodType.ANGRY -> this.ANGRY
            DefaultMoodType.OUTRAGED -> this.OUTRAGED
        }
    }
}

@JsonClass(generateAdapter = true)
data class CustomIconTheme(
    val name: String,
    val iconRounding: Float,
    val anxious: String? = null,
    val uncomfy: String? = null,
    val sad: String? = null,
    val depressed: String? = null,
    val chill: String? = null,
    val calm: String? = null,
    val bored: String? = null,
    val numb: String? = null,
    val confused: String? = null,
    val grateful: String? = null,
    val happy: String? = null,
    val excited: String? = null,
    val hopeful: String? = null,
    val annoyed: String? = null,
    val angry: String? = null,
    val outraged: String? = null,
) {
    fun mapToIconPath(type: DefaultMoodType): String? {
        return when (type) {
            DefaultMoodType.ANXIOUS -> this.anxious
            DefaultMoodType.UNCOMFY -> this.uncomfy
            DefaultMoodType.SAD -> this.sad
            DefaultMoodType.DEPRESSED -> this.depressed
            DefaultMoodType.CHILL -> this.chill
            DefaultMoodType.CALM -> this.calm
            DefaultMoodType.BORED -> this.bored
            DefaultMoodType.NUMB -> this.numb
            DefaultMoodType.CONFUSED -> this.confused
            DefaultMoodType.GRATEFUL -> this.grateful
            DefaultMoodType.HAPPY -> this.happy
            DefaultMoodType.EXCITED -> this.excited
            DefaultMoodType.HOPEFUL -> this.hopeful
            DefaultMoodType.ANNOYED -> this.annoyed
            DefaultMoodType.ANGRY -> this.angry
            DefaultMoodType.OUTRAGED -> this.outraged
        }
    }

    fun setByType(type: DefaultMoodType, value: String?): CustomIconTheme {
        return when (type) {
            DefaultMoodType.ANXIOUS -> this.copy(anxious = value)
            DefaultMoodType.UNCOMFY -> this.copy(uncomfy = value)
            DefaultMoodType.SAD -> this.copy(sad = value)
            DefaultMoodType.DEPRESSED -> this.copy(depressed = value)
            DefaultMoodType.CHILL -> this.copy(chill = value)
            DefaultMoodType.CALM -> this.copy(calm = value)
            DefaultMoodType.BORED -> this.copy(bored = value)
            DefaultMoodType.NUMB -> this.copy(numb = value)
            DefaultMoodType.CONFUSED -> this.copy(confused = value)
            DefaultMoodType.GRATEFUL -> this.copy(grateful = value)
            DefaultMoodType.HAPPY -> this.copy(happy = value)
            DefaultMoodType.EXCITED -> this.copy(excited = value)
            DefaultMoodType.HOPEFUL -> this.copy(hopeful = value)
            DefaultMoodType.ANNOYED -> this.copy(annoyed = value)
            DefaultMoodType.ANGRY -> this.copy(angry = value)
            DefaultMoodType.OUTRAGED -> this.copy(outraged = value)
        }
    }
}

@JsonClass(generateAdapter = true)
data class ThemeList(
    val list: List<CustomIconTheme>
)
