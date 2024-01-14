import service.FileService

fun main() {
    val fileService = FileService()
    println(fileService.downloadPostsToJsonFiles())
}