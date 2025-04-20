package com.example.vision

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    campus: String,
    classroom: String,
    onBackPressed: () -> Unit,
    onSubmitFeedback: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // משתני מצב עבור דירוגים
    var instructorRating by remember { mutableStateOf(0) }
    var accessibilityRating by remember { mutableStateOf(0) }
    var appRating by remember { mutableStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }

    // משתנה מצב לטעינה
    var isSubmitting by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    // הפעלת אנימציית טעינה בעת שליחת הפידבק
    LaunchedEffect(isSubmitting) {
        if (isSubmitting) {
            delay(1500) // מדמה שליחת נתונים
            isSubmitting = false
            isSubmitted = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("חוות דעת ומשוב") },
                navigationIcon = {
                    Button(
                        onClick = onBackPressed,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("חזרה")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Spacer(Modifier.weight(1f))
                if (!isSubmitted) {
                    Button(
                        onClick = {
                            isSubmitting = true
                        },
                        modifier = Modifier.padding(16.dp),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("שלח משוב")
                        }
                    }
                } else {
                    Button(
                        onClick = onSubmitFeedback,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("המשך")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // כותרת
            Text(
                text = "שתף את חוויתך!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )

            // הודעת אישור לאחר שליחה
            if (isSubmitted) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
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
                            text = "תודה על המשוב!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "המשוב שלך יעזור לנו לשפר את השירות ואת חוויית הנגישות במכללה.",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                // כרטיס פרטי המקום
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "כיתה $classroom - מתחם $campus",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                // דירוג המרצה
                FeedbackRatingCard(
                    title = "האם המרצה התחשב בצרכי הנגישות שלך?",
                    rating = instructorRating,
                    onRatingChanged = { instructorRating = it }
                )

                // דירוג הנגישות הפיזית
                FeedbackRatingCard(
                    title = "איך היית מדרג את רמת הנגישות בכיתה?",
                    rating = accessibilityRating,
                    onRatingChanged = { accessibilityRating = it }
                )

                // דירוג האפליקציה
                FeedbackRatingCard(
                    title = "כמה האפליקציה עזרה לך להגיע ליעד?",
                    rating = appRating,
                    onRatingChanged = { appRating = it }
                )

                // תיבת טקסט חופשי
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "הוסף הערות נוספות:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            placeholder = { Text("שתף את החוויה שלך, הצעות לשיפור...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            maxLines = 5
                        )
                    }
                }

                // הצעות לשיפור
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "האם יש לך הצעות ספציפיות לשיפור הנגישות?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { }
                            )

                            Text(
                                text = "יותר מקומות ישיבה מונגשים",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { }
                            )

                            Text(
                                text = "שיפור התאורה בכיתה",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { }
                            )

                            Text(
                                text = "שיפור האקוסטיקה",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { }
                            )

                            Text(
                                text = "שיפור הנגישות בין הכיתות",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun FeedbackRatingCard(
    title: String,
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // דירוג כוכבים
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (star in 1..5) {
                    StarRatingButton(
                        isSelected = star <= rating,
                        onClick = { onRatingChanged(star) }
                    )
                }
            }

            // טקסט דירוג
            Text(
                text = when (rating) {
                    0 -> "לא דורג"
                    1 -> "גרוע מאוד"
                    2 -> "לא טוב"
                    3 -> "סביר"
                    4 -> "טוב"
                    5 -> "מצוין"
                    else -> ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.Center,
                color = if (rating > 0) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}

@Composable
fun StarRatingButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Text(
            text = "★",
            fontSize = 32.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
        )
    }
}