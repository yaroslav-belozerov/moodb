package com.yaabelozerov.moodb.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.model.ThemeList
import java.util.Locale

@Composable
fun WelcomeLanguage(
    modifier: Modifier = Modifier, locales: List<Locale>, onSetLocale: (String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var selecting by remember {
            mutableStateOf(false)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.welcometo),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                textAlign = TextAlign.Center,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
            Text(text = stringResource(id = R.string.language), fontSize = 24.sp)
            Card(onClick = {
                selecting = true
            }) {
                DropdownMenu(expanded = selecting, onDismissRequest = { selecting = false }) {
                    locales.map {
                        Text(modifier = Modifier
                            .clickable {
                                selecting = false
                                onSetLocale(it.toLanguageTag()  )
                            }
                            .fillMaxWidth()
                            .padding(16.dp),
                            text = it.displayName.replaceFirstChar { char -> char.uppercase() },
                            fontSize = 24.sp
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = Locale.getDefault().displayLanguage.replaceFirstChar { it.uppercase() },
                        fontSize = 24.sp
                    )
                    Icon(
                        imageVector = if (selecting) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
        }
    }
}