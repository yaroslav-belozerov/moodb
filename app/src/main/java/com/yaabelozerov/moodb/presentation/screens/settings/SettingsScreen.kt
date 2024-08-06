package com.yaabelozerov.moodb.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.presentation.common.MenuRoute
import com.yaabelozerov.moodb.presentation.common.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, routes: List<MenuRoute>, onBack: () -> Unit) {
    val scroll = exitUntilCollapsedScrollBehavior()
    Scaffold(topBar = {
        TopBar(name = stringResource(id = R.string.settings), scroll = scroll, onBack = onBack)
    }) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            routes.map { route ->
                Row(modifier = Modifier
                    .clickable { route.fourth() }
                    .fillMaxWidth()
                    .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = route.first, contentDescription = null
                    )
                    Text(text = route.second)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = route.third, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}