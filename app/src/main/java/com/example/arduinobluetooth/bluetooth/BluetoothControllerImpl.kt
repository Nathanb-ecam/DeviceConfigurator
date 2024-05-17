package com.example.arduinobluetooth.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.arduinobluetooth.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import com.example.arduinobluetooth.utils.Crypto
import com.icure.kryptom.utils.toHexString
import java.util.LinkedList
import java.util.Queue


@SuppressLint("MissingPermission")
class BluetoothControllerImpl(private val context : Context) : IBluetoothController {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter = bluetoothManager.adapter
    private var connectedGatt:BluetoothGatt? = null


    private val _connectionState = MutableStateFlow(BluetoothState.INIT)
    override val connectionState : StateFlow<BluetoothState> get() = _connectionState
    private val _isScanning = MutableStateFlow(false)

    // WRITING TO BLUETOOTH CHARACTERISTICS REQURES A QUEUE MECHANISM
    data class WriteOperation(val characteristic: BluetoothGattCharacteristic, val data : ByteArray)
    private val writeQueue: Queue<WriteOperation> = LinkedList()
    private var isWriting = false


    private val handler = Handler(Looper.getMainLooper())

    private val _scannedDevices = MutableStateFlow<List<MyBluetoothDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<MyBluetoothDevice>> = _scannedDevices.asStateFlow()
    private var devRealTimeRssi = mutableListOf<MyBluetoothDevice>() // to contain address and RSSI value
    private var updateScannedDevicesRunnable: Runnable? = null


    private val bleDeviceUuids = mapOf(
        "serviceUuid" to UUID.fromString(context.getString(R.string.serviceUuid)),
        "testUuid" to UUID.fromString(context.getString(R.string.testUuid)),
        "senderUuid" to UUID.fromString(context.getString(R.string.senderUuid)),
        "senderTokenUuid" to UUID.fromString(context.getString(R.string.senderTokenUuid)),
        "encryptKeyUuid" to UUID.fromString(context.getString(R.string.encryptKeyUuid)),
        "topicUuid" to UUID.fromString(context.getString(R.string.topicUuid)),
        "contactId" to UUID.fromString(context.getString(R.string.contactUuid)),
        "configStatusUuid" to UUID.fromString(context.getString(R.string.configStatusUid))
    )

    companion object ble {
        val TAG = "BluetoothControllerImpl"

    }


    private val cryptoUtils  = Crypto()

/*    private val SCAN_PERIOD :Long = 1000;*/



    override fun updateConnectedState(state : BluetoothState){
        _connectionState.value = state
    }

    override fun scanLeDevice(context: Context,){
        if(!adapter.isEnabled){
                Log.i("BTH","Bluetooth not enabled")
                Toast.makeText(context, "Enable Bluetooth", Toast.LENGTH_SHORT).show()
                return
            }
            try{
                if(!_isScanning.value){
                    adapter.bluetoothLeScanner.startScan(leScanCallback)
                    _isScanning.value = true
                    startUpdating()
                }
            }catch(e: Exception){
                Log.i("Exception",e.toString())
            }
    }


