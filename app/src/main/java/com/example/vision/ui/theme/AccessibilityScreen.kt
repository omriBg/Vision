package com.example.vision

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AccessibilityOption(
    val id: String,
    val title: String,
    val description: String
)

data class ColorOption(
    val id: String,
    val title: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityScreen(onBackPressed: () -> Unit, onContinue: (List<String>) -> Unit) {
    val scrollState = rememberScrollState()

    // רשימת אפשרויות הנגשה
    val accessibilityOptions = listOf(
        AccessibilityOption(
            id = "wheelchair",
            title = "נגישות לכיסא גלגלים",
            description = "מסלולים נגישים לכיסאות גלגלים ואנשים עם מוגבלות בניידות"
        ),
        AccessibilityOption(
            id = "visual",
            title = "לקויות ראייה",
            description = "התאמות לאנשים עם עיוורון או לקות ראייה"
        ),
        AccessibilityOption(
            id = "colorblind",
            title = "עיוורון צבעים",
            description = "התאמות צבעים לאנשים עם עיוורון צבעים"
        ),
        AccessibilityOption(
            id = "hearing",
            title = "לקויות שמיעה",
            description = "התאמות לאנשים עם חירשות או לקויות שמיעה"
        ),
        AccessibilityOption(
            id = "stroller",
            title = "עגלת תינוק",
            description = "מסלולים נגישים לעגלות תינוק"
        ),
        AccessibilityOption(
            id = "elderly",
            title = "מגבלה בהליכה",
            description = " הימנעות ממדרגות ומדרכים מסובכות"
        ),
        AccessibilityOption(
            id = "noise",
            title = "רגישות לרעש",
            description = "התאמות לאנשים עם רגישות לרעש"
        ),
    )

    // רשימת אפשרויות צבעים להימנעות
    val colorOptions = listOf(
        ColorOption(
            id = "red",
            title = "אדום",
            color = Color.Red
        ),
        ColorOption(
            id = "green",
            title = "ירוק",
            color = Color.Green
        ),
        ColorOption(
            id = "blue",
            title = "כחול",
            color = Color.Blue
        ),
        ColorOption(
            id = "purple",
            title = "סגול",
            color = Color(0xFF800080)
        )
    )

    // מעקב אחר אפשרויות שנבחרו
    val selectedOptions = remember { mutableStateOf(setOf<String>()) }
    // מעקב אחר צבעים שנבחרו להימנעות
    val selectedColors = remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("הגדרות נגישות") },
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
                    onClick = {
                        // כולל את כל האופציות ואת הצבעים שנבחרו
                        val allOptions = selectedOptions.value.toMutableList()
                        selectedColors.value.forEach { color ->
                            allOptions.add("color_$color")
                        }
                        onContinue(allOptions)
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("המשך")
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
                text = "אנא בחר את ההתאמות הנדרשות עבורך",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "האפליקציה תותאם לצרכים שתבחר. ניתן לבחור מספר אפשרויות.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // רשימת האפשרויות
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .fillMaxWidth()
            ) {
                accessibilityOptions.forEach { option ->
                    val isSelected = selectedOptions.value.contains(option.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    selectedOptions.value = if (isSelected) {
                                        selectedOptions.value - option.id
                                    } else {
                                        selectedOptions.value + option.id
                                    }

                                    // אם ביטלנו בחירה של עיוורון צבעים, ננקה גם את בחירת הצבעים
                                    if (option.id == "colorblind" && isSelected) {
                                        selectedColors.value = emptySet()
                                    }
                                },
                                role = Role.Checkbox
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = option.description,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }

                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null
                                )
                            }

                            // הצגת אפשרויות צבעים אם נבחר עיוורון צבעים
                            if (option.id == "colorblind" && isSelected) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider()
                                Text(
                                    text = "בחר צבעים להימנעות:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Column {
                                    colorOptions.forEach { colorOption ->
                                        val isColorSelected = selectedColors.value.contains(colorOption.id)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .selectable(
                                                    selected = isColorSelected,
                                                    onClick = {
                                                        selectedColors.value = if (isColorSelected) {
                                                            selectedColors.value - colorOption.id
                                                        } else {
                                                            selectedColors.value + colorOption.id
                                                        }
                                                    },
                                                    role = Role.Checkbox
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // צבע דוגמה
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .padding(end = 12.dp)
                                                    .background(
                                                        color = colorOption.color,
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                            )

                                            Text(
                                                text = colorOption.title,
                                                modifier = Modifier.weight(1f)
                                            )

                                            Checkbox(
                                                checked = isColorSelected,
                                                onCheckedChange = null
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp)) // מרווח בתחתית עבור ה-BottomBar
            }
        }
    }
}