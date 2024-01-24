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
import android.os.Message
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.util.UUID
import com.example.arduinobluetooth.utils.Crypto


@SuppressLint("MissingPermission")
class BluetoothController(private val context : Context) {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter = bluetoothManager.adapter

    private var connectedGatt:BluetoothGatt? = null
    private val _isConnected = MutableStateFlow<Boolean>(false)
    val isConnected : StateFlow<Boolean> get() = _isConnected

    private val handler = Handler(Looper.getMainLooper())

    private val _scannedDevices = MutableStateFlow<List<MyBluetoothDevice>>(emptyList())
    val scannedDevices: StateFlow<List<MyBluetoothDevice>> = _scannedDevices.asStateFlow()

    private var devRealTimeRssi = mutableListOf<MyBluetoothDevice>() // to contain address and RSSI value

    private var updateScannedDevicesRunnable: Runnable? = null

    private val serviceUuid = UUID.fromString("b1be5923-8ca2-415c-9f20-69023f8b4c33")
    private val testUuid = UUID.fromString("8bbc0c8b-d41b-4fd3-8854-af19317d62a1")
    private val senderUuid = UUID.fromString("3dbd6a55-fcc7-4cd8-811f-2c5754296a0a")
    private val senderTokenUuid = UUID.fromString("0fd7161a-c2e2-44cb-a5ee-9dd3187424fc")
    private val encryptKeyUuid = UUID.fromString("7626adb2-28ab-4327-8ead-bb571cb1d7f0")
    private val configStatusUuid = UUID.fromString("5fae4e14-ca8f-41d7-bd5b-c3a0498973ae")


    private val cryptoUtils  = Crypto()

    //private val SCAN_PERIOD :Long = 1000;



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


    fun stopScanLeDevice(context: Context){
        if(!adapter.isEnabled){
            Log.i("BTH","Bluetooth not enabled")
            return
        }

        try{
            adapter.bluetoothLeScanner.stopScan(leScanCallback)
            Log.i("After scan", " Found ${_scannedDevices.value.size.toString()} devices")

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



    // toggle arduino builtin Led to ensure we are communicating with the right device
    fun testDeviceConnection() {
        connectedGatt?.let { gatt ->

            val arduinoService = gatt.getService(serviceUuid)
            val testCharacteristic = arduinoService?.getCharacteristic(testUuid)

            if (testCharacteristic != null) {
                val message = "device connection test".toByteArray(Charsets.UTF_8)
                testCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                testCharacteristic.setValue(message)
                gatt.writeCharacteristic(testCharacteristic)
            }
        }
    }



    fun configureArduinoDevice(){
        connectedGatt?.let { gatt ->

            val arduinoService = gatt.getService(serviceUuid)
            val senderIdCharacteristic = arduinoService?.getCharacteristic(senderUuid)
            val senderTokenCharacteristic = arduinoService?.getCharacteristic(senderTokenUuid)
            val senderKeyCharacteristic = arduinoService?.getCharacteristic(encryptKeyUuid)
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


            if (senderIdCharacteristic!=null){
                Handler(Looper.getMainLooper()).postDelayed({
                    val senderUuid  = UUID.fromString("219628ad-1441-4b15-9fbb-406b8f220779")
                    val byteArray = cryptoUtils.uuid128ToByteArray(senderUuid)
                    writeToCharacteristic(senderIdCharacteristic,gatt,byteArray)
                    Log.i("GATT", "Setting send name characteristic")
                }, 200)
            }

            if (senderTokenCharacteristic != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val senderToken  = UUID.fromString("6fc6ee94-c981-47c8-ba23-1d9d70bdcea9")
                    val byteArray = cryptoUtils.uuid128ToByteArray(senderToken)
                    Log.i("GATT", "Setting sender token characteristic")
                    writeToCharacteristic(senderTokenCharacteristic,gatt,byteArray)
                }, 300)
            }

            if (senderKeyCharacteristic != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val uuid256 = cryptoUtils.generateAES256()

                    if(uuid256!= null){
                        val byteArray = cryptoUtils.keyToByteArray(uuid256)
                        //Log.i("ENCRYPTION KEY",byteArray.toHexString())
                        Log.i("GATT", "Setting encryption key characteristic")
                        writeToCharacteristic(senderKeyCharacteristic,gatt,byteArray)
                    }
                }, 800)
            }



        }
    }

    fun ByteArray.toHexString(): String {
        return joinToString(separator = "") { byte -> "%02X".format(byte) }
    }

    fun writeToCharacteristic(characteristic: BluetoothGattCharacteristic, gatt: BluetoothGatt,byteArray: ByteArray){ //message: String
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        characteristic.setValue(byteArray)
        gatt.writeCharacteristic(characteristic)
    }


    fun connectDevice(device : BluetoothDevice){
        try {
            device.connectGatt(context,false,gattCallback)
            Log.i("GATT", "Connecting to gatt")
        }catch(e:Error){
            Log.i("GATT", e.toString())
        }
    }



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

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothGatt .STATE_CONNECTED){
                gatt?.requestMtu(256)
                connectedGatt = gatt
                _isConnected.value = true
                Handler(Looper.getMainLooper()).postDelayed({
                    gatt?.discoverServices()
                }, 2000)
            }else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                connectedGatt = null
                _isConnected.value = false
            }
        }


        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.i("GATT","Service discovered")

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