package com.example.vision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // הפעלת ערכת הנושא החדשה והמשופרת
            VisionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // העלאת אוסף הטיפים למסכי הסבר
                    val accessibilityTips = remember {
                        listOf(
                            "השתמש בכפתור SOS בכל זמן לקבלת עזרה",
                            "מסלולי הניווט מותאמים לצרכי הנגישות שהגדרת",
                            "ניתן לבטל אנימציות בהגדרות הנגישות",
                            "כל הפעולות מלוות בהסברים קוליים עבור קוראי מסך"
                        )
                    }

                    // מנהל ניהול העדפות נגישות
                    var accessibilityPrefs by remember {
                        mutableStateOf(AccessibilityPreferences())
                    }

                    AppNavigation(
                        accessibilityPreferences = accessibilityPrefs,
                        onUpdateAccessibilityPrefs = { accessibilityPrefs = it },
                        accessibilityTips = accessibilityTips
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    accessibilityPreferences: AccessibilityPreferences,
    onUpdateAccessibilityPrefs: (AccessibilityPreferences) -> Unit,
    accessibilityTips: List<String>
) {
    // מעקב אחר המסך הנוכחי עם אנימציה
    var currentScreen by remember { mutableStateOf("login") }
    var previousScreen by remember { mutableStateOf("") }
    var isAnimating by remember { mutableStateOf(false) }

    // הצגת טיפ יומי
    var showAccessibilityTip by remember { mutableStateOf(true) }
    var currentTipIndex by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    // שמירת האפשרויות שנבחרו במסך הנגישות
    var selectedAccessibilityOptions by remember { mutableStateOf(listOf<String>()) }

    // שמירת דגשים למרצה
    var instructorNotes by remember { mutableStateOf("") }

    // שמירת מקום ומתחם
    var selectedCampus by remember { mutableStateOf("") }
    var selectedClassroom by remember { mutableStateOf("") }

    // שמירת מספר כיסא - מאותחל ל-1 במקום -1
    var selectedSeatId by remember { mutableStateOf(1) }

    // פונקציית מעבר בין מסכים שתומכת באנימציה
    fun navigateTo(screen: String) {
        if (currentScreen != screen && !isAnimating) {
            isAnimating = true
            previousScreen = currentScreen
            currentScreen = screen

            // ביטול האנימציה אם נדרש לפי העדפות הנגישות
            if (accessibilityPreferences.reducedMotion) {
                isAnimating = false
            } else {
                coroutineScope.launch {
                    delay(300) // משך האנימציה
                    isAnimating = false
                }
            }
        }
    }

    // הצגת טיפ נגישות באופן אוטומטי
    LaunchedEffect(Unit) {
        delay(2000)
        currentTipIndex = (0 until accessibilityTips.size).random()
        showAccessibilityTip = true
        delay(5000)
        showAccessibilityTip = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // המסך הנוכחי עם אנימציה
        AnimatedVisibility(
            visible = !isAnimating,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            when (currentScreen) {
                "login" -> {
                    AfekaLoginScreen(
                        onLoginClick = { username, password ->
                            // בעתיד - לוגיקת כניסה עם שם משתמש וסיסמה
                            navigateTo("accessibility")
                        },
                        onGuestClick = {
                            // מעבר למסך בחירת אפשרויות נגישות
                            navigateTo("accessibility")
                        },
                        accessibilityPreferences = accessibilityPreferences,
                        onUpdateAccessibilityPrefs = onUpdateAccessibilityPrefs
                    )
                }
                "accessibility" -> {
                    AccessibilityScreen(
                        onBackPressed = {
                            // חזרה למסך הכניסה
                            navigateTo("login")
                        },
                        onContinue = { options ->
                            // שמירת האפשרויות שנבחרו ומעבר למסך דגשים למרצה
                            selectedAccessibilityOptions = options
                            navigateTo("instructor_notes")
                        }
                    )
                }
                "instructor_notes" -> {
                    InstructorNotesScreen(
                        selectedAccessibilityOptions = selectedAccessibilityOptions,
                        onBackPressed = {
                            // חזרה למסך הנגישות
                            navigateTo("accessibility")
                        },
                        onContinue = { options, notes ->
                            // שמירת הדגשים למרצה ומעבר למסך בחירת כיתה
                            selectedAccessibilityOptions = options
                            instructorNotes = notes
                            navigateTo("class_selection")
                        }
                    )
                }
                "class_selection" -> {
                    ClassSelectionScreen(
                        selectedAccessibilityOptions = selectedAccessibilityOptions,
                        instructorNotes = instructorNotes,
                        onBackPressed = {
                            // חזרה למסך הדגשים למרצה
                            navigateTo("instructor_notes")
                        },
                        onFinalConfirmation = { campus, classroom ->
                            // שמירת פרטי הכיתה ומעבר למסך בחירת כיסא
                            selectedCampus = campus
                            selectedClassroom = classroom
                            navigateTo("seat_selection")
                        }
                    )
                }
                "seat_selection" -> {
                    SeatSelectionScreen(
                        campus = selectedCampus,
                        classroom = selectedClassroom,
                        onBackPressed = {
                            // חזרה למסך בחירת כיתה
                            navigateTo("class_selection")
                        },
                        onContinue = { seatId ->
                            // שמירת מספר הכיסא ומעבר למסך הבית
                            selectedSeatId = seatId
                            navigateTo("home")
                        }
                    )
                }
                "navigation_route" -> {
                    NavigationRouteScreen(
                        campus = selectedCampus,
                        classroom = selectedClassroom,
                        selectedSeatId = selectedSeatId,
                        onBackPressed = {
                            // חזרה למסך הבית
                            navigateTo("home")
                        },
                        onContinue = {
                            // מעבר למסך משוב
                            navigateTo("feedback")
                        }
                    )
                }
                "feedback" -> {
                    FeedbackScreen(
                        campus = selectedCampus,
                        classroom = selectedClassroom,
                        onBackPressed = {
                            // חזרה למסך הניווט
                            navigateTo("navigation_route")
                        },
                        onSubmitFeedback = {
                            // חזרה למסך הבית
                            navigateTo("home")
                        }
                    )
                }
                "home" -> {
                    HomeScreen(
                        selectedOptions = selectedAccessibilityOptions,
                        instructorNotes = instructorNotes,
                        campus = selectedCampus,
                        classroom = selectedClassroom,
                        selectedSeatId = selectedSeatId,
                        onBackPressed = {
                            // חזרה למסך בחירת כיסא
                            navigateTo("seat_selection")
                        },
                        onNavigateToRoute = {
                            // מעבר למסך ניווט
                            navigateTo("navigation_route")
                        },
                        onNavigateToFeedback = {
                            // מעבר למסך משוב
                            navigateTo("feedback")
                        }
                    )
                }
            }
        }

        // הצגת טיפ נגישות
        AnimatedVisibility(
            visible = showAccessibilityTip && currentScreen == "login",
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "טיפ נגישות",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = accessibilityTips[currentTipIndex],
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { showAccessibilityTip = false }
                    ) {
                        Text("הבנתי")
                    }
                }
            }
        }
    }
}

