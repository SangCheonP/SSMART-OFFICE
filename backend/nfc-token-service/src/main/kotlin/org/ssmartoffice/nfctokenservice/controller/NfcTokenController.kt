package org.ssmartoffice.nfctokenservice.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ssmartoffice.nfctokenservice.controller.port.NfcTokenService
import org.ssmartoffice.nfctokenservice.controller.request.TokenCreateRequest
import org.ssmartoffice.nfctokenservice.controller.request.TokenDeleteRequest
import org.ssmartoffice.nfctokenservice.global.dto.CommonResponse

@RestController
@RequestMapping("/api/v1/nfc-tokens")
class NfcTokenController(val nfcTokenService: NfcTokenService) {

    @PostMapping("/tokens")
    fun createToken(@Valid @RequestBody tokenCreateRequest: TokenCreateRequest): ResponseEntity<CommonResponse> {
        val token :String = nfcTokenService.createToken(tokenCreateRequest)
        return CommonResponse.created("토큰 생성에 성공하였습니다.", token)
    }

    @DeleteMapping("/tokens")
    fun deleteToken(@Valid @RequestBody tokenDeleteRequest: TokenDeleteRequest): ResponseEntity<CommonResponse> {
        nfcTokenService.deleteToken(tokenDeleteRequest)
        return CommonResponse.ok("토큰 삭제에 성공하였습니다.")
    }

}