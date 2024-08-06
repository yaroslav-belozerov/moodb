package com.yaabelozerov.moodb.onboarding

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class Destinations(val route: String) {
    WelcomeLanguage("welcome_language"), ThemeSetup("theme_setup");

    fun next(): Destinations {
        val constants = this.javaClass.enumConstants!!
        return if (constants.size == this.ordinal + 1) {
            this
        } else {
            constants[constants.indexOf(this) + 1]
        }
    }

    fun previous(): Destinations {
        val constants = this.javaClass.enumConstants!!
        return if (this.ordinal == 0) this
        else constants[this.ordinal - 1]
    }

    fun isLast(): Boolean = this.javaClass.enumConstants!!.size - 1 == this.ordinal
}