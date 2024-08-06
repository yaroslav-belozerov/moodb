package com.yaabelozerov.moodb.presentation.screens.moodedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.model.MoodType
import com.yaabelozerov.moodb.data.icons.DualImageResource
import com.yaabelozerov.moodb.presentation.common.DualAsyncImage
import com.yaabelozerov.moodb.presentation.common.TopBar
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodEditAll(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    types: List<MoodType>,
    ic: Map<DefaultMoodType, DualImageResource>,
    onBack: () -> Unit,
    onChoose: (Int) -> Unit
) {
    val scroll = exitUntilCollapsedScrollBehavior()
    Scaffold(topBar = {
        TopBar(name = stringResource(id = R.string.edit_mood_types), scroll = scroll, onBack = onBack)
    }) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .nestedScroll(scroll.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(types) { type ->
                Row(
                    modifier = Modifier
                        .clickable {
                            onChoose(types.indexOf(type))
                        }
                        .fillParentMaxWidth()
                        .padding(16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ic[type.defaultMoodType]?.let {
                        DualAsyncImage(
                            imageModifier = Modifier.size(48.dp),
                            imageLoader = imageLoader,
                            dualIconResource = it
                        )
                    }
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = type.customName ?: stringResource(id = type.defaultMoodType.nameRes),
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}