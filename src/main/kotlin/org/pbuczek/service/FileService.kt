import com.fasterxml.jackson.databind.ObjectMapper
import org.pbuczek.exception.DuplicateIdException
import org.pbuczek.service.DataService
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FileService {
    private var dataService = DataService(ObjectMapper())
    fun setDataService(dataService: DataService) {
        this.dataService = dataService
    }

    @Throws(IOException::class)
    fun createDirectory(folderPath: String) {
        try {
            val path = Paths.get(folderPath)
            Files.createDirectories(path)
        } catch (e: IOException) {
            throw IOException("Failed to create directory:" + folderPath + e.message)
        }
    }

    fun downloadPostsToJsonFiles(): String {
        var folderPath = ""
        try {
            val jsonPosts = dataService.getJsonFromUrlAddress(JSON_PLACEHOLDER_POSTS_URL_ADDRESS)
            val posts = dataService.mapJsonToPosts(jsonPosts)
            val formatter = DateTimeFormatter.ofPattern("yyyy_MM(MMM)_dd_HH_mm_ss")
            folderPath = "results/posts_" + formatter.format(LocalDateTime.now())
            createDirectory(folderPath)
            dataService.savePostsToFiles(posts, folderPath)
        } catch (e: DuplicateIdException) {
            //probably would be better to log with @SLF4J or something
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return folderPath
    }

    companion object {
        const val JSON_PLACEHOLDER_POSTS_URL_ADDRESS = "https://jsonplaceholder.typicode.com/posts"
    }
}
