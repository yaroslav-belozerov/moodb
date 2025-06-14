package com.yaabelozerov.moodb.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.di.MainApplication
import com.yaabelozerov.moodb.presentation.locale.AvailableLocalizations
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageChoiceSheet(sheetState: SheetState) {
    val scope = rememberCoroutineScope()
    if (sheetState.isVisible) ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { scope.launch { sheetState.hide() } },
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
                Icon(Icons.Default.LocationOn, contentDescription = null)
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
                            sheetState.hide()
                            MainApplication.dataStoreManager.set(
                                SK.LocaleTag, it.localization.localeTag
                            )
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