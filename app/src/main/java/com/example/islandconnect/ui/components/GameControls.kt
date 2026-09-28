package com.example.islandconnect.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameControls(
    canRotate: Boolean,
    canUndo: Boolean,
    onRotate: () -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Return to menu",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo button
            FilledTonalButton(
                onClick = onUndo,
                enabled = canUndo,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp),
                modifier = Modifier.height(42.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = null,
                    modifier = Modifier.width(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Undo")
            }

            // Restart button
            FilledTonalButton(
                onClick = onRestart,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp),
                modifier = Modifier.height(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.width(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Restart")
            }

            // Rotate button (primary tactical action)
            Button(
                onClick = onRotate,
                enabled = canRotate,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(42.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.RotateRight,
                    contentDescription = null,
                    modifier = Modifier.width(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rotate")
            }
        }
    }
}
