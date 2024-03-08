package com.dev.ipati.simplecomposenavigate.presentation

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
import com.dev.ipati.simplecomposenavigate.core.LoginGraph
import com.dev.ipati.simplecomposenavigate.core.NavigateOption
import org.koin.androidx.compose.get

@Composable
fun Home() {
    val appNavigator = get<AppNavigator>()
    BaseHome(appNavigator)
}

@Composable
fun BaseHome(appNavigator: AppNavigator? = null) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier
                        .padding(it),
                    text = "Home Page",
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    appNavigator?.push(NavigateOption.Route(LoginGraph.LoginDestination.route))
                }) {
                    Text(text = "Navigate Route")
                }

                Button(onClick = {
                    appNavigator?.push(NavigateOption.DeepLink(LoginGraph.LoginDestination.deepLink))
                }) {
                    Text(text = "Navigate DeepLink")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPage1() {
    BaseHome()
}