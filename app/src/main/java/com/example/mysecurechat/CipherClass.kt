package com.example.mysecurechat
import android.util.Log
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.spec.IvParameterSpec

class CipherClass {

    fun generateRSAKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.genKeyPair()
    }

    fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256) // Можно использовать 128 или 256 бит
        return keyGen.generateKey()
    }

    // Шифрование сообщения с использованием AES
    fun encryptMessage(message: String, secretKey: SecretKey): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        // Генерация случайного IV
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        // Инициализация шифрования
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

        // Шифрование сообщения
        val encryptedBytes = cipher.doFinal(message.toByteArray())

        // Кодируем зашифрованное сообщение и IV в Base64
        val encryptedMessageBase64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)

        // Возвращаем зашифрованное сообщение и IV
        return Pair(encryptedMessageBase64, ivBase64)
    }

    // Расшифровка сообщения с использованием AES и IV
    fun decryptMessage(encryptedMessage: String, secretKey: SecretKey, ivBase64: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        // Декодируем IV из Base64
        val iv = Base64.decode(ivBase64, Base64.DEFAULT)
        val ivSpec = IvParameterSpec(iv)
        // Инициализация расшифровки
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        // Декодируем зашифрованное сообщение из Base64
        val encryptedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT)
        // Расшифровываем сообщение
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}