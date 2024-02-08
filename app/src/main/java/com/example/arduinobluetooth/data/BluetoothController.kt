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
import com.example.arduinobluetooth.utils.BluetoothState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import com.example.arduinobluetooth.utils.Crypto


@SuppressLint("MissingPermission")
class BluetoothController(private val context : Context) {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter = bluetoothManager.adapter

    private var connectedGatt:BluetoothGatt? = null
    private val _connectionState = MutableStateFlow(BluetoothState.DISCONNECTED)
    val connectionState : StateFlow<BluetoothState> get() = _connectionState

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
    private val contactId = UUID.fromString("477f3318-3a09-4c09-8273-bfb28163b7fd")
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
            //super.onScanResult(callbackType, result)
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
            //super.onScanFailed(errorCode)
            Log.i("BTH",String.format("Error scan %s" ,errorCode))
        }
    }



    // toggle arduino builtin Led to ensure we are communicating with the right device
    fun testDeviceConnection() {
        connectedGatt?.let { gatt ->

            val arduinoService = gatt.getService(serviceUuid)
            val testCharacteristic = arduinoService?.getCharacteristic(testUuid)



            if (testCharacteristic != null) {
                writeToCharacteristic(testCharacteristic,gatt,"1".toByteArray())
    /*            val message = "device connection test".toByteArray(Charsets.UTF_8)
                testCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                testCharacteristic.setValue(message)
                gatt.writeCharacteristic(testCharacteristic)*/
            }
        }
    }



    fun configureArduinoDevice(){
        connectedGatt?.let { gatt ->


            val arduinoService = gatt.getService(serviceUuid)
            val senderIdCharacteristic = arduinoService?.getCharacteristic(senderUuid)
            val senderTokenCharacteristic = arduinoService?.getCharacteristic(senderTokenUuid)
            val senderKeyCharacteristic = arduinoService?.getCharacteristic(encryptKeyUuid)
            val contactIdCharacteristic = arduinoService?.getCharacteristic(contactId)
            //val statusCharacteristic = arduinoService?.getCharacteristic(configStatusUuid)

            val allDefined = listOf(
                    senderIdCharacteristic,
                    senderTokenCharacteristic,
                    senderKeyCharacteristic
                )
                .any { it != null }


            if(!allDefined){
                Log.i("CONFIGURE", "Some characteristics are not defined")
                return
            }

/*            Handler(Looper.getMainLooper()).postDelayed({
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
            }, 100)*/


            if (senderIdCharacteristic!=null){
                Handler(Looper.getMainLooper()).postDelayed({
                    val senderUuid  = UUID.fromString("219628ad-1441-4b15-9fbb-406b8f220779")
                    val byteArray = cryptoUtils.uuid128ToByteArray(senderUuid)
                    Log.i("SENDER UUID",byteArray.toHexString())
                    writeToCharacteristic(senderIdCharacteristic,gatt,byteArray)
                    Log.i("GATT", "Setting send name characteristic")
                }, 0)
            }

            if (senderTokenCharacteristic != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val senderToken  = UUID.fromString("6fc6ee94-c981-47c8-ba23-1d9d70bdcea9")
                    val byteArray = cryptoUtils.uuid128ToByteArray(senderToken)
                    Log.i("TOKEN",byteArray.toHexString())
                    Log.i("GATT", "Setting sender token characteristic")
                    writeToCharacteristic(senderTokenCharacteristic,gatt,byteArray)
                }, 200)
            }

            if (contactIdCharacteristic != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val contactId  = UUID.fromString("6fc6ee94-47c8-ba23-c981-1d9d70bdcea9")
                    val byteArray = cryptoUtils.uuid128ToByteArray(contactId)
                    Log.i("CONTACT",byteArray.toHexString())
                    Log.i("GATT", "Setting contact Id characteristic")
                    writeToCharacteristic(contactIdCharacteristic,gatt,byteArray)
                }, 400)
            }


            if (senderKeyCharacteristic != null) {
                Handler(Looper.getMainLooper()).postDelayed({
   /*                 val uuid256 = cryptoUtils.generateAES256()

                    if(uuid256!= null){
                        val byteArray = cryptoUtils.keyToByteArray(uuid256)
                        Log.i("ENCRYPTION KEY",byteArray.toHexString())
                        Log.i("GATT", "Setting encryption key characteristic")
                        writeToCharacteristic(senderKeyCharacteristic,gatt,byteArray)
                        _connectionState.value = BluetoothState.CONFIGURED
                    }*/
                    val hexString = "2b7e151628aed2a6abf7158809cf4f3c2b7e151628aed2a6abf7158809cf4f3c"
                    val byteArrayKey = hexStringToByteArray(hexString)
                    writeToCharacteristic(senderKeyCharacteristic,gatt,byteArrayKey)
                    _connectionState.value = BluetoothState.CONFIGURED
                }, 600)
            }



        }
    }

    fun hexStringToByteArray(hexString: String): ByteArray {
        val result = ByteArray(hexString.length / 2)
        for (i in hexString.indices step 2) {
            val firstDigit = Character.digit(hexString[i], 16)
            val secondDigit = Character.digit(hexString[i + 1], 16)
            val byteValue = (firstDigit shl 4) + secondDigit
            result[i / 2] = byteValue.toByte()
        }
        return result
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
            //super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothGatt .STATE_CONNECTED){
                //gatt?.requestMtu(32) // biggest item to send is the 256 bit key
                connectedGatt = gatt
                _connectionState.value = BluetoothState.CONNECTED
                Handler(Looper.getMainLooper()).postDelayed({
                    gatt?.discoverServices()
                }, 2000)
            }else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                connectedGatt = null
                if(_connectionState.value != BluetoothState.CONFIGURED){
                    _connectionState.value = BluetoothState.DISCONNECTED
                }


            }

        }


        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            //super.onServicesDiscovered(gatt, status)
            Log.i("GATT","Service discovered")

            val arduinoService = gatt?.getService(serviceUuid)
            val statusCharacteristic = arduinoService?.getCharacteristic(configStatusUuid)

            val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            val descriptor = statusCharacteristic?.getDescriptor(CCCD_UUID)

            gatt?.setCharacteristicNotification(statusCharacteristic, true)



            if (descriptor != null) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                val descriptorWriteResult = gatt.writeDescriptor(descriptor)
                if (descriptorWriteResult == true) {
                    Log.i("GATT", "Descriptor write successful")
                } else {
                    Log.e("GATT", "Descriptor write failed")
                }




            } else {
                Log.e("Bluetooth", "Descriptor not found for CCCD UUID")
            }
            _connectionState.value = BluetoothState.READY_TO_CONFIGURE
        }


        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            //super.onCharacteristicChanged(gatt, characteristic, value)
            Log.i("GATT UP","Updated value")
            Handler(Looper.getMainLooper()).post {
                val statusCharacteristicValue = characteristic.value?.toString() ?: ""
                Log.i("GATT changed","NEW" +statusCharacteristicValue)
            }
        }
    }




}