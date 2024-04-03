package com.example.arduinobluetooth.bluetooth

enum class BluetoothState {
    INIT,
    CONNECTED,
    UNKNOWN_DEVICE, // probably not the icure device
    READY_TO_CONFIGURE,
    DISCONNECTED,
    CONFIGURED // the device disconnected since its has been configured
}