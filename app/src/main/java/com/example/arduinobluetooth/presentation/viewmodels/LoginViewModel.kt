package com.example.arduinobluetooth.presentation.viewmodels


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.arduinobluetooth.R
import com.example.arduinobluetooth.bluetooth.BluetoothConfigData
import com.example.arduinobluetooth.login.ILoginViewModel
import com.example.arduinobluetooth.storage.MySharedPreferences
import com.icure.kryptom.crypto.RsaAlgorithm
import com.icure.kryptom.crypto.defaultCryptoService
import com.icure.kryptom.utils.hexToByteArray
import com.icure.sdk.IcureSdk
import com.icure.sdk.api.AuthenticationMethod
import com.icure.sdk.auth.UsernamePassword
import com.icure.sdk.crypto.impl.BasicCryptoStrategies
import com.icure.sdk.model.Contact
import com.icure.sdk.model.DecryptedContact
import com.icure.sdk.model.DecryptedPatient
import com.icure.sdk.model.Patient
import com.icure.sdk.storage.IcureStorageFacade
import com.icure.sdk.storage.impl.DefaultStorageEntryKeysFactory
import com.icure.sdk.storage.impl.JsonAndBase64KeyStorage
import com.icure.sdk.storage.impl.VolatileStorageFacade
import com.icure.sdk.utils.InternalIcureApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID


enum class DeviceDataStatus{
    READY,
    ERROR,
    INIT
}
data class LoginUIState(
    val apiInitalized : Boolean,
    val deviceDataStatus: DeviceDataStatus,
    val deviceConfigData: BluetoothConfigData
)


