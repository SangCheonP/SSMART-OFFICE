package org.ssmartoffice.fileservice.service

import io.awspring.cloud.s3.ObjectMetadata
import io.awspring.cloud.s3.S3Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.ssmartoffice.fileservice.controller.port.FileService
import java.util.*

@Service
class FileServiceImpl(
    val s3Template: S3Template,
    @Value("\${spring.cloud.aws.s3.bucket}")
    val bucketName: String
) : FileService {


    override fun uploadFile(file: MultipartFile): String {
        val fileName: String = file.originalFilename + UUID.randomUUID().toString()
        val extension: String = StringUtils.getFilenameExtension(fileName) ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")
        val s3Resource = s3Template.upload(bucketName, fileName, file.inputStream, ObjectMetadata.builder().contentType(extension).build())
        return s3Resource.url.toString()
    }
}