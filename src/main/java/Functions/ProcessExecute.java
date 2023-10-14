package Functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessExecute {
    public void executeProcessAsync(String command, String... arguments) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.command().addAll(List.of(arguments));
        processBuilder.redirectErrorStream(true); // Merge error and output streams

        Process process = processBuilder.start();

        // Capture the output and error streams
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // Print each line to the console
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("Process exited with error code " + exitCode);
        }

    }
}
