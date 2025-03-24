package com.example.todo.showtask_home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todo.util.ThemeManager

@Composable
fun SettingsScreen(viewModel: ShowTasksViewModel = hiltViewModel(), navController: NavController, navigateNext: (String) -> Unit = {}) {
    val context = LocalContext.current

    val isDarkModeEnabled by ThemeManager.isDarkMode

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.height(36.dp)
                        )
                    }
                    Text(
                        text = "Settings",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Enable Dark Mode",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = { isChecked ->
                        ThemeManager.setDarkMode(context, isChecked)
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isDarkModeEnabled) "Dark Mode Enabled" else "Dark Mode Disabled",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun YourAppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        ThemeManager.init(context)
    }
    val isDarkMode by ThemeManager.isDarkMode

    MaterialTheme(
        colorScheme = if (isDarkMode) {
            darkColorScheme()
        } else {
            lightColorScheme()
        },
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}


