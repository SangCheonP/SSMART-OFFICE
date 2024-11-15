package org.ssmartoffice.nfctokenservice.controller.port

import org.ssmartoffice.nfctokenservice.controller.request.TokenCreateRequest
import org.ssmartoffice.nfctokenservice.controller.request.TokenDeleteRequest

interface NfcTokenService {
    fun createToken(tokenCreateRequest: TokenCreateRequest): String
    fun deleteToken(tokenDeleteRequest: TokenDeleteRequest)
}