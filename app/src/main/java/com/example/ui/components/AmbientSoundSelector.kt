package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.audio.AmbientSoundType

@Composable
fun AmbientSoundSelector(
    selectedSound: AmbientSoundType,
    onSoundSelected: (AmbientSoundType) -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ambient_sound_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Âm Thanh Tập Trung",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                AnimatedVisibility(visible = isPlaying && selectedSound != AmbientSoundType.OFF) {
                    Text(
                        text = "● Đang phát",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AmbientSoundType.values().forEach { sound ->
                    val isSelected = selectedSound == sound
                    val icon: ImageVector = when (sound) {
                        AmbientSoundType.OFF -> Icons.Default.VolumeOff
                        AmbientSoundType.RAIN -> Icons.Default.WaterDrop
                        AmbientSoundType.BINAURAL -> Icons.Default.Waves
                        AmbientSoundType.WHITE_NOISE -> Icons.Default.GraphicEq
                        AmbientSoundType.CAFE -> Icons.Default.Coffee
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { onSoundSelected(sound) },
                        label = { Text(sound.title) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = sound.title,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("sound_chip_${sound.name}")
                    )
                }
            }
        }
    }
}
