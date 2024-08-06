package com.yaabelozerov.moodb.data.locale

import java.util.Locale

sealed class LocaleList {

    companion object {
        val builtin = listOf(Locale.ENGLISH, Locale("ru", "RU"))
    }
}