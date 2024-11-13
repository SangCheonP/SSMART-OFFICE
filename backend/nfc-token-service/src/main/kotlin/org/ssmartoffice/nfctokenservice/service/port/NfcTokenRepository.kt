package org.ssmartoffice.nfctokenservice.service.port

interface NfcTokenRepository {
    fun saveToken(email: String, token: String)
    fun expireToken(email: String)
}