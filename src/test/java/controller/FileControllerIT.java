package controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import storage.Application;
import storage.database.FileData;
import storage.database.FileDataDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class FileControllerIT {

    @Autowired
    private FileDataDAO fileDataDAO;

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile file = new MockMultipartFile(
            "file",
            "file",
            "multipart/form-data",
            "test".getBytes()
    );

    @Value("${file.directory}")
    private String pathToSaveFile;

    @Test
    public void shouldSaveFile() throws Exception {

        this.mockMvc
                .perform(fileUpload("/storage/file").file(file))
                .andExpect(status().isOk());

        assertTrue(Files.exists(Paths.get((pathToSaveFile))));
    }

    @Test
    public void shouldDownloadFile() throws Exception {
        File testFile = new File(pathToSaveFile,"file");
        FileWriter writer = new FileWriter(testFile);
        writer.append("test");
        writer.close();

        MvcResult result = mockMvc
                .perform(get("/download/file")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertEquals("test", result.getResponse().getContentAsString() );
    }

    @Test
    public void testHibernateToDatabaseConnection() {
        FileData testFileData = new FileData();
        testFileData.setTitle("TestFileTest");
        fileDataDAO.save(testFileData);
    }

    @Test
    public void testHibernateSelectAll() {
        FileData testFileData = new FileData();
        testFileData.setTitle("TestFileTest");
        FileData testFileData2 = new FileData();
        testFileData2.setTitle("TestFileTest2");
        FileData testFileData3 = new FileData();
        testFileData3.setTitle("TestFileTest3");
        fileDataDAO.save(testFileData);
        fileDataDAO.save(testFileData2);
        fileDataDAO.save(testFileData3);

        List<FileData> dataSet = fileDataDAO.list();
        for( FileData c : dataSet ) {
            System.out.println(c.toString());
        }
    }

    @AfterAll
    public void clean() throws IOException {
        Files.deleteIfExists(Paths.get(pathToSaveFile + "file"));
    }

}