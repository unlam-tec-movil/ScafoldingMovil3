package ar.edu.unlam.mobile.scaffolding.ui.screens.userPosts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.posts.PostCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyReportsScreen() {
    val viewModel: MyPetsViewModel = hiltViewModel()
    val foundPosts by viewModel.foundPosts.collectAsState(initial = emptyList())

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier =
            Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            foundPosts,
            key = { it.id },
        ) { pet ->
            PostCard(pet = pet)
        }
    }
}
