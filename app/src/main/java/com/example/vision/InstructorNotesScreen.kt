package com.example.vision

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorNotesScreen(
    selectedAccessibilityOptions: List<String>,
    onBackPressed: () -> Unit,
    onContinue: (List<String>, String) -> Unit
) {
    val scrollState = rememberScrollState()
    var instructorNotes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("דגשים למרצה") },
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
                        onContinue(selectedAccessibilityOptions, instructorNotes)
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
                text = "האם תרצה להוסיף דגשים למרצה?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "כאן תוכל לציין דגשים מיוחדים או בקשות שיועברו למרצה לפני השיעור.",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // תיבת טקסט גדולה להזנת דגשים
            OutlinedTextField(
                value = instructorNotes,
                onValueChange = { instructorNotes = it },
                label = { Text("דגשים למרצה") },
                placeholder = { Text("לדוגמה: אני זקוק בבקשה שתדבר בקול רם קרוב למושבים המונגשים...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )

            if (selectedAccessibilityOptions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "בהתאם לאפשרויות הנגישות שבחרת, אנו ממליצים לציין:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

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
                        // המלצות שונות בהתאם לאפשרויות שנבחרו
                        if (selectedAccessibilityOptions.contains("wheelchair")) {
                            SuggestionItem("צורך בנגישות לכיסא גלגלים וסידור מקומות הישיבה בכיתה")
                        }

                        if (selectedAccessibilityOptions.contains("visual")) {
                            SuggestionItem("העדפה לקבל חומרי לימוד מוגדלים או בפורמט דיגיטלי נגיש")
                        }

                        if (selectedAccessibilityOptions.contains("colorblind")) {
                            SuggestionItem("בקשה להימנע משימוש בשקפים המסתמכים על הבחנה בין צבעים")

                            // הצגת צבעים ספציפיים אם נבחרו
                            selectedAccessibilityOptions.forEach { option ->
                                if (option.startsWith("color_")) {
                                    val color = option.substring(6) // להסיר את הקידומת "color_"
                                    when (color) {
                                        "red" -> SuggestionItem("קושי בזיהוי צבע אדום", indented = true)
                                        "green" -> SuggestionItem("קושי בזיהוי צבע ירוק", indented = true)
                                        "blue" -> SuggestionItem("קושי בזיהוי צבע כחול", indented = true)
                                        "purple" -> SuggestionItem("קושי בזיהוי צבע סגול", indented = true)
                                    }
                                }
                            }
                        }

                        if (selectedAccessibilityOptions.contains("hearing")) {
                            SuggestionItem("בקשה להקפיד על דיבור ברור ו/או שימוש במיקרופון")
                        }

                        if (selectedAccessibilityOptions.contains("elderly")) {
                            SuggestionItem("צורך בהקצאת זמן נוסף בין השיעורים למעבר בין כיתות")
                        }

                        if (selectedAccessibilityOptions.contains("noise")) {
                            SuggestionItem("רגישות לרעשים חזקים או פתאומיים")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // מרווח בתחתית עבור ה-BottomBar
        }
    }
}

@Composable
fun SuggestionItem(text: String, indented: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (indented) 16.dp else 0.dp,
                bottom = 8.dp
            )
    ) {
        Text(
            text = if (indented) "- $text" else "• $text",
            fontSize = 14.sp
        )
    }
}