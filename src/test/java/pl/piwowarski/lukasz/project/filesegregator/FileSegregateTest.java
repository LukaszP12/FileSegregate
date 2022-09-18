package pl.piwowarski.lukasz.project.filesegregator;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileSegregateTest {
    @Test
    void test() throws IOException {
        // given
        FileSegregate fileSegregate = new FileSegregate("HOME", "DEV", "TEST");

        // when
        fileSegregate.segregate();

        // then
    }
}