package com.example.arduinobluetooth.presentation.appscreens


import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arduinobluetooth.presentation.viewmodels.LoginViewModel


@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun HelpScreen(
    navController: NavController,
    loginViewModel : LoginViewModel = viewModel()

) {

/*    LaunchedEffect("apiInit"){
        coroutineScope {
            *//*loginViewModel.apiInit()*//*
            loginViewModel.apiInitialize("18092@ecam.be", "77811dcf-1846-4ce4-89c4-cffcdc822657")
            loginViewModel.getDeviceConfigData()
        }
    }*/






}



