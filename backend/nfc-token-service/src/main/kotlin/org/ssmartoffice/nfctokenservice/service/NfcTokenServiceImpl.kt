package org.ssmartoffice.nfctokenservice.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.PropertyResolver
import org.springframework.stereotype.Service
import org.ssmartoffice.nfctokenservice.client.UserClient
import org.ssmartoffice.nfctokenservice.controller.port.NfcTokenService
import org.ssmartoffice.nfctokenservice.controller.request.TokenCreateRequest
import org.ssmartoffice.nfctokenservice.controller.request.TokenDeleteRequest
import org.ssmartoffice.nfctokenservice.global.const.errorcode.TokenErrorCode
import org.ssmartoffice.nfctokenservice.global.exception.RestApiException
import org.ssmartoffice.nfctokenservice.global.jwt.JwtTokenProvider
import org.ssmartoffice.nfctokenservice.service.port.NfcTokenRepository


@Service
class NfcTokenServiceImpl(
    val tokenProvider: JwtTokenProvider,
    val nfcTokenRepository: NfcTokenRepository,
    @Value("\${nfc.token.authCode}")
    val authCode: String,
    val userClient: UserClient,
) : NfcTokenService {
    override fun createToken(nfcTokenCreateRequest: TokenCreateRequest): String {

        val email = nfcTokenCreateRequest.email

        validateToken(nfcTokenCreateRequest.authCode)
        val token: String = tokenProvider.createToken(1.toString(), email)

        //검증 코드
        //userClient.getAuthentication(email, nfcTokenCreateRequest.authCode)

        nfcTokenRepository.saveToken(email, token)
        return token
    }

    override fun deleteToken(nfcTokenDeleteRequest: TokenDeleteRequest) {
        validateToken(nfcTokenDeleteRequest.authCode)

        //검증 코드
        //userClient.getAuthentication(email, nfcTokenCreateRequest.authCode)

        nfcTokenRepository.expireToken(nfcTokenDeleteRequest.email)
    }

    private fun validateToken(code: String) {
        println(authCode)
        println(code)
        if(code != authCode) {
            throw RestApiException(TokenErrorCode.NOT_MATCH_AUTHCODE)
        }
    }
}