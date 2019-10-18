package com.vesoft.nebula.tools.reader;

import com.vesoft.nebula.tools.Options;

public class ReaderFactory {

    public enum ReaderType {
        CSV("csv", 1);

        private String type;
        private int index;

        ReaderType(String type, int index) {
            this.type = type;
            this.index = index;
        }
    }

    public static Reader get(ReaderType type, Options options) throws Exception {
        Reader reader;
        switch (type) {
            case CSV:
                reader = new CsvReader(options.getFile(), options.getBatchSize());
                reader.init();
                return reader;

            default:
                throw new Exception(String.format("No such reader: %s", type));
        }
    }
}
