package com.example.arduinobluetooth.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BluetoothController(private val context : Context) {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter = bluetoothManager.adapter
    private var connectedGatt:BluetoothGatt? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _scannedDevices = MutableStateFlow<List<MyBluetoothDevice>>(emptyList())
    val scannedDevices: StateFlow<List<MyBluetoothDevice>> = _scannedDevices.asStateFlow()

    private var devRealTimeRssi = mutableListOf<MyBluetoothDevice>() // to contain address and RSSI value

    private var updateScannedDevicesRunnable: Runnable? = null

    //private val SCAN_PERIOD :Long = 1000;



    @SuppressLint("MissingPermission")
    fun scanLeDevice(context: Context,){
            if(!adapter.isEnabled){
                Log.i("BTH","Bluetooth not enabled")
                Toast.makeText(context, "Enable Bluetooth", Toast.LENGTH_SHORT).show()
                return
            }

/*            Handler(Looper.getMainLooper()).postDelayed({
                adapter.bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)*/

            try{
                adapter.bluetoothLeScanner.startScan(leScanCallback)
                startUpdating()
            }catch(e: Exception){
                Log.i("Exception",e.toString())
            }
    }

    @SuppressLint("MissingPermission")
    fun stopScanLeDevice(context: Context){
        if(!adapter.isEnabled){
            Log.i("BTH","Bluetooth not enabled")
            return
        }

        try{
            adapter.bluetoothLeScanner.stopScan(leScanCallback)
            Log.i("After scan", _scannedDevices.value.size.toString())
            for(dev in _scannedDevices.value){
                Log.i("After scan","MAC : ${dev.device.address}")
            }

        }
        catch(e:Exception){
            Log.i("Exception",e.toString())
        }
    }
    fun deleteSearchResults(){
        stopScanLeDevice(context = context)
        stopRefreshingDeviceValues()
        devRealTimeRssi = mutableListOf()
        _scannedDevices.value = emptyList()
    }




    private fun startUpdating() {
        updateStateFlow() // for the first update
        startRefreshingDevicesWithRunnable() // runnable
    }
    private fun startRefreshingDevicesWithRunnable() {
        updateScannedDevicesRunnable = object : Runnable {
            override fun run() {
                updateStateFlow()
                handler.postDelayed(this, 300)
            }
        }
        handler.postDelayed(updateScannedDevicesRunnable!!, 1000)
    }

    private fun stopRefreshingDeviceValues() {
        updateScannedDevicesRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    private fun updateStateFlow() {
        val updatedList = _scannedDevices.value.toMutableList().apply {
            devRealTimeRssi.forEach { newDevice ->
                val existingDeviceIndex = indexOfFirst { it.device.address == newDevice.device.address }
                if (existingDeviceIndex != -1) {
                    // Device already exists, update its RSSI value
                    this[existingDeviceIndex] = newDevice
                } else {
                    // Device is not in the list, add it
                    add(newDevice)
                }
            }
        }
        _scannedDevices.value = updatedList
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val rssi = result.rssi

            val newDevice = MyBluetoothDevice(device, rssi)

            val existingDeviceIndex = devRealTimeRssi.indexOfFirst { it.device.address == newDevice.device.address }
            if (existingDeviceIndex != -1) {
                // update the already existing device
                devRealTimeRssi[existingDeviceIndex] = newDevice
            } else {
                devRealTimeRssi.add(newDevice)
            }

        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("BTH",String.format("Error scan %s" ,errorCode))
        }
    }


    @SuppressLint("MissingPermission")
    fun connectDevice(device : BluetoothDevice){
        try {
            device.connectGatt(context,false,gattCallback)
            Log.i("GATT", "Connecting to gatt")
        }catch(e:Error){
            Log.i("GATT", e.toString())
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnectDevice(){
        connectedGatt?.let{gatt->
            try{
                gatt.disconnect()
            }catch(e:Error){
                Log.i("GATT Device","Device disconnected")
            }
        }
    }


    private val gattCallback = object : BluetoothGattCallback(){
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothGatt .STATE_CONNECTED){
                gatt?.requestMtu(256)
                connectedGatt = gatt
                Handler(Looper.getMainLooper()).postDelayed({
                    gatt?.discoverServices()
                }, 2000)
            }else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                connectedGatt = null
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.i("GATT","Service discovered")

            val serviceUuid = UUID.fromString("b1be5923-8ca2-415c-9f20-69023f8b4c33")
            val senderUuid = UUID.fromString("3dbd6a55-fcc7-4cd8-811f-2c5754296a0a")
            val encryptKeyUuid = UUID.fromString("7626adb2-28ab-4327-8ead-bb571cb1d7f0")
            val configStatusUuid = UUID.fromString("5fae4e14-ca8f-41d7-bd5b-c3a0498973ae")


            val arduinoService =   gatt?.getService(serviceUuid)
            val senderCharacteristic = arduinoService?.getCharacteristic(senderUuid)
            val keyCharacteristic = arduinoService?.getCharacteristic(encryptKeyUuid)
            val statusCharacteristic = arduinoService?.getCharacteristic(configStatusUuid)



            Handler(Looper.getMainLooper()).postDelayed({
                val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                val descriptor = statusCharacteristic?.getDescriptor(CCCD_UUID)

                if (descriptor != null) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.setCharacteristicNotification(statusCharacteristic, true)
                    gatt.writeDescriptor(descriptor)
                    Log.i("GATT", "Getting better")
                } else {
                    Log.e("Bluetooth", "Descriptor not found for CCCD UUID")
                }
            }, 100)


            if (senderCharacteristic != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    Log.i("GATT", "Setting send name characteristic")
                    senderCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    senderCharacteristic.setValue("Jean-jacques".toByteArray(Charsets.UTF_8))
                    //characteristic.value = byteArrayOf(1)
                    gatt.writeCharacteristic(senderCharacteristic)
                }, 1500) // Adjust the delay as needed
            }

            if (keyCharacteristic != null) {
                Log.i("GATT", "Setting encryption key characteristic")
                Handler(Looper.getMainLooper()).postDelayed({
                    keyCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    keyCharacteristic.setValue("a241bAZEERTRTY123".toByteArray(Charsets.UTF_8))
                    gatt.writeCharacteristic(keyCharacteristic)
                }, 1600) // Adjust the delay as needed
            }

        }


        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            Log.i("GATT UP","Updated value")
            Handler(Looper.getMainLooper()).post {
                val statusCharacteristicValue = characteristic.value?.toString() ?: ""
                Log.i("GATT changed","NEW" +statusCharacteristicValue)
            }
        }
    }




}