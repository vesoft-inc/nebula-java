package com.vesoft.nebula.tools.reader;

import java.util.Iterator;
import java.util.List;

public interface Reader extends Iterator<List<List<String>>> {
    void init() throws Exception;
    void close() throws Exception;
}
