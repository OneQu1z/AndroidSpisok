package com.example.spisok.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.ui.theme.Dimens

/**
 * Боковое меню навигации
 */
@Composable
fun NavigationDrawer(
    onNotificationsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onTemplatesClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
        ) {
            // Пункт меню "Уведомления"
            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Уведомления",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                selected = false,
                onClick = {
                    onNotificationsClick()
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Пункт меню "Категории"
            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Категории",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                selected = false,
                onClick = {
                    onCategoriesClick()
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Пункт меню "Шаблоны"
            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Шаблоны",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                selected = false,
                onClick = {
                    onTemplatesClick()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

