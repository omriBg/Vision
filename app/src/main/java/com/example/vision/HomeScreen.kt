package com.example.vision

import androidx.compose.foundation.BorderStroke
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
fun HomeScreen(
    selectedOptions: List<String>,
    instructorNotes: String,
    campus: String,
    classroom: String,
    selectedSeatId: Int,
    onBackPressed: () -> Unit,
    onNavigateToRoute: () -> Unit,
    onNavigateToFeedback: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showLocationSentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("מסלול נגיש") },
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
                Button(
                    onClick = onNavigateToRoute,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("הצג מסלול נגיש")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showEmergencyDialog = true },
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Text("SOS", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "המסלול שלך מוכן!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // כרטיסית מיקום
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "יעד הגעה:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "מתחם: $campus",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Text(
                                text = "כיתה: $classroom",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Text(
                                text = "מקום ישיבה: $selectedSeatId",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // כרטיסית הגדרות נגישות
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "התאמות נגישות:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (selectedOptions.isEmpty()) {
                        Text("לא נבחרו התאמות נגישות")
                    } else {
                        selectedOptions.forEach { option ->
                            if (!option.startsWith("color_")) {
                                Text(
                                    text = "• $option",
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // כרטיסית דגשים למרצה
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "דגשים למרצה:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (instructorNotes.isBlank()) {
                        Text("לא הוזנו דגשים למרצה")
                    } else {
                        Text(
                            text = instructorNotes,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // כרטיסית סיוע נוסף
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "אפשרויות נוספות:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onNavigateToRoute,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("מסלול נגיש")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = onNavigateToFeedback,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("מלא משוב")
                        }
                    }
                }
            }

            // לחצן מצוקה גדול
            OutlinedButton(
                onClick = { showEmergencyDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                ),
                border = BorderStroke(2.dp, Color.Red)
            ) {
                Text(
                    text = "לחצן מצוקה - שלח את מיקומך לסיוע",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // דיאלוג לחצן מצוקה
        if (showEmergencyDialog) {
            AlertDialog(
                onDismissRequest = { showEmergencyDialog = false },
                title = { Text("לחצן מצוקה") },
                text = {
                    Text(
                        "האם אתה זקוק לעזרה מיידית? לחיצה על 'שלח מיקום' תשלח התראה לצוות הסיוע של המכללה עם מיקומך המדויק."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showEmergencyDialog = false
                            coroutineScope.launch {
                                // מדמה שליחת מיקום
                                delay(1000)
                                showLocationSentDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("שלח מיקום")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showEmergencyDialog = false }
                    ) {
                        Text("ביטול")
                    }
                }
            )
        }

        // דיאלוג אישור שליחת מיקום
        if (showLocationSentDialog) {
            AlertDialog(
                onDismissRequest = { showLocationSentDialog = false },
                title = { Text("המיקום נשלח") },
                text = {
                    Column {
                        Text("המיקום שלך נשלח לצוות הסיוע של המכללה. איש צוות יצור איתך קשר בהקדם.")
                        Text("מיקום: $campus, כיתה $classroom", modifier = Modifier.padding(top = 8.dp))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showLocationSentDialog = false }
                    ) {
                        Text("הבנתי")
                    }
                }
            )
        }
    }
}