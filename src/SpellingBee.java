/*
 * File: SpellingBee.java
 * ----------------------
 * This program contains the starter file for the SpellingBee application.
 * BE SURE TO CHANGE THIS COMMENT WHEN YOU COMPLETE YOUR SOLUTION.
 */

//import edu.willamette.cs1.spellingbee.SpellingBeeGraphics; // Struggle: this was throwing things off!
import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

public class SpellingBee {

    public void run() {
        sbg = new SpellingBeeGraphics();
        totalScore = 0;
        // Add two interactors to the control strip at the bottom of the window
        // The calls to addField() and addButton() specify the actions that occur when the user triggers events
        sbg.addField("Puzzle", (s) -> puzzleAction(s)); // Calls puzzleAction(), passing the puzzle string
        sbg.addButton("Solve", (s) -> solveAction()); // Calls solveAction()
        sbg.addField("Word", (s) -> wordAction(s), 7);
    }

    private void puzzleAction(String s) {
        String error = validatePuzzle(s);
        if (error == null) {
            sbg.setBeehiveLetters(s); // Display puzzle letters on the screen
            puzzle = sbg.getBeehiveLetters().trim(); // Fetch data at the top level, then pass it down to helpers!
        } else
            sbg.showMessage(error, Color.RED); // Tells the user WHY the puzzle failed
    }

    private void solveAction() {
        // getBeehiveLetters() returns default value (spaces) if Solve is clicked before a valid puzzle is set
        if (puzzle.isEmpty()) {
            sbg.showMessage("Please enter a puzzle first.", Color.RED);
            return;
        }
        solutions = new ArrayList<>(validateWords(puzzle));
        for (String solution : solutions) {
            int score = scoreWord(solution, puzzle); totalScore += score;
            Color color = isPangram(solution, puzzle) ? Color.BLUE : Color.BLACK;
            sbg.addWord(solution + " (" + score + ")", color);
        }
        sbg.showMessage(solutions.size() + " words; " + totalScore + " points", Color.BLACK);
        // Reset score after solving
//        sbg.clearWordList();
        totalScore = 0;
    }

    private void wordAction(String s) {
        String attempt = sbg.getField("Word");

        if (puzzle.isEmpty()) {
            sbg.showMessage("Please enter a puzzle first.", Color.RED);
            return;
        }
        // Check legality before traversing the list of words to validate
        if (isWordLegal(attempt, puzzle)) {
            if (solutions.contains(attempt)) {
                int score = scoreWord(attempt, puzzle); totalScore += score;
                Color color = isPangram(attempt, puzzle) ? Color.BLUE : Color.BLACK;
                sbg.addWord(attempt + " (" + score + ")", color);
                solutions.remove(attempt);
                sbg.showMessage(solutions.size() + " words; " + totalScore + " points", Color.BLACK);
                sbg.setField("Word", ""); // UX feature: clear field after an acceptable word is entered
            }
        }
    }

    // Decompose the puzzleAction problem: check whether a puzzle is legal
    private String validatePuzzle(String s) {
        if (s.length() == 7) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);

                if (!Character.isAlphabetic(c)) return "Puzzle must contain only letters."; // A non-letter found

                String seen = s.substring(0, i); // Characters seen before index i
                if (seen.indexOf(c) != -1) return "Puzzle must not contain duplicate letters."; // Duplicate letter found
            }
        } else return "Puzzle must be exactly 7 letters."; // 7-character length not met
        return null; // Passed all criteria
    }

    // Decompose the solveAction problem: read the dictionary into a list
    private ArrayList<String> readDictionary() {
        ArrayList<String> entries = new ArrayList<>();
        Scanner dictionary = null;
        try {
            dictionary = new Scanner(new File(ENGLISH_DICTIONARY)); // Wrapped with a try-catch-throw
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while(dictionary.hasNext()) {
            String entry = dictionary.next(); // Process dictionary entry
            entries.add(entry);
        }
        dictionary.close();
        return entries;
    }

    // Decompose the solveAction problem: check whether a particular word meets the requirements
    private boolean isWordLegal(String word, String puzz) {
        if (word.length() >= 4) {
            for (int i = 0; i < word.length(); i++) {
                // Make character comparison case-insensitive
                if (puzz.indexOf(Character.toUpperCase(word.charAt(i))) == -1) return false; // Illegal letter found
            }
            // Make character comparison case-insensitive
            if (word.indexOf(Character.toLowerCase(puzz.charAt(0))) == -1) return false; // Missing center letter
        } else return false; // Shorter than 4 letters
        return true; // Passed all criteria
    }

    // Decompose the solveAction problem: read the dictionary and check each word
    private ArrayList<String> validateWords(String puzz) {
        ArrayList<String> words = new ArrayList<>(readDictionary()); // Assign a shallow copy of the return ArrayList
        ArrayList<String> legalWords = new ArrayList<>();

        for (String word : words) {
            if (isWordLegal(word, puzz)) legalWords.add(word);
        }
        return legalWords;
    }

    // Decompose the solveAction problem: display scores following the words that they credit
    private int scoreWord(String word, String puzz) {
        int score = 1;
        if (word.length() > 4) {
            score *= word.length(); // Words 4 letters or longer score a point per letter
            // A 7-letter word is likely a pangram
            if (word.length() >= 7) {
                for (int i = 0; i < puzz.length(); i++) {
                    // Make character comparison case-insensitive
                    if (word.indexOf(Character.toLowerCase(puzz.charAt(i))) == -1) return score; // Not a pangram
                }
                return score + 7; // 7-point pangram bonus applied
            }
        }
        return score; // Base score
    }

    // Decompose the solveAction problem: color words accordingly
    private boolean isPangram(String word, String puzz) {
        if (word.length() >= 7) {
            for (int i = 0; i < puzz.length(); i++) {
                if (word.indexOf(Character.toLowerCase(puzz.charAt(i))) == -1) return false; // Not a pangram
            }
            return true; // A pangram
        } else return false; // Word does not contain all puzzle letters
    }

    // Decompose the wordAction problem: find the word in the puzzle
    private String findWord(String w, ArrayList<String> l) {
        return "";
    }

/* Constants */

    private static final String ENGLISH_DICTIONARY = "EnglishWords.txt";

/* Private instance variables */

    private SpellingBeeGraphics sbg;
    private String puzzle;
    private ArrayList<String> solutions;
    private int totalScore;

/* Startup code */

    public static void main(String[] args) {
        new SpellingBee().run();
    }
}
