package com.example.arduinobluetooth.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothController(private val context : Context) {


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
        }
        catch(e:Exception){
            Log.i("Exception",e.toString())
        }
    }


    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            Log.i("BTH","Device : ${result.device.name?:"No name"} : ${result.device.address}")

        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)

            Log.i("BTH",String.format("Error scan %s" ,errorCode))
        }
    }

}