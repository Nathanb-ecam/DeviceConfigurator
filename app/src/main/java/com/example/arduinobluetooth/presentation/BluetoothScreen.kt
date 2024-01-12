package com.example.arduinobluetooth.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController


@Composable
fun BluetoothScreen(
    navController : NavHostController,
    blueViewModel : BluetoothViewModel = viewModel()
){
    val context = LocalContext.current

    val scannedDevice : List<BluetoothDevice> = mutableListOf<BluetoothDevice>()
    val blueUIState by blueViewModel.uiState.collectAsState()
    Column{
          Button(onClick = {
              blueViewModel.startScan(context = context)
          }) {
              Text(text="Start Scan ble")

          }
          Button(onClick = { blueViewModel.stopScan(context = context) }) {
              Text(text="Stop Scan ble")
          }
    }


}











