package com.example.arduinobluetooth.presentation

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduinobluetooth.data.BluetoothController
import com.example.arduinobluetooth.data.MyBluetoothDevice


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

    private val _scannedDevices = MutableStateFlow<List<MyBluetoothDevice>>(emptyList())

    val scannedDevices: StateFlow<List<MyBluetoothDevice>> get() = _scannedDevices.asStateFlow()

    init {
        // Observe the scannedDevicesFlow from the BluetoothController
        bluetoothController.scannedDevices.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(3000),
            emptyList() // Initial value for the StateFlow
        ).onEach { updatedDevicesList ->
            _scannedDevices.value = updatedDevicesList
        }.launchIn(viewModelScope)
    }


    fun getDeviceByAddress(address : String) : MyBluetoothDevice?{
        return _scannedDevices.value.firstOrNull{it.device.address == address}
    }

    fun startScan(context: Context) {
        bluetoothController.scanLeDevice(context)
    }

    fun stopScan(context: Context) {
        bluetoothController.stopScanLeDevice(context)
    }

    fun deleteSearchResults(context:Context){
        bluetoothController.stopScanLeDevice(context = context)
        bluetoothController.disconnectDevice()
        bluetoothController.deleteSearchResults()
    }


    fun connectDevice(context: Context,device : BluetoothDevice){
        bluetoothController.connectDevice(device)
    }


}