package com.example.arduinobluetooth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.arduinobluetooth.data.BluetoothConfigData
import com.example.arduinobluetooth.utils.BluetoothState
import com.icure.kryptom.crypto.RsaAlgorithm
import com.icure.kryptom.crypto.defaultCryptoService
import com.icure.kryptom.utils.hexToByteArray
import com.icure.sdk.api.IcureApi
import com.icure.sdk.auth.UsernamePassword

import com.icure.sdk.model.HexString
import com.icure.sdk.model.Patient
import com.icure.sdk.model.Contact
import com.icure.sdk.storage.IcureStorageFacade
import com.icure.sdk.storage.impl.DefaultStorageEntryKeysFactory
import com.icure.sdk.storage.impl.JsonAndBase64KeyStorage
import com.icure.sdk.storage.impl.VolatileStorageFacade
import com.icure.sdk.utils.InternalIcureApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


import java.util.UUID

data class LoginUIState(
    val apiInitalized : Boolean,
    val deviceDataReady: Boolean,
    val deviceConfigData: BluetoothConfigData
)


class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState(false,false,BluetoothConfigData("","","","")))
    val uiState : StateFlow<LoginUIState> = _uiState.asStateFlow();
    

    private var privateKey = "308204bd020100300d06092a864886f70d0101010500048204a7308204a3020100028201010092c7214c58f92670ab6ed1436045e1cc2b3b779f1905ca9d59b53838743ef355d6618a9d1a2b4c2c4e8f67eee253bb38e28d2b19378789ba5be7ff7546bb5ff20f55529136ef1277be61727788fce8ce1b79360bc6e2b4f42bd662c295aed258e54b38bcc96e215b6130127b67eda77756497bf5a5594d1a75b26b9a62a0ffef325263af706ed1318588fa7771d6584d4c4dac77b74468d0d632d2ae1733fee75af91b7e76b2fa6b4220d74305268bb003479fa8525784e38e598f8d627cc8986aaef0a52d6434a5f62a57d76232e8971b76b80500036008e1df1b12c8f46fe7fee96acc7b18ee40b4eeb772bdf0d65a35b7d8d81c2176608a77a8c1432962cb02030100010282010000a0fdba80b09e00b1671bb4d593d9e56ed747d3b87948ca561807d304734f89c4c285d12234f3d046505d649ae4682f185b841250cb269b5b896422f4a52f8633116231db60d6ac0914b2192e5ded63e562884d125284849be3d3c2706f9076faa0fe339f38f6d6bdb6f2a18f41a3ea404db76a56495984402eab40f1a497205c4ef51ac2e45e065d978d6e6d9133328a907d222ec45de2749311fee050a93b058a1c204a7e1f11248e25708e390507384fef79cb139a180fe7008603e9e09cb49d619bf49c7bb94ebad56e8366fae2ed68901aa8d70ec64c6c26c01cd7bb2649c37b4d522d44ecb2399deda0a77bb82ce976bcbe7ece2fb03a9eeee141ac4902818100cd0eb45f52ca64b6ae94cf66d1f8f9e2dbe6a875c14f6db121658d728c720350933d0b3b058268a0b76dbbc44d18b209c6c3d15ad20d96859f7b8b452821b7b42eb121c517c6dce0aefe4c5a808a3f391635b86606148fcadb30be4a74ca58fe2055acd64c9e46e63712f344faad9d4470e44bfe039d9a99784d1a29c4da085902818100b73df204271fed75f5c8f2cc767aee9ead3e8f3bf1c1705bea6f464ce0433dde88105a1efffa3dfee8206f825a0dba8a556b08df3eb712864406a0cf830a7dbcbe723f754b83abad4478140532ac126e6752face5ae94ad60bf4a5a381f8eb1b8e4a0c8c3aa6b2c122ce238603e46a02e0f64cd108ead95eb4763dbea85c5fc302818076a4de6a14fdf3bddb4f509d3e3a1f634a36b01bc9e88a2025a19b70bea02d18cec8501a2b3ae40cb174f56243f558f5dd22699c71d2d8d7a18a1aefbda39ca5cd2a41cec6ce11f89267a287eebd8e8d1a33a4e8aaf5f4a8bb9ffd4a193f06b4aac2c7890ab5bd71f045afa9a69d135489391e5aef7e9df7d42a504c0090e2a902818100b19ba71283619ce632df1d0734e79e95ca51d2bf456a3b1fbc3132b09cc650177b466e3391e4d9665353ed01298c18fcb02258ad19ba9487f3338176e10ab028db671d3ef17ca450030768504977233e4383d8795f00b47a9787ff6c6634009deae2b71acc567af85c65f564962d34dba974d1abe2bacf09ce5cb5283143a7c502818078b8778a2df003696f21efe4a610e20ddcadfe0a4261ff703fab0fc36eaedea6923aad2a3301bf81694b84af6bb4a970c3c517f317e7fed155120476444297f0b1917288bc41a1e3554b5d9b1ef990d49b4c7f2b690fd06ed7fb28e66228601c1ea6af9d2d865203c667fa86c6e66fcc34766bc06d4dcea847f54ff9fb1e50c4"

    private var icureApi : IcureApi? = null
    private var storage = VolatileStorageFacade()
    private var dataOwnerid = defaultCryptoService.strongRandom.randomUUID()


    private var patientId : String? = "e1116a8d-ca50-4aa5-b10a-dfa7738d66d5" // to avoid creating a new patient every time
    private var patient : Patient? = null
    private var contact : Contact? = null



    private val cid = defaultCryptoService.strongRandom.randomUUID()
    private var userId : String? = null
    private var userPassword : String? = null
    private var symmetricKey : String? = null


    suspend fun apiInitialize(username : String, password : String) {
        userId = username
        userPassword = password
        try {
            icureApi = IcureApi.initialise(
                "https://api.icure.cloud",
                UsernamePassword(username, password),
                storage
            )
            val currentState =_uiState.value
            _uiState.value = currentState.copy(apiInitalized = true)
            Log.i("ICURE API INIT", "Api successfuly initalized")
        }catch(e : Exception){
            Log.i("ICURE API INIT",e.toString())
        }
    }

    suspend fun createPatient(firstName: String, lastName : String) : Patient? {

        val createdPatient = icureApi!!.patient.encryptAndCreate(
            icureApi!!.patient.initialiseEncryptionMetadata(
                Patient(
                    id = dataOwnerid,
                    firstName = firstName,
                    lastName = lastName,
                    note = "Yeet"
                )
            )
        )
        createdPatient?.let {
            val retrievedPatient = icureApi!!.patient.getAndDecrypt(createdPatient.id)
            patient = retrievedPatient
            return retrievedPatient
        }
        return null
    }


    suspend fun createContact(patient : Patient) : Contact?{
        val createdContact = icureApi!!.contact.encryptAndCreate(
            icureApi!!.contact.initialiseEncryptionMetadata(
                Contact(
                    id = cid,
                    descr = "ASCASDFASDASDQWEQW",
                ),
                patient
            )
        )
        createdContact?.let {
            val retrievedContact = icureApi!!.contact.getAndDecrypt(createdContact.id)
            contact = retrievedContact
            return  retrievedContact
        }
        return null
    }

    @OptIn(InternalIcureApi::class)
    suspend fun getDeviceConfigData() {
        icureApi?.let {
            val patient = createPatient("Jean","Jacques")
            if(patient == null){
                Log.i("ICURE API","Couldn't get patient")
                return
            }

            val createdContact = createContact(patient)
            if(createdContact == null){
                Log.i("ICURE API","Couldn't create contact")
                return
            }


            createdContact.let {
                symmetricKey = icureApi!!.contact.getEncryptionKeyOf(createdContact).s
                Log.i("KEY", symmetricKey.toString())
                handleKeyStorage()
                symmetricKey?.let {
                    try {
                        val currentState = _uiState.value;
                        val bluetoothConfigData = BluetoothConfigData(cid,userId!!,userPassword!!,symmetricKey!!) // testKey!!
                        _uiState.value = currentState.copy(deviceConfigData = bluetoothConfigData,deviceDataReady = true)
                        Log.i("ICURE DATA CONFIG","Got device config data")
                    }catch (e : Exception){
                        Log.i("ICURE DATA CONFIG","Couldn't get device config data")
                    }
                }
            }?:Log.i("ICURE API","Couldn't get contact ")
        }
    }


    @OptIn(InternalIcureApi::class)
    suspend fun handleKeyStorage(){
        val icureFacade = IcureStorageFacade(
            JsonAndBase64KeyStorage(storage),
            storage,
            DefaultStorageEntryKeysFactory,
            defaultCryptoService,
            false
        )
        icureFacade.saveEncryptionKeypair(
            dataOwnerid,
            defaultCryptoService.rsa.loadKeyPairPkcs8(RsaAlgorithm.RsaEncryptionAlgorithm.OaepWithSha256, hexToByteArray(privateKey)),
            true
        )
    }

}