package main.java.spellchecker;


import java.io.*;
import java.util.*;

import main.java.spellchecker.utils.WordRecommender;

import static main.java.spellchecker.utils.Constants.*;

public class SpellChecker {

    private static WordRecommender wordRecommender = null;

    public SpellChecker() { }

    private static void init() {
        wordRecommender = new WordRecommender(DICTIONARY);
    }

    public static void main(String[] args) {

        System.out.println("##### Spell Checker ##### \n");

        init();

        // Read user file while contains a stream of space-separated words.We are going to ignore punctuation completely (i.e., input files have no punctuation),
        // so this means no periods, question marks, quotations, apostrophes-- just think of this as a stream of words--line breaks are allowed.
        System.out.println("Please enter the path to the file you want to spell check.");

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            String filePath = reader.readLine();
            File file = new File(filePath);
            boolean doesFileExist = checkIfFileExists(file);
            while(!doesFileExist) {
                System.out.println("The entered file does not exist. Please enter a valid file path.");
                filePath = reader.readLine();
                file = new File(filePath);
                doesFileExist = checkIfFileExists(file);
            }

            reader = new BufferedReader(new FileReader(filePath));
            String fileNameWithoutExtn = removeExtension(file.getName());
            String outputFilePath = filePath.replace(fileNameWithoutExtn, fileNameWithoutExtn + "_chk");
            FileWriter fileWriter = new FileWriter(outputFilePath);
            Scanner input = new Scanner(System.in);

            String line = reader.readLine();
            while (line != null) {
                input = new Scanner(line);
                while (input.hasNext()) {
                    String word  = input.next();
                    boolean isMisspelled = wordRecommender.checkIfWordIsMisspelled(word.toLowerCase());
                    if(isMisspelled) {
                        String replacementWord = displayWordSuggestions(word);
                        fileWriter.write(replacementWord + " ");
                    } else {
                        fileWriter.write(word + " ");
                    }
                }
                // Read next line
                line = reader.readLine();
                fileWriter.write("\n");
            }
            System.out.println();
            System.out.println("########################################################################################");
            System.out.println("Location of Output File: " + file.getCanonicalPath().replace(fileNameWithoutExtn, fileNameWithoutExtn + "_chk"));
            System.out.println("########################################################################################");
            fileWriter.close();
            reader.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String displayWordSuggestions(String word) {
        String replacementWord = word;
        Scanner scanner = new Scanner(System.in);
        try {
            boolean suggestionsAvailable = false;
            List<String> topWordSuggestions = wordRecommender.getWordSuggestions(word, CANDIDATE_WORD_LENGTH_DIFFERENCE, CANDIDATE_WORD_COMMON_PERCENT, TOP_WORDS_COUNT);
            System.out.println("\nThe word ‘" + word + "’ is misspelled.");
            if(topWordSuggestions.size() > 0) {
                suggestionsAvailable = true;
                System.out.println("The following suggestions are available");
                for(int i = 0; i < topWordSuggestions.size(); i++) {
                    System.out.println((i + 1) + ". ‘" + topWordSuggestions.get(i) + "’");
                }
                System.out.println("Press ‘r’ for replace, ‘a’ for accept as is, ‘t’ for type in manually.");
            } else {
                suggestionsAvailable = false;
                System.out.println("There are 0 suggestions in our dictionary for this word.");
                System.out.println("Press ‘a’ for accept as is, ‘t’ for type in manually.");
            }
            char choice = 'z';
            // Character input
            choice = scanner.next().charAt(0);
            scanner.nextLine();
            int replacementWordChoice = 0;
            boolean replayOptions = false;

            do {
                switch(Character.toLowerCase(choice)) {
                    case 'r':
                        if(!suggestionsAvailable) {
                            System.out.println("\nInvalid Input! There are no suggestions available. Press ‘a’ for accept as is, ‘t’ for type in manually.");
                            replayOptions = true;
                            choice = scanner.next().charAt(0);
                            scanner.nextLine();
                            break;
                        }
                        System.out.println("\nYour word will now be replaced with one of the suggestions");
                        System.out.println("Enter the number corresponding to the word that you want to use for replacement.");
                        // Accept suggesstion choice
                        boolean isNumeric = false;
                        while(!isNumeric) {
                            try {
                                replacementWordChoice = scanner.nextInt();
                                scanner.nextLine();
                                if(replacementWordChoice > 0 && replacementWordChoice <= topWordSuggestions.size()) {
                                    isNumeric = true;//numeric value entered, so break the while loop
                                    System.out.println("Selected word for replacement: " + topWordSuggestions.get(replacementWordChoice - 1));
                                    // Replace word with suggestion in output file.
                                    replacementWord = topWordSuggestions.get(replacementWordChoice - 1);
                                }
                            } catch(InputMismatchException ime) {
                                System.out.println("\nInvalid number entered, Please enter a valid choice!!!");
                                System.out.println("Enter the number corresponding to the word that you want to use for replacement.");
                                scanner.nextLine();
                            }
                        }
                        break;

                    case 'a':
                        // Word is written to output file as is.
                        replacementWord = word;
                        replayOptions = false;
                        break;

                    case 't':
                        System.out.println("\nPlease type the word that will be used as the replacement in the output file.");
                        // Accept replacement word.
                        replacementWord = scanner.next();
                        replayOptions = false;
                        break;

                    default:
                        System.out.println("\nIncorrect Input! Please try again.");
                        if(suggestionsAvailable) {
                            System.out.println("The following suggestions are available");
                            for(int i = 0; i < topWordSuggestions.size(); i++) {
                                System.out.println((i + 1) + ". ‘" + topWordSuggestions.get(i) + "’");
                            }
                            System.out.println("Press ‘r’ for replace, ‘a’ for accept as is, ‘t’ for type in manually.");
                        } else {
                            System.out.println("There are 0 suggestions in our dictionary for this word.");
                            System.out.println("Press ‘a’ for accept as is, ‘t’ for type in manually.");
                        }
                        break;
                }
            } while(!(Character.toLowerCase(choice) == 'r' || Character.toLowerCase(choice) == 'a' || Character.toLowerCase(choice) == 't') || replayOptions);
        } catch(Exception e) {
            System.out.println("\nAn error occcured while fetching Word Suggestions. " + e.getMessage());
        }
        return replacementWord;
    }

    private static boolean checkIfFileExists(File file) {
        try {
            if (!file.isDirectory()) {
                if (file != null && file.exists()){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
}
