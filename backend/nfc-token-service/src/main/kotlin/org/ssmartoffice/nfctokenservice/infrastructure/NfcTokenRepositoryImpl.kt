package org.ssmartoffice.nfctokenservice.infrastructure

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import org.ssmartoffice.nfctokenservice.service.port.NfcTokenRepository

@Repository
class NfcTokenRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>
): NfcTokenRepository {
    override fun saveToken(email: String, token: String) {
        val valueOperations: ValueOperations<String, String> = redisTemplate.opsForValue()
        valueOperations.set("$email-nfc", token)
    }
    override fun expireToken(email: String) {
        redisTemplate.delete("$email-nfc")
    }
}