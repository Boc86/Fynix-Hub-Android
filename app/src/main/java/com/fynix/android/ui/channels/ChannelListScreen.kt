package com.fynix.android.ui.channels

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fynix.android.data.NetworkRepository
import com.fynix.android.network.models.MergedChannel
import com.fynix.android.ui.components.FocusableCard

@Composable
fun ChannelListScreen(
    networkRepo: NetworkRepository,
    onChannelSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChannelListViewModel = viewModel(factory = ChannelListViewModelFactory(networkRepo))
) {
    val channels by viewModel.channels
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
    ) {
        items(channels) { channel ->
            FocusableCard(
                focused = false,
                onClick = { onChannelSelected(channel.id) }
            ) {
                androidx.compose.material3.CardContent {
                    androidx.compose.material3.Text(text = channel.name)
                }
            }
        }
    }
}
