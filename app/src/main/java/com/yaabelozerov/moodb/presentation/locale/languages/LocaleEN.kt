package com.yaabelozerov.moodb.presentation.locale.languages

import cafe.adriel.lyricist.LyricistStrings
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.presentation.locale.Localization
import com.yaabelozerov.moodb.presentation.locale.LocaleNavigation
import com.yaabelozerov.moodb.presentation.locale.LocaleEdit
import com.yaabelozerov.moodb.presentation.locale.LocaleMoodCategory
import com.yaabelozerov.moodb.presentation.locale.LocaleSettings
import com.yaabelozerov.moodb.presentation.theme.ColorSchemes

@LyricistStrings(languageTag = "en", default = true)
val LocalizationEN = Localization(
    appName = "MOODB",
    localeTag = "en",
    localizedName = "English",
    navigation = LocaleNavigation(
        back = "Back",
        next = "Next",
        finish = "Finish",
        close = "Close",
        cancel = "Cancel",
    ),
    welcomeTo = "Welcome to",
    chooseLanguage = "Choose language",
    chooseIconTheme = "Choose theme",
    chooseColorTheme = "Choose colors",
    today = "Today",
    edit = LocaleEdit(
        add = "Add",
        remove = "Remove",
        save = "Save",
    ),
    settings = LocaleSettings(
        settings = "Settings",
        moodTypes = "Mood Types",
        iconTheme = "Icon Theme",
        language = "Language",
        colorTheme = "Colors",
    ),
    mood = LocaleMoodCategory(
        happy = "Happy",
        energetic = "Energetic",
        neutral = "Neutral",
        sad = "Sad",
        angry = "Angry",
    ),
    moodType = {
        when (it) {
            DefaultMoodType.ANXIOUS -> "Anxious"
            DefaultMoodType.UNCOMFY -> "Uncomfy"
            DefaultMoodType.SAD -> "Sad"
            DefaultMoodType.DEPRESSED -> "Depressed"
            DefaultMoodType.CHILL -> "Chill"
            DefaultMoodType.CALM -> "Calm"
            DefaultMoodType.BORED -> "Bored"
            DefaultMoodType.NUMB -> "Numb"
            DefaultMoodType.CONFUSED -> "Confused"
            DefaultMoodType.GRATEFUL -> "Grateful"
            DefaultMoodType.HAPPY -> "Happy"
            DefaultMoodType.EXCITED -> "Excited"
            DefaultMoodType.HOPEFUL -> "Hopeful"
            DefaultMoodType.ANNOYED -> "Annoyed"
            DefaultMoodType.ANGRY -> "Angry"
            DefaultMoodType.OUTRAGED -> "Outraged"
        }
    },
    theme = "Theme",
    youCanAddThemeLater = "You can create your own later",
    colorTheme = {
        when (it) {
            ColorSchemes.Light -> "Light"
            ColorSchemes.Dark -> "Dark"
            ColorSchemes.Adapt -> "Adaptive"
        }
    }
)
