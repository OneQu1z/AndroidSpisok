package com.example.spisok.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spisok.ui.theme.Dimens

/**
 * Строка товара в списке
 */
@Composable
fun ProductItemRow(
    itemName: String,
    isPurchased: Boolean,
    isDeleteMode: Boolean,
    isMarkedForDeletion: Boolean,
    onPurchasedChange: (Boolean) -> Unit,
    onMarkForDeletion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Spacing4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isPurchased,
                onCheckedChange = onPurchasedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(modifier = Modifier.width(Dimens.Spacing8))
            
            Text(
                text = itemName,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isPurchased) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
        
        // Крестик для удаления (показывается в режиме удаления)
        if (isDeleteMode) {
            IconButton(
                onClick = onMarkForDeletion,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить",
                    tint = if (isMarkedForDeletion) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

