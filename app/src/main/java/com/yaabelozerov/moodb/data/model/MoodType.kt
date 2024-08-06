package com.yaabelozerov.moodb.data.model

import com.squareup.moshi.JsonClass
import com.yaabelozerov.moodb.R

@JsonClass(generateAdapter = true)
data class Category(
    val angry: Float, val neutral: Float, val sad: Float, val happy: Float, val energetic: Float
)

@JsonClass(generateAdapter = true)
data class MoodType(
    val defaultMoodType: DefaultMoodType,
    val customCategory: Category? = null,
    val customName: String? = null
)

@JsonClass(generateAdapter = true)
data class MoodList(
    val list: List<MoodType>
)

@JsonClass(generateAdapter = false)
enum class DefaultMoodType(val nameRes: Int, val category: Category) {
    ANXIOUS(
        R.string.mood_anxious, Category(0.0f, 0.5f, 0.5f, 0.0f, 0.0f)
    ),
    UNCOMFY(R.string.mood_uncomfortable, Category(0.0f, 0.8f, 0.2f, 0.0f, 0.0f)), SAD(
        R.string.mood_sad, Category(0.0f, 0.2f, 0.8f, 0.0f, 0.0f)
    ),
    DEPRESSED(
        R.string.mood_depressed, Category(0.0f, 0.0f, 1.0f, 0.0f, 0.0f)
    ),
    CHILL(R.string.mood_chill, Category(0.0f, 0.5f, 0.0f, 0.5f, 0.0f)), CALM(
        R.string.mood_calm, Category(0.0f, 0.2f, 0.0f, 0.8f, 0.0f)
    ),
    BORED(R.string.mood_bored, Category(0.0f, 0.4f, 0.2f, 0.4f, 0.0f)), NUMB(
        R.string.mood_numb, Category(0.0f, 1.0f, 0.0f, 0.0f, 0.0f)
    ),
    CONFUSED(
        R.string.mood_confused, Category(0.2f, 0.2f, 0.6f, 0.0f, 0.0f)
    ),
    GRATEFUL(R.string.mood_grateful, Category(0.0f, 0.5f, 0.0f, 0.5f, 0.0f)), HAPPY(
        R.string.mood_happy, Category(0.0f, 0.0f, 0.0f, 0.8f, 0.2f)
    ),
    EXCITED(
        R.string.mood_excited, Category(0.0f, 0.0f, 0.0f, 1.0f, 0.5f)
    ),
    HOPEFUL(
        R.string.mood_hopeful, Category(0.0f, 0.1f, 0.0f, 0.7f, 0.2f)
    ),
    ANNOYED(R.string.mood_annoyed, Category(0.5f, 0.0f, 0.0f, 0.0f, 0.5f)), ANGRY(
        R.string.mood_angry, Category(0.8f, 0.0f, 0.0f, 0.0f, 0.2f)
    ),
    OUTRAGED(R.string.mood_outraged, Category(1.0f, 0.0f, 0.0f, 0.0f, 0.0f));
}