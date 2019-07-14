package main.java.spellchecker.utils;

public class Constants {

    public static final String DICTIONARY = "engDictionary.txt";

    public static final char EXTENSION_SEPARATOR = '.';
    public static final char UNIX_SEPARATOR = '/';
    public static final char WINDOWS_SEPARATOR = '\\';

    // Alter these values as you please.
    public static final int CANDIDATE_WORD_LENGTH_DIFFERENCE = 3;
    public static final int TOP_WORDS_COUNT= 10;
    public static final double CANDIDATE_WORD_COMMON_PERCENT = 0.5;

}
