package com.example.arduinobluetooth.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BluetoothController(private val context : Context) {

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scannedDevices: StateFlow<List<BluetoothDevice>> = _scannedDevices.asStateFlow()


    @SuppressLint("MissingPermission")
    fun scanLeDevice(context: Context){
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val adapter = bluetoothManager.adapter

        if(!adapter.isEnabled){
            Log.i("BTH","Bluetooth not enabled")
        }

        try{
            adapter.bluetoothLeScanner.startScan(leScanCallback)
        }catch(e: Exception){
            Log.i("Exception",e.toString())
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScanLeDevice(context: Context){
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val adapter = bluetoothManager.adapter


        if(!adapter.isEnabled){
            Log.i("BTH","Bluetooth not enabled")
        }

        try{
            adapter.bluetoothLeScanner.stopScan(leScanCallback)
            Log.i("After scan", _scannedDevices.value.size.toString())
            for(dev in _scannedDevices.value){
                Log.i("After scan","MAC : ${dev.address}")
            }

        }
        catch(e:Exception){
            Log.i("Exception",e.toString())
        }
    }


    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val newDevice = result.device

            if (_scannedDevices.value.none { it.address == newDevice.address }) {
                val updatedList = _scannedDevices.value.toMutableList().apply {
                    add(newDevice)
                }
                _scannedDevices.value = updatedList
            }


            Log.i("BTH","Device : ${result.device.name?:"No name"} : ${result.device.address}")


        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)

            Log.i("BTH",String.format("Error scan %s" ,errorCode))
        }
    }


    @SuppressLint("MissingPermission")
    fun deviceFound(device : BluetoothDevice){
        try {
            device.connectGatt(context,false,gattCallback)
            Log.i("GATT", "Connecting to gatt")
        }catch(e:Error){
            Log.i("GATT", e.toString())
        }

    }

    fun deleteSearchResults(){
        _scannedDevices.value = emptyList()
    }


    private val gattCallback = object : BluetoothGattCallback(){

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothGatt .STATE_CONNECTED){
                gatt?.requestMtu(256)

                Handler(Looper.getMainLooper()).postDelayed({
                    gatt?.discoverServices()
                    Log.i("GATT","Disovering device services")
                }, 1000) // Adjust the delay as needed
            }
        }




        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.i("GATT","Service discovered")

            val serviceUuid = UUID.fromString("b1be5923-8ca2-415c-9f20-69023f8b4c33")
            val charUuid = UUID.fromString("7626adb2-28ab-4327-8ead-bb571cb1d7f0")
            val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(charUuid)


            if (characteristic != null) {
                Log.i("GATT","Setting value to 1")
                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                //characteristic.setValue("1".toByteArray(Charsets.UTF_8))
                characteristic.value = byteArrayOf(1)
                gatt.writeCharacteristic(characteristic)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.i("GATT","Writed something to device")
        }
    }



}