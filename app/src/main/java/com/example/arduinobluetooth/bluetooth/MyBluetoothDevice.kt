package com.example.arduinobluetooth.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

data class MyBluetoothDevice(
    /*val device : BluetoothDevice,*/
    val name : String,
    val address : String,
    var rssi : Int
) {
    fun toBluetoothDevice(): BluetoothDevice {
        return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)
    }

}
