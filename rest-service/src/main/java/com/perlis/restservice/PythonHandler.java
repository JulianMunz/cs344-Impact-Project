package com.perlis.restservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

/**
 * Class for running python files and retrieving their output.
 */
public class PythonHandler {
    private String fileName;

    /**
     * Constructor for the Object.
     * @param fileName File name of the python file stored in resources
     */
    public PythonHandler(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Runs and gets the output of the python file
     * @return Output in form of a string
     * @throws IOException
     */
    public String output() throws IOException {
        try {
            String pythonFile = PythonHandler.class.getResource(fileName).toURI().getPath();
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonFile);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = "";
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null) {
                output = output + readLine + "\n";
            }
            return output;
        } catch (URISyntaxException e) {
            return "File not found!";
        }

    }

}
