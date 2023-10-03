package Functions;

import java.io.IOException;
import java.util.List;

public class ProcessExecute {
    public void executeProcessAsync(String command, String... arguments) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.command().addAll(List.of(arguments));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("Process exited with error code " + exitCode);
        }

    }
}
