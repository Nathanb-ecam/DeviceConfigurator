package com.example.arduinobluetooth.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController


@SuppressLint("MissingPermission")
@Composable
fun BluetoothScreen(
    navController : NavHostController,
    blueViewModel : BluetoothViewModel = viewModel()
){
    val context = LocalContext.current

    val scannedDevices by blueViewModel.scannedDevices.collectAsState()
    Column(modifier = Modifier.padding(8.dp)){
        Row(){
            OutlinedButton(onClick = {
                blueViewModel.startScan(context = context)
            }) {
                Text(text="Scan for devices")

            }
            Button(onClick = {blueViewModel.deleteSearchResults()}){
                Text(text = "Delete searchs")
            }
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
        LazyColumn{
            items(scannedDevices){device->
                if (device.address == "C8:C9:A3:E6:64:92"){
                    blueViewModel.stopScan(context)
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
                        Column {
                            Text(text = device.name?:"No name")
                            Text(text = device.address?:"No adress")
                        }
                        Button(onClick = {blueViewModel.connectDevice(device)}){
                            Text(text="Configure")
                        }
                    }
                }



            }
        }



    }
}













