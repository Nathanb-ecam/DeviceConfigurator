package com.example.arduinobluetooth.presentation.appscreens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arduinobluetooth.presentation.BluetoothViewModel
import com.example.arduinobluetooth.presentation.LoginViewModel
import com.icure.sdk.api.IcureApi
import com.icure.sdk.auth.UsernamePassword
import com.icure.sdk.storage.impl.VolatileStorageFacade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


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



