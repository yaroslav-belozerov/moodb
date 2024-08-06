package com.yaabelozerov.moodb.presentation.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    name: String,
    scroll: TopAppBarScrollBehavior,
    onBack: (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(title = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = name,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }, scrollBehavior = scroll, navigationIcon = {
        onBack?.let {
            IconButton(onClick = { it() }) {
                Icon(
                    imageVector = Icons.Default.Close, contentDescription = null
                )
            }
        }
    }, actions = actions)
}