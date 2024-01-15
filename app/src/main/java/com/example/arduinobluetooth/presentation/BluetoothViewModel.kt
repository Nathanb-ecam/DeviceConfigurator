package com.example.arduinobluetooth.presentation

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduinobluetooth.data.BluetoothController


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

import kotlin.text.Typography.dagger



class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList()

)


class BluetoothViewModel (
    private val bluetoothController: BluetoothController
): ViewModel() {

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())

    // Public StateFlow that external components can observe
    val scannedDevices: StateFlow<List<BluetoothDevice>> get() = _scannedDevices.asStateFlow()

    init {
        // Observe the scannedDevicesFlow from the BluetoothController
        bluetoothController.scannedDevices.map { devices ->
            // You can perform any additional transformations here if needed
            devices
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(3000),
            emptyList() // Initial value for the StateFlow
        ).onEach { updatedDevicesList ->
            // Update the private mutable state flow with the new list
            _scannedDevices.value = updatedDevicesList
        }.launchIn(viewModelScope)
    }

    fun startScan(context: Context) {
        bluetoothController.scanLeDevice(context)
    }

    fun stopScan(context: Context) {
        bluetoothController.stopScanLeDevice(context)
    }

    fun deleteSearchResults(){
        bluetoothController.deleteSearchResults()
    }


    fun connectDevice(device : BluetoothDevice){
        bluetoothController.deviceFound(device)
    }


}