package com.example.arduinobluetooth.presentation

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn



class BluetoothViewModel (
    private val bluetoothController: BluetoothController
): ViewModel() {

    private val _searchtext = MutableStateFlow("")
    val searchText = _searchtext.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<MyBluetoothDevice>>(emptyList())
    val scannedDevices = searchText
        .combine(_scannedDevices){text,devices ->
            if(text.isBlank()){
                devices
            }else{
                devices.filter {
                    doesMatchSearchQuery(text,it)
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _scannedDevices.value
        )



    val isConnected : StateFlow<Boolean> = bluetoothController.isConnected.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),false)

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

    @SuppressLint("MissingPermission")
    fun doesMatchSearchQuery(query:String, device: MyBluetoothDevice):Boolean{
        val matching = device.device.name
        if(device.device.name != null){
            return matching.startsWith(query,ignoreCase = true)
        }
        return false

    }

    fun onSearchTextChange(text:String){
        _searchtext.value = text;
    }



    fun getDeviceByAddress(address : String?) : MyBluetoothDevice?{
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


    fun testDeviceConnection(){
        bluetoothController.testDeviceConnection()
    }

    fun configureArduinoDevice(){
        bluetoothController.configureArduinoDevice()
    }


}