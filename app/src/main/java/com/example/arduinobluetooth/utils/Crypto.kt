package com.example.arduinobluetooth.utils

import java.nio.ByteBuffer
import java.util.UUID
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class Crypto {
    private val keyGenerator = KeyGenerator.getInstance("AES")
    private var secretKey : SecretKey? = null

    init {
        keyGenerator.init(256)
    }
    fun generateAES256():SecretKey?{
        secretKey = keyGenerator.generateKey()
        return secretKey
    }


    fun uuid128ToByteArray(uuid: UUID): ByteArray {
        val buffer = ByteBuffer.wrap(ByteArray(16))
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        return buffer.array()
    }

    fun keyToByteArray(key:SecretKey) : ByteArray{
        return key.encoded
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



}