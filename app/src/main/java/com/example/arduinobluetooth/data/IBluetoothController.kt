package com.example.arduinobluetooth.data

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.example.arduinobluetooth.utils.BluetoothState
import kotlinx.coroutines.flow.StateFlow

interface IBluetoothController  {
    val connectionState: StateFlow<BluetoothState>
    val scannedDevices: StateFlow<List<MyBluetoothDevice>>

    fun scanLeDevice(context: Context)
    fun stopScanLeDevice(context: Context)

    fun disconnectDevice()

    fun deleteSearchResults()

    fun connectDevice(device : BluetoothDevice)

    fun testDeviceConnection()

    fun configureArduinoDevice(configData: BluetoothConfigData)

    fun updateConnectedState(state : BluetoothState)


}