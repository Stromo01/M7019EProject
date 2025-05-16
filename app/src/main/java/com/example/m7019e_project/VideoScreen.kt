package com.example.m7019e_project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.m7019e_project.ui.theme.DetailScreenViewmodel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun VideoScreen(navController: NavController, detailScreenViewmodel: DetailScreenViewmodel) {
    Column(
        modifier = Modifier
            .background(Color(0xFF153f69)).verticalScroll(rememberScrollState()),
    ) {
        Banner("Luleå", detailScreenViewmodel, navController)
        WebcamView("https://www2.lulea.se/web-camera/stadshuset/stadshus.jpg", "Luleå Stadshus")
        WebcamView("https://www2.lulea.se/web-camera/ormberget/imageorm.jpg", "Luleå Ormberget")
        WebcamView("https://webcam.vackertvader.se/39626801/latest.jpg", "Norra Sunderbyn")
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun WebcamView(imageUrl: String, description: String) {
    var updatedUrl by remember { mutableStateOf(imageUrl) }
    var imageLoadFailed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            updatedUrl = "$imageUrl?timestamp=${System.currentTimeMillis()}"
            kotlinx.coroutines.delay(60_000) // Wait for 1 minute
        }
    }

    Column {
        if (imageLoadFailed) {
            // Display a fallback message or placeholder when the image fails to load
            Text(
                text = "Image not found",
                color = Color.Red,
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color(0xFF1a4e82))
                    .padding(16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            AsyncImage(
                model = updatedUrl,
                contentDescription = description,
                alignment = Alignment.TopCenter,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .width(400.dp)
                    .padding(8.dp),
                onError = { imageLoadFailed = true } // Set flag if image fails to load
            )
        }
        Text(
            text = description,
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFF153f69))
                .padding(8.dp)
                .padding(bottom = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

}