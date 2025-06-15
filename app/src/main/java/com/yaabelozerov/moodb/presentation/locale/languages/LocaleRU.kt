package com.yaabelozerov.moodb.presentation.locale.languages

import cafe.adriel.lyricist.LyricistStrings
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.presentation.locale.Localization
import com.yaabelozerov.moodb.presentation.locale.LocaleNavigation
import com.yaabelozerov.moodb.presentation.locale.LocaleEdit
import com.yaabelozerov.moodb.presentation.locale.LocaleMoodCategory
import com.yaabelozerov.moodb.presentation.locale.LocaleSettings
import com.yaabelozerov.moodb.presentation.theme.ColorSchemes

@LyricistStrings(languageTag = "ru", default = false)
val LocalizationRU = Localization(
    appName = "MOODB",
    localeTag = "ru",
    localizedName = "Русский",
    navigation = LocaleNavigation(
        back = "Назад",
        next = "Далее",
        finish = "Готово",
        close = "Закрыть",
        cancel = "Отменить",
    ),
    welcomeTo = "Добро пожаловать в",
    chooseLanguage = "Выбери язык",
    chooseIconTheme = "Выбери тему",
    chooseColorTheme = "Выбери цвета",
    today = "Сегодня",
    edit = LocaleEdit(
        add = "Добавить",
        remove = "Удалить",
        save = "Сохранить",
    ),
    settings = LocaleSettings(
        settings = "Настройки",
        moodTypes = "Типы настроения",
        iconTheme = "Тема иконок",
        language = "Язык",
        colorTheme = "Цвета",
    ),
    mood = LocaleMoodCategory(
        happy = "Счастливый",
        energetic = "Энергичный",
        neutral = "Независимый",
        sad = "Грустный",
        angry = "Гневный",
    ),
    moodType = {
        when (it) {
            DefaultMoodType.HAPPY -> "Счастливый"
            DefaultMoodType.SAD -> "Грустный"
            DefaultMoodType.ANGRY -> "Гневный"
            DefaultMoodType.ANXIOUS -> "Тревожный"
            DefaultMoodType.UNCOMFY -> "Некомфортный"
            DefaultMoodType.DEPRESSED -> "Депрессивный"
            DefaultMoodType.CHILL -> "Чилл"
            DefaultMoodType.CALM -> "Спокойный"
            DefaultMoodType.BORED -> "Скучающий"
            DefaultMoodType.NUMB -> "Без эмоций"
            DefaultMoodType.CONFUSED -> "Запутанный"
            DefaultMoodType.GRATEFUL -> "Благодарный"
            DefaultMoodType.EXCITED -> "Радостный"
            DefaultMoodType.HOPEFUL -> "Надеющийся"
            DefaultMoodType.ANNOYED -> "Раздражённый"
            DefaultMoodType.OUTRAGED -> "Яростный"
        }
    }, theme = "Тема",
    youCanAddThemeLater = "Свою тему можно создать позже",
    colorTheme = {
        when (it) {
            ColorSchemes.Light -> "Светлая"
            ColorSchemes.Dark -> "Темная"
            ColorSchemes.Adapt -> "Авто"
        }
    }
)