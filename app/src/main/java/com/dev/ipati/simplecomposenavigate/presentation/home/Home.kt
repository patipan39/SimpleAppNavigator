package com.dev.ipati.simplecomposenavigate.presentation.home

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
import com.dev.ipati.simplecomposenavigate.core.ModalBottomCalendar
import com.dev.ipati.simplecomposenavigate.core.NavigateOption
import org.koin.androidx.compose.get
import java.util.Calendar

@Composable
fun Home() {
    val appNavigator = get<AppNavigator>()
    BaseHome(appNavigator)
}

@Composable
fun BaseHome(
    appNavigator: AppNavigator? = null
) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ModalBottomCalendar("ระบุวันเกิด",
                    inputCalendar = Calendar.getInstance(),
                    onDismissCallBack = {
//                        dismiss()
                    },
                    onDateSelectedCallBack = {
//                        listener?.onDateSelectedCallBack(it)
                    }
                )
                Text(
                    modifier = Modifier
                        .padding(it),
                    text = "Home Page",
                    textAlign = TextAlign.Center
                )

                Button(onClick = {
                    appNavigator?.push(NavigateOption.DeepLink(LoginGraph.LoginDestination.deepLink))
                }) {
                    Text(text = "Navigate Login Page")
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