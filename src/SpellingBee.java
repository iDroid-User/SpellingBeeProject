/*
 * File: SpellingBee.java
 * ----------------------
 * This program contains the starter file for the SpellingBee application.
 * BE SURE TO CHANGE THIS COMMENT WHEN YOU COMPLETE YOUR SOLUTION.
 */

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

public class SpellingBee {

    public void run() {
        sbg = new SpellingBeeGraphics();
        // Add two interactors to the control strip at the bottom of the window
        // The calls to addField() and addButton() specify the actions that occur when the user triggers events
        sbg.addField("Puzzle", (s) -> puzzleAction(s)); // Calls puzzleAction(), passing the puzzle string
        sbg.addButton("Solve", (a) -> solveAction());
        sbg.addField("Word", (w) -> wordAction(w), 7);
    }

    private void puzzleAction(String s) {
        sbg.clearWordList();
        sbg.showMessage(""); // Clear the message area
        String error = validatePuzzle(s);
        if (error == null) {
            isNewPuzzle = true;
            sbg.setBeehiveLetters(s); // Display puzzle letters on the screen; fetch data top-down to helpers!
            puzzle = s;
            solutions = new ArrayList<>(validateWords());
            foundWords = new ArrayList<>();
            totalScore = 0; // New puzzle, new score
        } else sbg.showMessage(error, Color.RED); // Tells the user WHY the puzzle failed
    }

    private void solveAction() {
        if (!isNewPuzzle) return;

        if (puzzle.isEmpty()) {
            sbg.showMessage("Please enter a puzzle first.", Color.RED);
            return;
        }
        for (String solution : solutions) {
            int score = scoreWord(solution);
            totalScore += score;
            Color color = isPangram(solution) ? Color.BLUE : Color.BLACK;
            sbg.addWord(solution + " (" + score + ")", color);
        }
        sbg.showMessage(solutions.size() + " words; " + totalScore + " points", Color.BLACK);
        isNewPuzzle = false; // Start a new puzzle to solve again
    }

    private void wordAction(String w) {
        if (puzzle.isEmpty()) {
            sbg.showMessage("Please enter a puzzle first.", Color.RED);
            return;
        }
        String error = validateInput(w);
        // Check legality before traversing the list of words to validate
        if (error == null) {
            int score = scoreWord(w);
            totalScore += score;
            Color color = isPangram(w) ? Color.BLUE : Color.BLACK;
            sbg.addWord(w + " (" + score + ")", color);
            foundWords.add(w);
            solutions.remove(w);
            sbg.showMessage(solutions.size() + " words; " + totalScore + " points", Color.BLACK);
            sbg.setField("Word", ""); // UX feature: clear field after an acceptable word is entered
        } else sbg.showMessage(error, Color.RED);
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
        entries = new ArrayList<>(); // The dictionary in an ArrayList
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
    private boolean isWordLegal(String word) {
        if (word.length() >= 4) {
            for (int i = 0; i < word.length(); i++) {
                // Make character comparison case-insensitive
                if (puzzle.indexOf(Character.toUpperCase(word.charAt(i))) == -1) return false; // Illegal letter found
            }
            // Make character comparison case-insensitive
            if (word.indexOf(Character.toLowerCase(puzzle.charAt(0))) == -1) return false; // Missing center letter
        } else return false; // Shorter than 4 letters
        return true; // Passed all criteria
    }

    // Decompose the solveAction problem: read the dictionary and check each word
    private ArrayList<String> validateWords() {
        ArrayList<String> words = new ArrayList<>(readDictionary()); // Assign a shallow copy of the return ArrayList
        ArrayList<String> legalWords = new ArrayList<>();

        for (String word : words) {
            if (isWordLegal(word)) legalWords.add(word);
        }
        return legalWords;
    }

    // Decompose the solveAction problem: display scores following the words that they credit
    private int scoreWord(String word) {
        int score = 1;
        if (word.length() > 4) {
            score *= word.length(); // Words 4 letters or longer score a point per letter
            // A 7-letter word is likely a pangram
            if (word.length() >= 7) {
                for (int i = 0; i < puzzle.length(); i++) {
                    // Make character comparison case-insensitive
                    if (word.indexOf(Character.toLowerCase(puzzle.charAt(i))) == -1) return score; // Not a pangram
                }
                return score + 7; // 7-point pangram bonus applied
            }
        }
        return score; // Base score
    }

    // Decompose the solveAction problem: color words accordingly
    private boolean isPangram(String word) {
        if (word.length() >= 7) {
            for (int i = 0; i < puzzle.length(); i++) {
                if (word.indexOf(Character.toLowerCase(puzzle.charAt(i))) == -1) return false; // Not a pangram
            }
            return true; // A pangram
        } else return false; // Word does not contain all puzzle letters
    }

    // Decompose the wordAction problem: if the word doesn't count, tell the user why
    private String validateInput(String w) {
        if (!entries.contains(w)) return "That word is not in the dictionary.";
        if (foundWords.contains(w)) return "That word has already been found.";

        if (w.length() >= 4) {
            for (int i = 0; i < w.length(); i++) {
                if (puzzle.indexOf(Character.toUpperCase(w.charAt(i))) == -1)
                    return "That word includes letters not in the beehive."; // Illegal letter found
            }
            if (w.indexOf(Character.toLowerCase(puzzle.charAt(0))) == -1)
                return "That word does not include the center letter."; // Missing center letter
        } else return "That word is shorter than 4 letters."; // Shorter than 4 letters
        return null; // Passed all criteria
    }

/* Constants */

    private static final String ENGLISH_DICTIONARY = "EnglishWords.txt";

/* Private instance variables */

    private SpellingBeeGraphics sbg;
    private String puzzle;
    private ArrayList<String> solutions, entries, foundWords;
    private int totalScore;
    private boolean isNewPuzzle; // Optimizes solveAction()

/* Startup code */

    public static void main(String[] args) {
        new SpellingBee().run();
    }
}
