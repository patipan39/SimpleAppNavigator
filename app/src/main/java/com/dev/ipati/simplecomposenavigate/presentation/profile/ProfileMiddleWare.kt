package com.dev.ipati.simplecomposenavigate.presentation.profile

import com.dev.ipati.simplecomposenavigate.core.LoginGraph
import com.dev.ipati.simplecomposenavigate.core.NavigateOption
import com.dev.ipati.simplecomposenavigate.presentation.middleware.MiddleWare
import com.dev.ipati.simplecomposenavigate.presentation.middleware.MiddleWareResult

class ProfileMiddleWare : MiddleWare {
    private val isLogin: Boolean = false
    private val isAcceptPDPA: Boolean = true
    override fun requiredFeature(): MiddleWareResult {
        //add condition required feature
        return if (!isLogin) {
            MiddleWareResult.Next(NavigateOption.DeepLink(LoginGraph.LoginDestination.deepLink))
        } else if (!isAcceptPDPA) {
            MiddleWareResult.Next(NavigateOption.DeepLink("PDPA"))
        } else {
            MiddleWareResult.Resume
        }
    }
}
