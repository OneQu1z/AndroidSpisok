package com.example.spisok.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.data.ProductList
import com.example.spisok.ui.components.CreateListButton
import com.example.spisok.ui.components.NavigationDrawer
import com.example.spisok.ui.components.ProductListCard
import com.example.spisok.ui.theme.Dimens

/**
 * Главный экран со списком продуктов
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListsScreen(
    onMenuClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCreateListClick: () -> Unit = {},
    onListClick: (String) -> Unit = {},
    productLists: List<ProductList> = emptyList(),
    drawerState: DrawerState,
    onNotificationsClick: () -> Unit = {},
    onCategoriesClick: () -> Unit = {},
    onTemplatesClick: () -> Unit = {}
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                onNotificationsClick = onNotificationsClick,
                onCategoriesClick = onCategoriesClick,
                onTemplatesClick = onTemplatesClick,
                onDismiss = { onMenuClick() }
            )
        }
    ) {
        Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Список продуктов",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Меню"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Spacing16)
                    .padding(top = Dimens.Spacing8)
                    .offset(y = (-64).dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                CreateListButton(
                    onClick = onCreateListClick
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
            contentPadding = PaddingValues(vertical = Dimens.Spacing16)
        ) {
            items(productLists) { list ->
                ProductListCard(
                    listName = list.name,
                    onGoClick = { onListClick(list.id) }
                )
            }
        }
        }
    }
}

