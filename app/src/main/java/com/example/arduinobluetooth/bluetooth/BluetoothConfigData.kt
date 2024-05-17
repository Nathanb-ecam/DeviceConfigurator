package com.example.arduinobluetooth.bluetooth

data class BluetoothConfigData(
    val cid : String,
    val uid : String,
    val password : String,
    val key : ByteArray,
    val topic : String
)
