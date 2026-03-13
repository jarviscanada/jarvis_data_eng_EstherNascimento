package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaGrepLambdaImp extends JavaGrepImp {

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrepLambdaImp regex rootPath outFile");
        }

        JavaGrepLambdaImp grepLambdaImp = new JavaGrepLambdaImp();
        grepLambdaImp.setRegex(args[0]);
        grepLambdaImp.setRootPath(args[1]);
        grepLambdaImp.setOutFile(args[2]);

        try {
            grepLambdaImp.process();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void process() throws IOException {
        List<String> matchedLines = listFiles(getRootPath()).stream()
                .flatMap(file -> readLines(file).stream())
                .filter(this::containsPattern)
                .collect(Collectors.toList());

        writeToFile(matchedLines);
    }

    @Override
    public List<File> listFiles(String rootDir) {
        try {
            // Files.walk creates a Stream of Paths recursively
            return Files.walk(Paths.get(rootDir))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Unable to list files in " + rootDir, e);
        }
    }

    @Override
    public List<String> readLines(File inputFile) {
        try {
            return Files.lines(inputFile.toPath()).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading lines from " + inputFile.getName(), e);
        }
    }
}
