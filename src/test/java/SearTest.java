import net.insprill.sear.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class SearTest {

	private static final String KNOWN_CORRECT_FILE_NAME = "test_file_correct";
	private static final String FILE_NAME = "test_file";
	private static final String FILE_EXTENSION = ".xml";

	@TempDir
	static Path tempDir;

	@Test
	void modifyTest() throws IOException {
		Path testFile = Files.createFile(Path.of(tempDir.toAbsolutePath() + File.separator + FILE_NAME + FILE_EXTENSION));
		Path knownCorrectFile = Files.createFile(Path.of(tempDir.toAbsolutePath() + File.separator + FILE_NAME + "_correct" + FILE_EXTENSION));

		Files.write(testFile, getFileBytes(FILE_NAME + FILE_EXTENSION));
		Files.write(knownCorrectFile, getFileBytes(KNOWN_CORRECT_FILE_NAME + FILE_EXTENSION));

		Main.modifyFile(testFile.toFile(), "255");

		Assertions.assertEquals(Files.mismatch(knownCorrectFile, testFile), -1L);
	}

	private byte[] getFileBytes(String fileName) throws IOException {
		return SearTest.class.getClassLoader().getResourceAsStream(fileName).readAllBytes();
	}

}