    override  fun stopScanLeDevice(context: Context){
        if(!adapter.isEnabled){
            Log.i("BTH","Bluetooth not enabled")
            return
        }
        try{
            if(_isScanning.value){
                adapter.bluetoothLeScanner.stopScan(leScanCallback)
                _isScanning.value = false
            }
            Log.i("After scan", " Found ${_scannedDevices.value.size} devices")
        }
        catch(e:Exception){
            Log.i("Exception",e.toString())
        }
    }
    override fun deleteSearchResults(){
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
                handler.postDelayed(this, 2000)
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
                val existingDeviceIndex = indexOfFirst { it.address == newDevice.address }
                if (existingDeviceIndex != -1) {
                    // Device already exists, update its RSSI value
                    this[existingDeviceIndex] = newDevice
                } else {
                    add(newDevice)
                }
            }
        }
        _scannedDevices.value = updatedList
    }

    fun BluetoothDevice.toMyBluetoothDevice(rssi : Int): MyBluetoothDevice {
        return MyBluetoothDevice(
            name = this.name ?: "Unknown",
            address = this.address,
            rssi = rssi
        )
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            //super.onScanResult(callbackType, result)
            val device = result.device
            val rssi = result.rssi



            val newDevice = device.toMyBluetoothDevice(rssi)

            val existingDeviceIndex = devRealTimeRssi.indexOfFirst { it.address == newDevice.address }
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

    fun configurationDeviceCompleted(){
        // need to reset data if the patient wants to reconfigure another device without quitting the app
        _connectionState.value = BluetoothState.CONFIGURED


    }

    // toggle arduino builtin Led to test connection with device
    override fun testDeviceConnection() {
        connectedGatt?.let { gatt ->

            val arduinoService = gatt.getService(bleDeviceUuids.getValue("serviceUuid"))
            val testCharacteristic = arduinoService?.getCharacteristic(bleDeviceUuids.getValue("testUuid"))

            if (testCharacteristic != null) {
                val data = "1".toByteArray()
                writeToCharacteristic(gatt,WriteOperation(testCharacteristic,data))
            }
        }?: Log.i("Connected GATT","Not connected to a device")
    }


    fun writeToCharacteristicWithQueue(characteristic: BluetoothGattCharacteristic, gatt: BluetoothGatt, data: ByteArray) {
        writeQueue.add(WriteOperation(characteristic, data))
        if (!isWriting) {
            processNextWrite(gatt)
        }
    }

    fun processNextWrite(gatt : BluetoothGatt){
        val writeOperation = writeQueue.poll()
        if(writeOperation != null){
            isWriting = true
            writeToCharacteristic(gatt,writeOperation)
        }
        configurationDeviceCompleted()


    }
    override fun configureArduinoDevice(configData: BluetoothConfigData){
        connectedGatt?.let { gatt ->


            val arduinoService = gatt.getService(bleDeviceUuids.getValue("serviceUuid"))
            val senderIdCharacteristic = arduinoService?.getCharacteristic(bleDeviceUuids.getValue("senderUuid"))
            val senderTokenCharacteristic = arduinoService?.getCharacteristic(bleDeviceUuids.getValue("senderTokenUuid"))
            val senderKeyCharacteristic = arduinoService?.getCharacteristic(bleDeviceUuids.getValue("encryptKeyUuid"))
            val contactIdCharacteristic = arduinoService?.getCharacteristic(bleDeviceUuids.getValue("contactId"))
            val topicCharacteristic = arduinoService?.getCharacteristic(bleDeviceUuids.getValue("topicUuid"))
            //val statusCharacteristic = arduinoService?.getCharacteristic(configStatusUuid)

            val allDefined = listOf(
                    senderIdCharacteristic,
                    senderTokenCharacteristic,
                    senderKeyCharacteristic,
                    contactIdCharacteristic,
                    topicCharacteristic
                )
                .all { it != null }


            if(!allDefined){
                Log.i("CONFIGURE", "Some characteristics are not defined")
                return
            }else{
                Log.i(TAG, "Starting beautiful queue")
                val contactId  = UUID.fromString(configData.cid)
                val byteArray = cryptoUtils.uuid128ToByteArray(contactId)
                writeToCharacteristicWithQueue(senderIdCharacteristic!!,gatt,configData.uid.toByteArray(Charsets.UTF_8))
                writeToCharacteristicWithQueue(senderTokenCharacteristic!!,gatt,configData.password.toByteArray())
                writeToCharacteristicWithQueue(contactIdCharacteristic!!,gatt,byteArray)
                writeToCharacteristicWithQueue(topicCharacteristic!!,gatt,configData.topic.toByteArray(Charsets.UTF_8))
                writeToCharacteristicWithQueue(senderKeyCharacteristic!!,gatt,configData.key)
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


        }?:Log.i("Connected GATT","Not connected to a device")
    }





    fun writeToCharacteristic(gatt: BluetoothGatt,wo : WriteOperation){ //message: String
        val characteristic = wo.characteristic
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        characteristic.setValue(wo.data)
        val success = gatt.writeCharacteristic(characteristic)
        if (!success) { // the initiation of the write operation failed, we can then set writing to false to reattempt
            isWriting = false
            processNextWrite(gatt)
        }
    }


    override fun connectDevice(deviceAddress : String){
        try {
            val device = adapter.getRemoteDevice(deviceAddress)
            device.connectGatt(context,false,gattCallback)

            Log.i("GATT", "Connecting to ${deviceAddress}")
        }catch(e:Error){
            Log.i("GATT", e.toString())
        }
    }

    override fun disconnectDevice(){
        connectedGatt?.let{gatt->
            try{
                gatt.disconnect()
                Log.i("GATT Device","We forced Device disconnection")

            }catch(e:Error){
                Log.i("ERROR", "Error while disconnecting device")
            }
        }
        Log.i("GATT Device","No device were connected")
    }


    private val gattCallback = object : BluetoothGattCallback(){

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    //gatt?.requestMtu(32) // biggest item to send is the 256 bit key
                    connectedGatt = gatt
                    gatt?.discoverServices()
                    Log.i("STATE","Connected")
                    stopScanLeDevice(context = context)
                    _connectionState.value = BluetoothState.CONNECTED

                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i("STATE","Disconnected")
                    if(_connectionState.value != BluetoothState.CONFIGURED){
                        _connectionState.value = BluetoothState.DISCONNECTED
                    }

                }
                BluetoothProfile.STATE_DISCONNECTING -> {
                    Log.i("STATE","Disconnecting")
                }
                BluetoothProfile.STATE_CONNECTING -> {
                    Log.i("STATE","Connecting")
                }
            }

        }


        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

            Log.i("GATT","Service discovered")

            val arduinoService = gatt?.getService(bleDeviceUuids.getValue("serviceUuid"))
            val statusCharacteristic = arduinoService?.getCharacteristic(bleDeviceUuids.getValue("configStatusUuid"))

            val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            val descriptor = statusCharacteristic?.getDescriptor(CCCD_UUID)

            val services = gatt?.services
            val icureService = services?.firstOrNull { svc ->
                svc.uuid == bleDeviceUuids.getValue("serviceUuid")
            }

            if (icureService != null) {
                // the device contains the right BLE service, we need to verify its characteristics
                val characteristics = icureService.characteristics
                characteristics?.forEach { characteristic ->
                    Log.i("CHAR","charcac device")
                    if(!bleDeviceUuids.containsValue(characteristic.uuid)) {
                        _connectionState.value = BluetoothState.UNKNOWN_DEVICE
                        Log.i("YESSIR","unknown device, was missing characteristics")
                        return
                    }
                }
            } else {
                _connectionState.value = BluetoothState.UNKNOWN_DEVICE
                Log.i("YESSIR","unknown device, deosnt even has the icure service")
                return
            }


            gatt.setCharacteristicNotification(statusCharacteristic, true)



            // NEED TO SETUP NOTFICATIONS TO KNOW WHEN DEVICE IS SUCCESSFULY CONFIGURED
    /*        if (descriptor != null) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                val descriptorWriteResult = gatt.writeDescriptor(descriptor)
                if (descriptorWriteResult == true) {
                    Log.i("GATT", "Descriptor write successful")
                } else {
                    Log.e("GATT", "Descriptor write failed")
                }
            } else {
                Log.e("Bluetooth", "Descriptor not found for CCCD UUID")
            }*/
            Log.i("YESSIR","this should not be printed if unknwon device")
            _connectionState.value = BluetoothState.READY_TO_CONFIGURE
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            gatt?.let {
                isWriting = false
                processNextWrite(gatt)
            }?: Log.d(TAG, "That's sad")
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