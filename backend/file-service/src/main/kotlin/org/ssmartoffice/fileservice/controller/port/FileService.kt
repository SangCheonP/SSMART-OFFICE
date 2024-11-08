package org.ssmartoffice.fileservice.controller.port

import org.springframework.web.multipart.MultipartFile


interface FileService {
    fun uploadFile(file: MultipartFile): String
}