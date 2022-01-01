package net.insprill.sear;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Main {

	private static final String[] ARGS = new String[0];
	private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d*\\.)?\\d+");
	private static final String GRID_SIZE_STR = "grid_size=\"%s\"";
	private static final Pattern GRID_SIZE_PATTERN = Pattern.compile(String.format(GRID_SIZE_STR, "((\\d*\\.)?\\d+)"));

	public static void main(String[] args) throws Exception {
		try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

			System.out.println("Enter the path to your Stormworks installation (e.g. C:\\Program Files (x86)\\Steam\\steamapps\\common\\Stormworks): ");
			String path = consoleReader.readLine()
					.replace("/", File.separator)
					.replace("\\", File.separator)
					+ File.separator + "rom" + File.separator + "data" + File.separator + "tiles";
			File tilesFolder = new File(path.trim());
			if (!tilesFolder.exists() || !tilesFolder.isDirectory()) {
				System.err.println("Incorrect install location! Please double check you entered it correctly.");
				main(ARGS);
				return;
			}

			System.out.println("Enter the new size of the edit areas (1-255): ");
			String size = consoleReader.readLine();
			try {
				size = Math.max(1, Math.min(Integer.parseInt(size), 255)) + "";
			} catch (NumberFormatException e) {
				System.err.println(size + " is not a number!");
				main(ARGS);
				return;
			}

			long t1 = System.currentTimeMillis();

			for (File file : tilesFolder.listFiles()) {
				modifyFile(file, size);
			}

			System.out.println("Finished in " + (System.currentTimeMillis() - t1) + "ms");
		}
	}

	public static void modifyFile(File file, String size) throws IOException {
		if (!file.getName().endsWith(".xml") || file.getName().contains("_instances"))
			return;

		System.out.println("Modifying " + file.getName());

		boolean inEditArea = false;
		boolean finishedEdit = false;

		List<String> newLines = new ArrayList<>();
		for (String line : Files.readAllLines(Path.of(file.getAbsolutePath()), StandardCharsets.UTF_8)) {
			String trimmed = line.trim();
			if (!finishedEdit && trimmed.startsWith("<edit_areas>")) {
				inEditArea = true;
			}
			if (inEditArea && trimmed.startsWith("</edit_areas>")) {
				inEditArea = false;
				finishedEdit = true;
			}

			if (inEditArea && trimmed.startsWith("<size ")) {
				line = SIZE_PATTERN.matcher(line).replaceAll(size);
			}
			if (inEditArea && trimmed.startsWith("<edit_area ")) {
				line = GRID_SIZE_PATTERN.matcher(line).replaceAll(String.format(GRID_SIZE_STR, size));
			}
			newLines.add(line);
		}
		Files.write(Path.of(file.getAbsolutePath()), newLines, StandardCharsets.UTF_8);
	}

}
