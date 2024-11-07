package org.ssmartoffice.fileservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.ssmartoffice.fileservice.controller.port.FileService

@RestController
@RequestMapping("api/v1/files")
class FileController(val fileService: FileService) {

    @GetMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile) : ResponseEntity<String> {
        println("File uploaded")
        val filaName = fileService.uploadFile(file)
        return ResponseEntity.ok().body(filaName)
    }
}