package com.example.ui.screens.emergency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitType
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.MintAccent
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun EmergencyCravingScreen(
    habits: List<HabitType>,
    onClose: () -> Unit,
    onOvercomeSuccess: (
        habit: HabitType,
        trigger: String,
        action: String,
        intensity: Int,
        durationSeconds: Int,
        note: String
    ) -> Unit
) {
    var selectedHabit by remember { mutableStateOf(habits.firstOrNull() ?: HabitType.SMOKING) }
    var selectedAction by remember { mutableStateOf("Breathe for 60 seconds") }
    var cravingIntensity by remember { mutableIntStateOf(3) }
    var triggerNote by remember { mutableStateOf("Sudden urge / stress wave") }
    var reflectionNote by remember { mutableStateOf("") }

    // Timer State
    var timerRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(60) }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (secondsLeft > 0 && timerRunning) {
                delay(1000L)
                secondsLeft--
            }
            if (secondsLeft <= 0) {
                isCompleted = true
                timerRunning = false
            }
        }
    }

    // Breathing Animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )

    val breathPhase = if (breathScale > 0.95f) "Hold..." else if (breathScale > 0.85f) "Inhale..." else "Exhale..."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = TealPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Emergency Support",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TealPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_craving_screen")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main comforting title
            Text(
                text = "Pause. Breathe.\nThis feeling will pass.",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cravings typically crest and disappear within 3 to 5 minutes. You only need to win this single moment.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Calming Breathing Visualizer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(190.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer glowing pulse ring
                        Canvas(modifier = Modifier.size(170.dp * breathScale)) {
                            drawCircle(
                                color = MintAccent.copy(alpha = 0.22f)
                            )
                        }

                        // Medium pulse ring
                        Canvas(modifier = Modifier.size(140.dp * breathScale)) {
                            drawCircle(
                                color = TealPrimaryLight.copy(alpha = 0.35f)
                            )
                        }

                        // Inner solid breathing circle
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(TealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (timerRunning) "${secondsLeft}s" else breathPhase,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!timerRunning && !isCompleted) {
                        Button(
                            onClick = {
                                timerRunning = true
                                secondsLeft = 60
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier.testTag("start_breathing_timer")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start 60s Breathing Timer", fontWeight = FontWeight.SemiBold)
                        }
                    } else if (timerRunning) {
                        Text(
                            text = "Follow the rhythm. Inhale deeply, hold gently, and exhale fully.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = null, tint = SageGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "60 seconds completed! You did it.",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SageGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Action Strategies
            Text(
                text = "Quick Grounding Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val quickActions = listOf(
                Pair("🌬️ Breathe for 60 seconds", "Center nervous system with box breathing"),
                Pair("💧 Drink a cold glass of water", "Shock the physical craving receptor loop"),
                Pair("🚶 Take a 5-minute walk", "Change your immediate physical environment"),
                Pair("💪 Do 20 push-ups or stretch", "Release instant endorphins and energy"),
                Pair("💬 Message or call someone", "Break the isolation of the urge"),
                Pair("📝 Write down why I started", "Re-anchor in your long-term commitment")
            )

            quickActions.forEach { (actionTitle, actionDesc) ->
                val isSelected = selectedAction == actionTitle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedAction = actionTitle }
                        .testTag("action_$actionTitle"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) TealPrimary.copy(alpha = 0.08f) else SurfaceWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) TealPrimary else SurfaceBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = actionTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                ),
                                color = if (isSelected) TealPrimary else TextPrimary
                            )
                            Text(
                                text = actionDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        if (isSelected) {
                            Surface(
                                shape = CircleShape,
                                color = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.padding(3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Craving Intensity Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Craving Intensity (1-5)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..5).forEach { level ->
                            val isSel = cravingIntensity == level
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) TealPrimary else SurfaceSubtle)
                                    .clickable { cravingIntensity = level }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$level",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reflection Note Input
            OutlinedTextField(
                value = reflectionNote,
                onValueChange = { reflectionNote = it },
                label = { Text("What triggered this? (Optional)") },
                placeholder = { Text("e.g., Felt tired after work, scrolling late night...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("craving_reflection_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite,
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = SurfaceBorder
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Finish & Overcome CTA Button
            Button(
                onClick = {
                    onOvercomeSuccess(
                        selectedHabit,
                        triggerNote,
                        selectedAction,
                        cravingIntensity,
                        60 - secondsLeft.coerceAtLeast(0),
                        reflectionNote
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("craving_overcome_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
            ) {
                Text(
                    text = "I Made It Through The Moment 💪",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
