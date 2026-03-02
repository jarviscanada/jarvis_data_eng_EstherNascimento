package ca.jrvs.apps.grep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);

    private String regex;
    private String rootPath;
    private String outFile;

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (Exception ex) {
            javaGrepImp.logger.error("Error: Unable to process", ex);
        }
    }

    @Override
    public void process() throws IOException {
        List<String> matchedLines = new ArrayList<>();
        // Traverse directory recursively
        for (File file : listFiles(rootPath)) {
            // Read file line by line
            for (String line : readLines(file)) {
                // Filter by regex pattern
                if (containsPattern(line)) {
                    matchedLines.add(line);
                }
            }
        }
        // Save to file
        writeToFile(matchedLines);
    }

    @Override
    public List<File> listFiles(String rootDir) {
        List<File> fileList = new ArrayList<>();
        File root = new File(rootDir);
        File[] files = root.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursive call for sub-directories
                    fileList.addAll(listFiles(file.getAbsolutePath()));
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    @Override
    public List<String> readLines(File inputFile) {
        List<String> lines = new ArrayList<>();
        // Using BufferedReader for efficiency
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            logger.error("Error reading file: " + inputFile.getName(), e);
        }
        return lines;
    }

    @Override
    public boolean containsPattern(String line) {
        // Regex matching logic
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        // BufferedWriter for efficient writing
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    @Override
    public String getRootPath() { return rootPath; }

    @Override
    public void setRootPath(String rootPath) { this.rootPath = rootPath; }

    @Override
    public String getRegex() { return regex; }

    @Override
    public void setRegex(String regex) { this.regex = regex; }

    @Override
    public String getOutFile() { return outFile; }

    @Override
    public void setOutFile(String outFile) { this.outFile = outFile; }
}