package com.example.arduinobluetooth.data.Bluetooth

import android.content.Context
import android.util.Log
import com.example.arduinobluetooth.interfaces.IBluetoothController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockBluetoothController(val bluetoothState: BluetoothState) : IBluetoothController {

    private val _scannedDevices = MutableStateFlow<List<MyBluetoothDevice>>(emptyList())

    private val _connectionState = MutableStateFlow(BluetoothState.INIT)


    init {
        val mockDevices = listOf(
            MyBluetoothDevice("Mock-Mac","AF-DA-AD-DA-DA", -50),
            MyBluetoothDevice("Mock-iCure-device","AF-11-33-DA-DA", -60),
            MyBluetoothDevice("Mock-Headphones","0F-78-DA-3E", -70)
        )
        _scannedDevices.value = mockDevices
        _connectionState.value = bluetoothState

    }

    override val connectionState: StateFlow<BluetoothState>
        get() = _connectionState.asStateFlow()
    override val scannedDevices: StateFlow<List<MyBluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    override fun scanLeDevice(context: Context) {
        Log.i("MOCK","N")
    }

    override fun stopScanLeDevice(context: Context) {
        Log.i("MOCK","N")
    }

    override fun disconnectDevice() {
        Log.i("MOCK","N")
    }

    override fun deleteSearchResults() {
        Log.i("MOCK","N")
    }

    override fun connectDevice(deviceAddress: String) {

        Log.i("MOCK","N")
    }

    override fun testDeviceConnection() {
        Log.i("MOCK","N")
    }

    override fun configureArduinoDevice(configData: BluetoothConfigData) {
        Log.i("MOCK","N")
    }

    override fun updateConnectedState(state: BluetoothState) {
        Log.i("MOCK","N")
    }

}
