package service

import com.fasterxml.jackson.databind.ObjectMapper
import lombok.SneakyThrows
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream


internal class FileServiceTest {
    @AfterEach
    @SneakyThrows
    fun tearDown() {
        FileUtils.deleteDirectory(File(folderPath))
    }

    @Test
    fun shouldSaveJsonFileWhenGivenOnePost() {
        // given
        val fileService = FileService()
        mockGetJsonFromUrlAddressMethod(
            fileService,
            "[{\"userId\": 1, \"id\": 1, \"title\": \"Test Title\", \"body\": \"Test Body\"}]"
        )

        // when
        folderPath = fileService.downloadPostsToJsonFiles()

        // then
        val expectedFileContents = "{\"userId\":1,\"id\":1,\"title\":\"Test Title\",\"body\":\"Test Body\"}"
        assertPostFileIsCorrect("$folderPath/1.json", expectedFileContents)
    }

    @Test
    fun shouldSaveThreeJsonFilesWhenGivenThreePosts() {
        // given
        val fileService = FileService()
        mockGetJsonFromUrlAddressMethod(
            fileService,
            "[{\"userId\": 1, \"id\": 1, \"title\": \"Test Title1\", \"body\": \"Test Body\"}," +
                    "{\"userId\": 1, \"id\": 2, \"title\": \"Test Title2\", \"body\": \"Test Body\"}," +
                    "{\"userId\": 2, \"id\": 3, \"title\": \"Test Title3\", \"body\": \"Test Body\"}]"
        )

        // when
        folderPath = fileService.downloadPostsToJsonFiles()

        // then
        val expectedFileOneContents = "{\"userId\":1,\"id\":1,\"title\":\"Test Title1\",\"body\":\"Test Body\"}"
        val expectedFileTwoContents = "{\"userId\":1,\"id\":2,\"title\":\"Test Title2\",\"body\":\"Test Body\"}"
        val expectedFileThreeContents = "{\"userId\":2,\"id\":3,\"title\":\"Test Title3\",\"body\":\"Test Body\"}"
        assertPostFileIsCorrect("$folderPath/1.json", expectedFileOneContents)
        assertPostFileIsCorrect("$folderPath/2.json", expectedFileTwoContents)
        assertPostFileIsCorrect("$folderPath/3.json", expectedFileThreeContents)
    }

    @Test
    fun shouldPrintStackTraceWhenResponseIsEmpty() {
        // given
        val fileService = FileService()
        mockGetJsonFromUrlAddressMethod(
            fileService,
            ""
        )

        // when
        fileService.downloadPostsToJsonFiles()
        assert(
            (errContent.toString().contains(
                "com.fasterxml.jackson.databind.exc.MismatchedInputException: No content to map due to end-of-input"
            ))
        )
    }

    @Test
    fun shouldThrowExceptionWhenResponseIsNull() {
        // given
        val fileService = FileService()
        mockGetJsonFromUrlAddressMethod(
            fileService,
            null
        )

        // when
        val exception: Exception =
            Assertions.assertThrows(IllegalArgumentException::class.java, fileService::downloadPostsToJsonFiles)
        assert((exception.message!!.contains("argument \"content\" is null")))
    }

    @Test
    fun shouldPrintStackTraceWhenResponseHasWrongStructure() {
        // given
        val fileService = FileService()
        mockGetJsonFromUrlAddressMethod(
            fileService,
            "[{\"wrong\": 1, \"id\": 1, \"title\": \"Test Title\", \"body\": \"Test Body\"}]"
        )

        // when
        folderPath = fileService.downloadPostsToJsonFiles()
        assert(
            (errContent.toString().contains(
                "com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field \"wrong\""
            ))
        )
    }

    @Test
    fun shouldPrintStackTraceWhenIdIsDuplicated() {
        // given
        val fileService = FileService()
        mockGetJsonFromUrlAddressMethod(
            fileService,
            ("[{\"userId\": 1, \"id\": 1, \"title\": \"Test Title1\", \"body\": \"Test Body\"}," +
                    "{\"userId\": 1, \"id\": 2, \"title\": \"Test Title2\", \"body\": \"Test Body\"}," +
                    "{\"userId\": 2, \"id\": 1, \"title\": \"Test Title3\", \"body\": \"Test Body\"}]")
        )

        // when
        folderPath = fileService.downloadPostsToJsonFiles()
        assert(
            (errContent.toString().contains(
                "DuplicateIdException: Returned jsonObject contains duplicated post ids"
            ))
        )
    }

    @SneakyThrows
    private fun assertPostFileIsCorrect(filePath: String, expectedFileContents: String) {
        val file = File(filePath)
        Assertions.assertTrue(file.exists())
        Assertions.assertEquals(expectedFileContents, FileUtils.readFileToString(file, "utf-8"))
    }

    @SneakyThrows
    private fun mockGetJsonFromUrlAddressMethod(fileService: FileService, mockedJsonResponse: String?) {
        val dataService = Mockito.spy(DataService(ObjectMapper()))
        Mockito.`when`(dataService.getJsonFromUrlAddress(FileService.JSON_PLACEHOLDER_POSTS_URL_ADDRESS))
            .thenReturn(mockedJsonResponse)
        fileService.setDataService(dataService)
    }

    companion object {
        private val errContent = ByteArrayOutputStream()
        private val originalErr = System.err
        private var folderPath = ""

        @JvmStatic
        @BeforeAll
        fun setUpStreams() {
            System.setErr(PrintStream(errContent))
        }

        @JvmStatic
        @AfterAll
        fun restoreStreams() {
            System.setErr(originalErr)
        }
    }
}