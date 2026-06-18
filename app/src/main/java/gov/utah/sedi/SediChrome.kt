package gov.utah.sedi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class OnboardingPhase(val label: String, val chipLabel: String, val stepRange: IntRange) {
    Request("Request", "Request", 1..2),
    SecureWallet("Secure wallet", "Wallet", 3..4),
    ConfirmPresence("Confirm presence", "Presence", 5..11),
    FinishSetup("Finish setup", "Finish", 12..15);

    companion object {
        fun forStep(step: Int): OnboardingPhase =
            entries.firstOrNull { step in it.stepRange } ?: FinishSetup
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingPhaseStrip(step: Int) {
    val active = OnboardingPhase.forStep(step)
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        OnboardingPhase.entries.forEach { phase ->
            val isActive = phase == active
            val isComplete = phase.stepRange.last < step
            Surface(
                color = when {
                    isActive -> StateBlue.copy(alpha = 0.12f)
                    isComplete -> Color(0xFF197A56).copy(alpha = 0.1f)
                    else -> Color(0xFFE8EEF3)
                },
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    phase.chipLabel,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    color = when {
                        isActive -> StateBlue
                        isComplete -> Color(0xFF197A56)
                        else -> Slate
                    }
                )
            }
        }
    }
}

data class TabDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val mainTabDestinations = listOf(
    TabDestination("wallet", "Wallet", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    TabDestination("requests", "Requests", Icons.Filled.Inbox, Icons.Outlined.Inbox),
    TabDestination("institutions", "Institutions", Icons.Filled.Apartment, Icons.Outlined.Apartment),
    TabDestination("activity", "Activity", Icons.Filled.History, Icons.Outlined.History)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScaffold(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            color = Ink,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (subtitle != null) {
                            Text(
                                subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Ink
                )
            )
        }
    ) { padding ->
        SediScreenBackdrop {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = SediVerticalRhythm.screenHorizontal)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(SediVerticalRhythm.tabTopBreathing))
                content()
                Spacer(Modifier.height(SediVerticalRhythm.focusBreathing))
            }
        }
    }
}

@Composable
fun ReassuranceLine(
    text: String = SediBrand.Copy.APPROVED_ONLY,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = TrustBlue,
        fontWeight = FontWeight.Medium
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrivacySplitPanel(
    shared: List<String>,
    hidden: List<String>,
    sharedLabel: String = "Shared",
    hiddenLabel: String = "Hidden"
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(sharedLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Ink)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                shared.forEach { item ->
                    PrivacyChip(label = item, tone = SediBrand.Success, positive = true)
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(hiddenLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Ink)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                hidden.forEach { item ->
                    PrivacyChip(label = item, tone = Slate, positive = false)
                }
            }
        }
    }
}

@Composable
private fun PrivacyChip(label: String, tone: Color, positive: Boolean) {
    Surface(
        color = tone.copy(alpha = if (positive) 0.12f else 0.08f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (positive) "✓" else "—",
                color = tone,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(6.dp))
            Text(label, color = Ink, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScaffold(
    title: String,
    onBack: () -> Unit,
    primaryLabel: String? = null,
    onPrimary: (() -> Unit)? = null,
    primaryEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Mist,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CardWhite,
                    titleContentColor = Ink
                )
            )
        },
        bottomBar = {
            if (primaryLabel != null && onPrimary != null) {
                SediActionSurface(
                    primaryLabel = primaryLabel,
                    onPrimary = onPrimary,
                    primaryEnabled = primaryEnabled,
                    elevated = true
                )
            }
        }
    ) { padding ->
        SediScreenBackdrop {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = SediVerticalRhythm.screenHorizontal)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(Modifier.height(SediVerticalRhythm.detailTopBreathing))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    content = content
                )
                Spacer(Modifier.height(SediVerticalRhythm.focusBreathing))
            }
        }
    }
}

@Composable
fun InvitationHeroCard(
    institutionName: String,
    headline: String,
    detail: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(24.dp)
                .background(
                    Brush.radialGradient(
                        listOf(StateBlue.copy(alpha = 0.18f), Color.Transparent)
                    )
                )
                .align(Alignment.BottomCenter)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = StateBlue,
            shadowElevation = 12.dp
        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InstitutionAvatarChip(institutionName)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        institutionName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "Verification request",
                        color = Color.White.copy(alpha = 0.72f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                headline,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if (detail.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    detail,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        }
    }
}

@Composable
private fun InstitutionAvatarChip(name: String) {
    val initials = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.16f)
    ) {
        Text(
            initials,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
