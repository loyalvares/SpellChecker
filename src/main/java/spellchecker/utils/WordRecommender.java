package main.java.spellchecker.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class WordRecommender {

    private static String fileName;

    private static SortedSet<String> dictionary = new TreeSet<>();

    public WordRecommender(String fileName) {
        this.fileName = fileName;
        loadDictionary();
    }

    public double getSimilarityMetric(String word1, String word2) {
        int leftSimilarityScore = getLeftSimilarityScore(word1, word2);
        int rightSimilarityScore = getRightSimilarityScore(word1, word2);
        return ((leftSimilarityScore + rightSimilarityScore) / 2.0);
    }

    public List<String> getWordSuggestions(String word, int n, double commonPercent, int topN) {
        int wordLength = word.length();
        Map<String, Double> similarityMetricScoresMap = new HashMap<>();
        List<String> wordSuggestions = new ArrayList<>();
        // If the number of character in (word - n) is less than 3, then explicitly set it to 3 as we don't want to consider words below 3 letters.
        int wordLengthLowerLimit = (wordLength - n) > 2 ? (wordLength - n) : 3;
        int wordLengthHigherLimit = wordLength + n;

        Set<Character> wordCharSet = convertStringToUniqueCharacterSet(word);
        for(String w1 : dictionary) {
            if(w1.equalsIgnoreCase("yesterday")) {
                System.out.println(w1);
            }
            if(w1.length() >= wordLengthLowerLimit && w1.length() <= wordLengthHigherLimit) {
                Set<Character> w1CharSet = convertStringToUniqueCharacterSet(w1);
                double intersectionCount = getIntersectionCount(wordCharSet, w1CharSet);
                double unionCount = getUnionCount(wordCharSet, w1CharSet);
                double candidateCommonPercent = intersectionCount / unionCount;
                if(candidateCommonPercent >= commonPercent) {
                    double similarityMetricScore = getSimilarityMetric(word, w1);
                    similarityMetricScoresMap.put(w1, similarityMetricScore);
                }
            }
        }
        similarityMetricScoresMap = similarityMetricScoresMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .limit(topN)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        wordSuggestions = new ArrayList<String>(similarityMetricScoresMap.keySet());
        return wordSuggestions;
    }

    public List<String> getWordsWithCommonLetters(String word, List<String> listOfWords, int n) {
        List<String> commonLettersWordList = new ArrayList<>();
        Set<Character> wordCharSet = convertStringToUniqueCharacterSet(word);
        for(String item : listOfWords) {
            int count = 0;
            Set<Character> itemCharSet = convertStringToUniqueCharacterSet(item);
            for(Character character : wordCharSet) {
                if(itemCharSet.contains(character)) {
                    count++;
                    if(count >= n) {
                        commonLettersWordList.add(item);
                        break;
                    }
                }
            }
        }
        return commonLettersWordList;
    }

    public String prettyPrint(List<String> list) {
        String prettyPrintString = "";
        for(int i = 0; i < list.size(); i++) {
            prettyPrintString += (i + 1) + ". " + list.get(i) + "\n";
        }
        return prettyPrintString;
    }

    private int getLeftSimilarityScore(String word1, String word2) {
        int leftSimilarityScore = 0;
        try {
            char[] word1CharArray = word1.toCharArray();
            char[] word2CharArray = word2.toCharArray();
            for(int i = 0, j = 0; i < word1CharArray.length && j < word2CharArray.length; i++, j++) {
                if(word1CharArray[i] == word2CharArray[j]) {
                    leftSimilarityScore++;
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return leftSimilarityScore;
    }

    private int getRightSimilarityScore(String word1, String word2) {
        int rightSimilarityScore = 0;
        try {
            char[] word1CharArray = word1.toCharArray();
            char[] word2CharArray = word2.toCharArray();
            for(int i = word1CharArray.length - 1, j = word2CharArray.length - 1; i >= 0 && j >= 0; i--, j--) {
                if(word1CharArray[i] == word2CharArray[j]) {
                    rightSimilarityScore++;
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return rightSimilarityScore;
    }

    private Set<Character> convertStringToUniqueCharacterSet(String string) {
        Set<Character> characterSet = new HashSet<Character>();
        for(char c : string.toCharArray()) {
            characterSet.add(c);
        }
        return characterSet;
    }

    private int getIntersectionCount(Set<Character> set1, Set<Character> set2) {
        int intersectionCount = 0;
        for(Character character : set1) {
            if(set2.contains(character)) {
                intersectionCount++;
            }
        }
        return intersectionCount;
    }

    private int getUnionCount(Set<Character> set1, Set<Character> set2) {
        Set<Character> unionSet = new HashSet<>(set1);
        unionSet.addAll(set2);
        return unionSet.size();
    }

    public static void loadDictionary() {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            dictionary = new TreeSet<>(stream.map(String::toLowerCase).collect(Collectors.toSet()));
            if(dictionary.size() == 0) {
                throw new Exception("WARNING: The dictionary loaded for the main.java.spellchecker.SpellChecker program is empty.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean checkIfWordIsMisspelled(String word) {
        if(dictionary.contains(word)) {
            return false;
        } else {
            return true;
        }
    }

    public static void main(String[] args) {
        WordRecommender wordRecommender = new WordRecommender("engDictionary.txt");
        List<String> wordList = new ArrayList<>(Arrays.asList("ban", "bang", "mange", "gang", "cling", "loo"));
        List<String> wordsWithCommonLettersList1 = wordRecommender.getWordsWithCommonLetters("cloong", wordList, 2);
        List<String> wordsWithCommonLettersList2 = wordRecommender.getWordsWithCommonLetters("cloong", wordList, 3);
        System.out.println(wordsWithCommonLettersList1);
        System.out.println(wordsWithCommonLettersList2);
        List<String> list = new ArrayList<>(Arrays.asList("biker", "tiger", "bigger"));
        String prettyPrintedString = wordRecommender.prettyPrint(list);
        System.out.println(prettyPrintedString);
    }
}
