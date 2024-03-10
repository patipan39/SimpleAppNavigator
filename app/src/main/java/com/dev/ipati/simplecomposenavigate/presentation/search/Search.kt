package com.dev.ipati.simplecomposenavigate.presentation.search

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dev.ipati.simplecomposenavigate.core.AppNavigator
import org.koin.androidx.compose.get

@Composable
fun Search() {
    val appNavigator = get<AppNavigator>()
    BaseSearch(appNavigator)
}

@Composable
fun BaseSearch(appNavigator: AppNavigator? = null) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier
                        .padding(it),
                    text = "Search Page",
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    appNavigator?.pop()
                }) {
                    Text(text = "Back Button")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearch() {
    BaseSearch()
}