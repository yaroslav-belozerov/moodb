package com.yaabelozerov.moodb.presentation.common

import androidx.compose.ui.graphics.vector.ImageVector

data class Four<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

typealias MenuRoute = Four<ImageVector, String, String, () -> Unit>