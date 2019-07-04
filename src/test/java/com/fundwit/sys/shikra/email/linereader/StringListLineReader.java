package com.fundwit.sys.shikra.email.linereader;

import java.io.IOException;
import java.util.List;

public class StringListLineReader implements LineReader {
    private List<String> lines;
    private int index = 0;

    public StringListLineReader(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public synchronized String readLine() throws IOException {
        if(index >= lines.size()){
            return null;
        }
        return lines.get(index++);
    }

    public int getIndex() {
        return index;
    }
}