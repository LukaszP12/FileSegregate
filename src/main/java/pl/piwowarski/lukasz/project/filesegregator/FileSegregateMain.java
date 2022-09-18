package pl.piwowarski.lukasz.project.filesegregator;

import java.io.IOException;

public class FileSegregateMain {
    public static void main(String[] args) throws IOException {
        FileSegregate fileSegregate = new FileSegregate("HOME", "DEV", "TEST");
        fileSegregate.segregate();
    }
}
