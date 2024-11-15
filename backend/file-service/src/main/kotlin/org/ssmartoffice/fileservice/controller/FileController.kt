package org.ssmartoffice.fileservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.ssmartoffice.fileservice.controller.port.FileService
import org.ssmartoffice.fileservice.dto.CommonResponse

@RestController
@RequestMapping("api/v1/files")
class FileController(val fileService: FileService) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile) : ResponseEntity<CommonResponse> {
        val fileName = fileService.uploadFile(file)
        return CommonResponse.created("파일 업로드 성공", fileName)
    }

}