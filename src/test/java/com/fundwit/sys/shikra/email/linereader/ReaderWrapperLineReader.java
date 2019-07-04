package com.fundwit.sys.shikra.email.linereader;

import java.io.BufferedReader;
import java.io.IOException;

public class ReaderWrapperLineReader implements LineReader {
    private BufferedReader reader;
    public ReaderWrapperLineReader(BufferedReader reader) {
        this.reader = reader;
    }
    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }
}