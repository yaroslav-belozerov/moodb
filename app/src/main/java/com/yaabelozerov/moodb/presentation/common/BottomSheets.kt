package com.yaabelozerov.moodb.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.di.MainApplication
import com.yaabelozerov.moodb.presentation.locale.AvailableLocalizations
import com.yaabelozerov.moodb.presentation.locale.setLocaleTag
import com.yaabelozerov.moodb.presentation.theme.ColorSchemes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageChoiceSheet(visible: Boolean, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    if (visible) ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Language, contentDescription = null)
                Text(
                    LocalStrings.current.chooseLanguage,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            AvailableLocalizations.entries.map {
                TextButton(
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        scope.launch {
                            onDismiss()
                            setLocaleTag(it.localization.localeTag)
                        }
                    }, shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = it.localization.localizedName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeChoiceSheet(isVisible: Boolean, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    if (isVisible) ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.InvertColors, contentDescription = null)
                Text(
                    LocalStrings.current.chooseColorTheme,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            ColorSchemes.entries.map {
                TextButton(
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        scope.launch {
                            onDismiss()
                            MainApplication.dataStoreManager.set(SK.Theme, it.key)
                        }
                    }, shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = LocalStrings.current.colorTheme(it),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
