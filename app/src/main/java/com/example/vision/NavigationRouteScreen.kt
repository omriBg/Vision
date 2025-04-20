package com.example.vision

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRouteScreen(
    campus: String,
    classroom: String,
    selectedSeatId: Int,
    onBackPressed: () -> Unit,
    onContinue: () -> Unit
) {
    // מצב ניווט
    var navigationProgress by remember { mutableStateOf(0f) }
    var navigationComplete by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }
    var showSOSDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showDirectionsDialog by remember { mutableStateOf(true) }

    // רשימת הוראות ניווט
    val navigationSteps = remember {
        listOf(
            "צא מהכניסה הראשית וסע במסדרון הראשי",
            "פנה שמאלה במסדרון הבא",
            "המשך בדרך ישרה לעבר מתחם $campus",
            "פנה ימינה במעבר המרכזי",
            "התקדם לקומה השנייה במעלית המונגשת",
            "המשך במסדרון עד לכיתה $classroom"
        )
    }

    // אנימציית התקדמות פשוטה
    LaunchedEffect(Unit) {
        val totalTime = 60_000L // 60 שניות סך הכל
        val updateInterval = 100L // 100 מילישניות בכל עדכון
        val progressStep = 1f / (totalTime / updateInterval)

        while (navigationProgress < 1f) {
            delay(updateInterval)
            navigationProgress = (navigationProgress + progressStep).coerceAtMost(1f)

            // עדכון השלבים לפי ההתקדמות
            currentStep = when {
                navigationProgress < 0.15f -> 0
                navigationProgress < 0.3f -> 1
                navigationProgress < 0.5f -> 2
                navigationProgress < 0.7f -> 3
                navigationProgress < 0.85f -> 4
                else -> 5
            }

            // סיום הניווט
            if (navigationProgress >= 1f) {
                navigationComplete = true
                delay(500)
                showConfirmationDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ניווט נגיש") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזרה")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSOSDialog = true },
                containerColor = Color.Red,
                contentColor = Color.White,
                modifier = Modifier.shadow(4.dp, CircleShape)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "לחצן מצוקה",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // במקום Canvas - נציג מסך פשוט עם מידע על הניווט

            // כרטיסיית מידע
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "כיתה $classroom, מתחם $campus",
                                    fontWeight = FontWeight.Bold
                                )

                                if (selectedSeatId > 0) {
                                    Text(
                                        "כיסא מספר $selectedSeatId",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // פס התקדמות
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(navigationProgress)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (navigationComplete) "הגעת ליעד!"
                                else "זמן הגעה משוער: ${(5 * (1 - navigationProgress)).toInt()} דקות",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "${(navigationProgress * 100).toInt()}%",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // הנחיות ניווט נוכחיות
                if (!navigationComplete) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "שלב ${currentStep + 1}/${navigationSteps.size}",
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                navigationSteps[currentStep],
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // תצוגת מיקום נוכחי
            if (!navigationComplete) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    val locationTags = listOf(
                        "כניסה ראשית",
                        "מסדרון ראשי",
                        "מתחם הקרייה",
                        "מתחם $campus",
                        "מעלית קומה 2",
                        "כיתה $classroom"
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .width(IntrinsicSize.Max),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "מיקום נוכחי:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                locationTags[currentStep],
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // הודעת סיום
            if (navigationComplete) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "הגעת ליעד!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "כיתה $classroom, מתחם $campus",
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showConfirmationDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("אישור הגעה")
                        }
                    }
                }
            }
        }
    }

    // דיאלוג הנחיות
    if (showDirectionsDialog) {
        AlertDialog(
            onDismissRequest = { showDirectionsDialog = false },
            title = { Text("הנחיות הניווט") },
            text = {
                Column {
                    Text("המסלול הנגיש אל כיתה $classroom במתחם $campus מוכן!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• המסלול מותאם לצרכי הנגישות שציינת")
                    Text("• הקפד לעקוב אחר ההוראות בכל שלב")
                    Text("• לחצן SOS אדום זמין במקרה חירום")
                }
            },
            confirmButton = {
                Button(onClick = { showDirectionsDialog = false }) {
                    Text("התחל ניווט")
                }
            }
        )
    }

    // דיאלוג SOS
    if (showSOSDialog) {
        AlertDialog(
            onDismissRequest = { showSOSDialog = false },
            title = { Text("קריאה לעזרה") },
            text = {
                Text("האם אתה זקוק לעזרה? לחיצה על 'שלח קריאה' תשלח התראה לצוות הסיוע.")
            },
            confirmButton = {
                Button(
                    onClick = { showSOSDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("שלח קריאה")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSOSDialog = false }) {
                    Text("ביטול")
                }
            }
        )
    }

    // דיאלוג אישור הגעה
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("אישור הגעה") },
            text = {
                Column {
                    Text("האם הגעת לכיתה $classroom במתחם $campus?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("באפשרותך לחזור למסך הראשי או להמשיך למילוי משוב.")
                }
            },
            confirmButton = {
                Button(onClick = onContinue) {
                    Text("המשך למשוב")
                }
            },
            dismissButton = {
                TextButton(onClick = onBackPressed) {
                    Text("חזרה למסך הראשי")
                }
            }
        )
    }
}