package com.fynix.android.ui.channels

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fynix.android.data.NetworkRepository
import com.fynix.android.network.models.MergedChannel

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
            ChannelCard(
                channel = channel,
                onClick = { onChannelSelected(channel.id) }
            )
        }
    }
}

@Composable
fun ChannelCard(
    channel: MergedChannel,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
        onClick = onClick
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = channel.name
        )
        androidx.compose.material3.Text(text = channel.name)
    }
}
