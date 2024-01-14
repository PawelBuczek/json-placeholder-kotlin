package service


import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.IOUtils
import exception.DuplicateIdException
import post.Post
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

class DataService(mapper: ObjectMapper) {
    private val mapper: ObjectMapper

    init {
        this.mapper = mapper
    }

    @Throws(IOException::class)
    fun getJsonFromUrlAddress(urlAddress: String?): String {
        URL(urlAddress).openStream().use { inputStream ->
            return IOUtils.toString(
                inputStream,
                StandardCharsets.UTF_8
            )
        }
    }

    @Throws(DuplicateIdException::class)
    fun mapJsonToPosts(jsonPosts: String?): List<Post> {
        val posts: List<Post> = mapper.readValue(jsonPosts, object : TypeReference<List<Post>>() {})
        val setOfPostIds = posts.map { it.id }.toSet()
        if (setOfPostIds.size < posts.size) {
            throw DuplicateIdException("Returned jsonObject contains duplicated post ids")
        }
        return posts
    }

    @Throws(IOException::class)
    fun savePostsToFiles(posts: List<Post>, folderPath: String) {
        for (post in posts) {
            val filePath = Paths.get(folderPath, post.id.toString() + ".json")
            mapper.writeValue(filePath.toFile(), post)
        }
    }
}