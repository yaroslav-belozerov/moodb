package com.yaabelozerov.moodb.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class Four<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

data class MenuRoute(
    val icon: ImageVector,
    val name: String,
    val details: @Composable () -> Unit,
    val onClick: () -> Unit,
)