@Composable
fun AfekaLoginScreen(
    onLoginClick: (String, String) -> Unit,
    onGuestClick: () -> Unit,
    accessibilityPreferences: AccessibilityPreferences,
    onUpdateAccessibilityPrefs: (AccessibilityPreferences) -> Unit
) {
    // משתנים לאחסון קלט המשתמש
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // כותרת "Vision" בסגול כהצגה של הלוגו
        Text(
            text = "Vision",
            fontSize = 32.sp,
            color = AfekaAccent, // סגול מערכת הנושא החדשה
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // כותרת "afeka" בירוק ובגדול
        Text(
            text = "afeka",
            fontSize = 48.sp,  // גודל גדול
            color = AfekaGreen,  // צבע ירוק
            fontWeight = FontWeight.Bold,  // מודגש
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // שדה שם משתמש
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("שם משתמש") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // שדה סיסמה
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("סיסמה") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // כפתור כניסה
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    onLoginClick(username, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("התחברות")
        }

        // קישור להרשמה
        TextButton(
            onClick = {
                // מעבר למסך הרשמה - לא מיושם עדיין
            }
        ) {
            Text(
                text = "אין לך חשבון? הירשם כאן",
                textAlign = TextAlign.Center
            )
        }

        // מרווח קטן
        Spacer(modifier = Modifier.height(8.dp))

        // כפתור כניסה כאורח
        OutlinedButton(
            onClick = onGuestClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("כניסה ללא חשבון")
        }

        // כפתור הגדרות נגישות
        TextButton(
            onClick = { showAccessibilityDialog = true },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("הגדרות נגישות")
            }
        }
    }

    // דיאלוג הגדרות נגישות
    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("הגדרות נגישות") },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ניגודיות גבוהה",
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = accessibilityPreferences.highContrast,
                            onCheckedChange = {
                                onUpdateAccessibilityPrefs(
                                    accessibilityPreferences.copy(highContrast = it)
                                )
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "טקסט מוגדל",
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = accessibilityPreferences.largeText,
                            onCheckedChange = {
                                onUpdateAccessibilityPrefs(
                                    accessibilityPreferences.copy(largeText = it)
                                )
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "הפחתת אנימציות",
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = accessibilityPreferences.reducedMotion,
                            onCheckedChange = {
                                onUpdateAccessibilityPrefs(
                                    accessibilityPreferences.copy(reducedMotion = it)
                                )
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAccessibilityDialog = false }) {
                    Text("אישור")
                }
            }
        )
    }
}