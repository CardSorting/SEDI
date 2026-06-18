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
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
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
        containerColor = Mist,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(title, fontWeight = FontWeight.Bold, color = Ink)
                        if (subtitle != null) {
                            Text(
                                subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Mist,
                    titleContentColor = Ink
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
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

@Composable
fun PrivacySplitPanel(
    shared: List<String>,
    hidden: List<String>,
    sharedLabel: String = "Shared",
    hiddenLabel: String = "Hidden"
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(sharedLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Ink)
            Spacer(Modifier.height(6.dp))
            shared.forEach { item ->
                PrivacySplitRow(item, SediBrand.Success)
            }
            Spacer(Modifier.height(10.dp))
            Text(hiddenLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Ink)
            Spacer(Modifier.height(6.dp))
            hidden.forEach { item ->
                PrivacySplitRow(item, Slate)
            }
        }
    }
}

@Composable
private fun PrivacySplitRow(label: String, tone: Color) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(tone, RoundedCornerShape(999.dp))
        )
        Spacer(Modifier.width(8.dp))
        Text(label, color = Ink, style = MaterialTheme.typography.bodyMedium)
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
                Surface(color = CardWhite, tonalElevation = 4.dp) {
                    androidx.compose.material3.Button(
                        onClick = onPrimary,
                        enabled = primaryEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(primaryLabel, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
fun InvitationHeroCard(
    institutionName: String,
    headline: String,
    detail: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = StateBlue
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
