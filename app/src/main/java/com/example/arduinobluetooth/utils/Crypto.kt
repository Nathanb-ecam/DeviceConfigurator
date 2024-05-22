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
        return hexString.chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }




}