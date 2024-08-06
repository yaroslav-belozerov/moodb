package com.yaabelozerov.moodb.presentation.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import com.yaabelozerov.moodb.data.icons.DualImageResource
import java.io.File
import kotlin.math.min

@Composable
fun DualAsyncImage(
    imageModifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    dualIconResource: DualImageResource
) {
    var imageSize by remember {
        mutableFloatStateOf(0f)
    }
    if (dualIconResource.filePath != null) {
        SubcomposeAsyncImage(modifier = imageModifier
            .onGloballyPositioned {
                imageSize = min(it.size.height.toFloat(), it.size.width.toFloat()) / 2
            }
            .clip(RoundedCornerShape(dualIconResource.rounding * imageSize)),
            model = File(dualIconResource.filePath),
            contentDescription = null,
            imageLoader = imageLoader)
    } else if (dualIconResource.resId != null) {
        SubcomposeAsyncImage(
            modifier = imageModifier,
            model = dualIconResource.resId,
            contentDescription = null,
            imageLoader = imageLoader
        )
    }
}