package com.example.vision

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Seat(
    val id: Int,
    val isAccessible: Boolean = false,
    val isReserved: Boolean = false,
    val isSelected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    campus: String,
    classroom: String,
    onBackPressed: () -> Unit,
    onContinue: (Int) -> Unit
) {
    val scrollState = rememberScrollState()

    // יצירת מצב הכיסאות - 3 מונגשים + 4 שמורים + רגילים
    var seats by remember {
        mutableStateOf(
            listOf(
                // שורה ראשונה - 8 כיסאות
                List(8) { index ->
                    Seat(
                        id = index + 1,
                        isAccessible = index in 1..3,  // כיסאות מונגשים בעמדות 2-4
                        isReserved = index in 5..8,    // כיסאות שמורים בעמדות 6-9
                        isSelected = false
                    )
                },
                // שורה שניה - 10 כיסאות רגילים
                List(10) { index ->
                    Seat(
                        id = index + 9,
                        isAccessible = false,
                        isReserved = false,
                        isSelected = false
                    )
                },
                // שורה שלישית - 10 כיסאות רגילים
                List(10) { index ->
                    Seat(
                        id = index + 19,
                        isAccessible = false,
                        isReserved = false,
                        isSelected = false
                    )
                }
            )
        )
    }

    // בחירת כיסא - מתחיל מ-1 ולא מ-0 או -1
    var selectedSeatId by remember { mutableStateOf(1) }

    // מידע על מספר הכיסא שנבחר
    var showSeatInfo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("בחירת מקום ישיבה") },
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
                    onClick = { onContinue(selectedSeatId) },
                    modifier = Modifier.padding(16.dp),
                    enabled = selectedSeatId > 0
                ) {
                    Text("שמור והמשך")
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
            Text(
                text = "בחירת מקום ישיבה",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )

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
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // מקרא
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem(
                            color = MaterialTheme.colorScheme.primary,
                            text = "כיסאות מונגשים"
                        )
                        LegendItem(
                            color = Color.Gray,
                            text = "כיסאות שמורים"
                        )
                        LegendItem(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            text = "כיסאות רגילים"
                        )
                    }
                }
            }

            // עמדת מרצה
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("עמדת מרצה", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // שורות כיסאות
            seats.forEachIndexed { rowIndex, rowSeats ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowSeats.forEachIndexed { seatIndex, seat ->
                        // יצירת מעבר באמצע השורה
                        if (rowIndex == 0 && seatIndex == 4) {
                            Spacer(modifier = Modifier.width(24.dp))
                        }

                        SeatItem(
                            seat = seat,
                            isSelected = seat.id == selectedSeatId,
                            onClick = {
                                if (!seat.isReserved) {
                                    // שינוי אופן עדכון הכיסאות - רק מעדכנים את ה-selectedSeatId
                                    // ללא עדכון מיותר של רשימת הכיסאות המלאה
                                    selectedSeatId = seat.id
                                    showSeatInfo = true
                                }
                            }
                        )
                    }
                }
            }

            // הודעה על בחירת כיסא
            if (showSeatInfo && selectedSeatId > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
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
                            text = "בחרת בכיסא מספר $selectedSeatId",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        val isAccessible = seats.flatten().find { it.id == selectedSeatId }?.isAccessible ?: false
                        if (isAccessible) {
                            Text(
                                text = "זהו כיסא מונגש",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                text = "זהו כיסא רגיל",
                                modifier = Modifier.padding(top = 4.dp)
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
fun SeatItem(
    seat: Seat,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        seat.isReserved -> Color.Gray
        seat.isAccessible -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        isSelected -> Color.White
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(enabled = !seat.isReserved) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = seat.id.toString(),
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp)
    }
}