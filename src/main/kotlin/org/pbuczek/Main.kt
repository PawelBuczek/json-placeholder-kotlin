package org.pbuczek

import FileService

fun main() {
    val fileService = FileService()
    println(fileService.downloadPostsToJsonFiles())
}