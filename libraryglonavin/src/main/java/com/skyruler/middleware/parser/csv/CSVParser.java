package com.skyruler.middleware.parser.csv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 该cvs Reader不通用(适用于Glonavin高铁测试)，默认会掠过第一行，每一行通过,分隔。
 * 如果要修改读取方式，请修改默认值或者使构造函数公开
 */
class CSVParser {
    private static final String DEFAULT_CHARSET = "utf8";
    private BufferedReader br;
    private boolean hasNext = true;
    private char separator;
    private int skipLines;
    private boolean linesSkiped;

    /**
     * The default separator to use if none is supplied to the constructor.
     */
    private static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default line to start reading.
     */
    private static final int DEFAULT_SKIP_LINES = 1;

    /**
     * Constructs CSVReader using a comma for the separator.
     *
     * @param reader the reader to an underlying CSV source.
     */
    CSVParser(InputStreamReader reader) {
        this(reader, DEFAULT_SEPARATOR, DEFAULT_SKIP_LINES);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     *
     * @param reader    the reader to an underlying CSV source.
     * @param separator the delimiter to use for separating entries
     * @param line      the line number to skip for start reading
     */
    private CSVParser(InputStreamReader reader, char separator, int line) {
        this.br = new BufferedReader(reader);
        this.separator = separator;
        this.skipLines = line;
    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     *
     * @return a string array with each comma-separated element as a separate
     * entry.
     * @throws IOException if bad things happen during the read
     */
    String[] readNext() throws IOException {
        String nextLine = getNextLine();
        return hasNext ? parseLine(nextLine) : null;
    }

    /**
     * Reads the next line from the file.
     *
     * @return the next line from the file without trailing newline
     * @throws IOException if bad things happen during the read
     */
    private String getNextLine() throws IOException {
        if (!this.linesSkiped) {
            for (int i = 0; i < skipLines; i++) {
                br.readLine();
            }
            this.linesSkiped = true;
        }
        String nextLine = br.readLine();
        if (nextLine == null) {
            hasNext = false;
            return null;
        }

        byte[] lineBytes = nextLine.getBytes();
        return new String(lineBytes, DEFAULT_CHARSET);
    }

    /**
     * Parses an incoming String and returns an array of elements.
     *
     * @param nextLine the string to parseLine
     * @return the spited list of elements, or null if nextLine is null
     */
    private String[] parseLine(String nextLine) {
        if (nextLine == null) {
            return null;
        }
        String[] strings = nextLine.split(String.valueOf(separator));
        return strings.length == 0 ? null : strings;
    }

    /**
     * Closes the underlying reader.
     *
     * @throws IOException if the close fails
     */
    void close() throws IOException {
        br.close();
    }
}

