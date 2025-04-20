package com.example.vision

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassSelectionScreen(
    selectedAccessibilityOptions: List<String>,
    instructorNotes: String,
    onBackPressed: () -> Unit,
    onFinalConfirmation: (String, String) -> Unit
) {
    val scrollState = rememberScrollState()

    // משתנים למעקב אחר הבחירות
    var selectedCampus by remember { mutableStateOf("") }
    var classNumber by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("בחירת מיקום") },
                navigationIcon = {
                    Button(
                        onClick = onBackPressed,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("חזרה")
                    }
                }
            )
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
            // הודעת אישור שהנתונים נשמרו
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
                        text = "יופי! הנתונים נשמרו",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "כעת נוכל להתאים עבורך את המסלול המדויק",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // כותרת בחירת מתחם
            Text(
                text = "בחר מתחם:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Start
            )

            // אפשרויות בחירת מתחם
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .fillMaxWidth()
            ) {
                // אפשרות פיקוס
                CampusOption(
                    title = "פיקוס",
                    description = "מתחם פיקוס - הבניין הראשי",
                    isSelected = selectedCampus == "פיקוס",
                    onClick = { selectedCampus = "פיקוס" }
                )

                // אפשרות קרייה
                CampusOption(
                    title = "קרייה",
                    description = "מתחם הקרייה - בניין החדשנות",
                    isSelected = selectedCampus == "קרייה",
                    onClick = { selectedCampus = "קרייה" }
                )
            }

            // שדה הזנת כיתה
            if (selectedCampus.isNotEmpty()) {
                Text(
                    text = "הזן מספר כיתה:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 8.dp),
                    textAlign = TextAlign.Start
                )

                OutlinedTextField(
                    value = classNumber,
                    onValueChange = { classNumber = it },
                    label = { Text("מספר כיתה") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // כפתור שמירה
                Button(
                    onClick = { showConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .height(50.dp),
                    enabled = classNumber.isNotEmpty()
                ) {
                    Text("שמור ועבור למסלול")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // דיאלוג אישור
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text(text = "אישור פרטים") },
            text = {
                Column {
                    Text("כעת נשמר לך מקום בכיתה ${classNumber} במתחם ${selectedCampus}")
                    Text("המרצה מעודכן בצרכים שלך.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("האם אפשר לצאת לדרך ולהתאים לך מסלול מדויק?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onFinalConfirmation(selectedCampus, classNumber)
                        showConfirmation = false
                    }
                ) {
                    Text("כן, צא לדרך!")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmation = false }
                ) {
                    Text("לא, חזור לעריכה")
                }
            }
        )
    }
}

@Composable
fun CampusOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = null
            )
        }
    }
}