class LoginViewModel(
    private val context: Context
) : ILoginViewModel,ViewModel() {

    override val _uiState = MutableStateFlow(LoginUIState(false,DeviceDataStatus.INIT,
        BluetoothConfigData("","","", ByteArray(0),"")
    ))
    override val uiState : StateFlow<LoginUIState> = _uiState.asStateFlow();
    

    private var privateKey =context.getString(R.string.privkey)

    private lateinit var api : IcureSdk
    private var storage = VolatileStorageFacade()
    private var dataOwnerId = context.getString(R.string.dataOwnerId)
    private var cidToTestDecrypt = context.getString(R.string.cidToTestDecrypt)



    private var cid : String? = null
    private var root_topic : String? = context.resources.getString(R.string.root_topic)
    private var userId : String? = null
    private var userPassword : String? = null
    private var symmetricKey : String? = null

    private val sharedPreferences: MySharedPreferences = MySharedPreferences(context)
    init {
        cid = sharedPreferences.cid
/*        Log.i("STORED",sharedPreferences.cid.toString())
        if(cid == null) cid = defaultCryptoService.strongRandom.randomUUID()
        sharedPreferences.cid = cid
        Log.i("STORING",sharedPreferences.cid.toString())*/

    }

    override suspend fun apiInitialize(apiUrl : String, username : String, password : String) {
        handleKeyStorage()
        userId = username
        userPassword = password
        try {
            api = IcureSdk.initialise(
                apiUrl,
                AuthenticationMethod.UsingCredentials(UsernamePassword(username, password)),
                storage,
                BasicCryptoStrategies,
            )
            val currentState =_uiState.value
            _uiState.value = currentState.copy(apiInitalized = true)
            testDecryption()
            Log.i("ICURE API INIT", "Api successfuly initalized")
        }catch(e : Exception){
            Log.i("ICURE API INIT",e.toString())
        }
    }

    override suspend fun createPatient(patient : DecryptedPatient) : DecryptedPatient? {
        try{
            val user = api.user.getCurrentUser()
            val createdPatient = api.patient.createPatient(

                api.patient.withEncryptionMetadata(
                    patient,
                    user

                )
            )
            return createdPatient

        }catch (e : Exception){
            e.printStackTrace()
            updateDataStatus(DeviceDataStatus.ERROR)
            Log.i("Patient","Error while craeting a patient")
        }
        return null
    }


    override suspend fun createContact(patient : DecryptedPatient) : DecryptedContact?{
        try{

            Log.i("ICURE DATA CONFIG, cid", sharedPreferences.cid!!)
            val user = api.user.getCurrentUser()
            val createdContact = api.contact.createContact(
                api.contact.withEncryptionMetadata(

                    DecryptedContact(
                        id = sharedPreferences.cid!!,
                        descr = "ASCASDFASDASDQWEQW",
                    ),
                    patient,
                    user
                )
            )
            return createdContact
        }catch (e : Exception){
            cid = defaultCryptoService.strongRandom.randomUUID()
            sharedPreferences.cid = cid
            sharedPreferences.topic = root_topic + defaultCryptoService.strongRandom.randomUUID()
            e.printStackTrace()
            Log.i("Contact","Error while creating contact")
        }
        return null
    }


    override suspend fun getDeviceConfigData() {
        // this function prepates all the necessary data to pass to the microcontroller



            val patient = createPatient(
                DecryptedPatient(id = UUID.randomUUID().toString(), firstName = "Jean", lastName = "Jacques", note = "Yeet")
            )
            if(patient == null){
                Log.i("ICURE API","Couldn't get patient")
                updateDataStatus(DeviceDataStatus.ERROR)
                return
            }



            val newCid = defaultCryptoService.strongRandom.randomUUID()
            sharedPreferences.cid = newCid

            val newTopicUuid = defaultCryptoService.strongRandom.randomUUID()
            sharedPreferences.topic = root_topic + newTopicUuid

            val createdContact = createContact(patient)
            if(createdContact == null){
                Log.i("ICURE API","Couldn't create contact")
                updateDataStatus(DeviceDataStatus.ERROR)
                return
            }


            createdContact.let {
                symmetricKey = getContactSymmetricKey(createdContact)
                /*symmetricKey = IcureSdk!!.contact.getEncryptionKeyOf(createdContact).s*/
                val byteArrayKey = hexToByteArray(symmetricKey!!)


                /*Log.i("KEY", symmetricKey.toString())*/
                symmetricKey?.let {
                    try {
                        val currentState = _uiState.value;

                        // users configured a new device , a new cid has to be used


                        val bluetoothConfigData = BluetoothConfigData(sharedPreferences.cid!!,userId!!,userPassword!!,byteArrayKey,sharedPreferences.topic!!) // testKey!!
                        _uiState.value = currentState.copy(deviceConfigData = bluetoothConfigData,deviceDataStatus = DeviceDataStatus.READY)
                        Log.i("ICURE DATA CONFIG","Got device config data")
                        Log.i("ICURE DATA CONFIG", sharedPreferences.cid.toString())

                    }catch (e : Exception){
                        updateDataStatus(DeviceDataStatus.ERROR)
                        Log.i("ICURE DATA CONFIG","Couldn't get device config data")
                    }
                }?: Log.i("ICURE API", "Couldn't retreive symmetric key")
            }?:Log.i("ICURE API","Couldn't get contact ")
    }


    suspend fun getContactSymmetricKey(contact: DecryptedContact) : String?{
        return api.contact.getEncryptionKeysOf(contact).firstOrNull()?.s



    }

    suspend fun getContactById(contactId : String) : DecryptedContact{
        return api.contact.getContact(contactId)

    }
    fun updateDataStatus(status : DeviceDataStatus){
        val currentState = _uiState.value;
        _uiState.value = currentState.copy(deviceDataStatus = status)
    }


    @OptIn(InternalIcureApi::class)
    override suspend fun handleKeyStorage(){
        val icureFacade = IcureStorageFacade(
            JsonAndBase64KeyStorage(storage),
            storage,
            DefaultStorageEntryKeysFactory,
            defaultCryptoService,
            false
        )
        icureFacade.saveEncryptionKeypair(
            dataOwnerId,
            defaultCryptoService.rsa.loadKeyPairPkcs8(RsaAlgorithm.RsaEncryptionAlgorithm.OaepWithSha256, hexToByteArray(privateKey)),
            true
        )
    }



    suspend fun testDecryption(){
        try {
            val retrievedContact1 = api.contact.getContact(cidToTestDecrypt)

            Log.i("Test Decryption",retrievedContact1.toString())

        }catch (e : Exception){
            e.printStackTrace()
            Log.i("Test Decryption",e.toString())
        }

    }


}