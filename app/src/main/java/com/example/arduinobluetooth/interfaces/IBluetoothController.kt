package com.example.arduinobluetooth.interfaces

import android.content.Context
import com.example.arduinobluetooth.data.Bluetooth.BluetoothConfigData
import com.example.arduinobluetooth.data.Bluetooth.MyBluetoothDevice
import com.example.arduinobluetooth.data.Bluetooth.BluetoothState
import kotlinx.coroutines.flow.StateFlow

interface IBluetoothController  {
    val connectionState: StateFlow<BluetoothState>
    val scannedDevices: StateFlow<List<MyBluetoothDevice>>

    fun scanLeDevice(context: Context)
    fun stopScanLeDevice(context: Context)

    fun disconnectDevice()

    fun deleteSearchResults()

    fun connectDevice(deviceAdress : String)

    fun testDeviceConnection()

    fun configureArduinoDevice(configData: BluetoothConfigData)

    fun updateConnectedState(state : BluetoothState)


}