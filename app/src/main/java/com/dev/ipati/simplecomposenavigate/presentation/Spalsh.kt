package com.dev.ipati.simplecomposenavigate.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dev.ipati.simplecomposenavigate.core.AppNavigator
import com.dev.ipati.simplecomposenavigate.core.HomeGraph
import com.dev.ipati.simplecomposenavigate.core.NavigateOption
import org.koin.androidx.compose.get

@Composable
fun Splash() {
    val appNavigator = get<AppNavigator>()
    BaseSplash(appNavigator)
}

@Composable
fun BaseSplash(appNavigator: AppNavigator? = null) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier
                        .padding(it),
                    text = "Splash Page",
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    appNavigator?.push(NavigateOption.DeepLink(HomeGraph.HomeDestination.deepLink))
                }) {
                    Text(text = "Go to home page")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLogin() {
    BaseSplash()
}