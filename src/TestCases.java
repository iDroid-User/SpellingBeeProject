/*
 * File: TestCases.java
 * ----------------------
 * This program includes the test cases that ensure the functionality of the app.
 */

public class TestCases {
    public static void main(String[] args) {
        SpellingBee bee = new SpellingBee();
        bee.puzzle = "OCALYPT";

        // Test validatePuzzle()
        System.out.println("--- validatePuzzle ---");
        System.out.println(bee.validatePuzzle("OCALYPT")); // null (valid)
        System.out.println(bee.validatePuzzle("ABC"));     // "Puzzle must be exactly 7 letters."
        System.out.println(bee.validatePuzzle("AABCDEF")); // "Puzzle must not contain duplicate letters."
        System.out.println(bee.validatePuzzle("ABCDEF1")); // "Puzzle must contain only letters."

        // Test isWordLegal()
        System.out.println("--- isWordLegal ---");
        System.out.println(bee.isWordLegal("copy"));   // true (valid letters, contains center)
        System.out.println(bee.isWordLegal("cat"));    // false (too short)
        System.out.println(bee.isWordLegal("clapt"));  // false (missing the center letter)
        System.out.println(bee.isWordLegal("xyz"));    // false (illegal letters)

        // Test scoreWord()
        System.out.println("--- scoreWord ---");
        System.out.println(bee.scoreWord("typo"));     // 1 (4 letters, base score)
        System.out.println(bee.scoreWord("octal"));    // 5 (5 letters)
        System.out.println(bee.scoreWord("ocalypt")); // 14 (7 letters + 7 pangram bonus)

        // Test isPangram()
        System.out.println("--- isPangram ---");
        System.out.println(bee.isPangram("ocalypt")); // true
        System.out.println(bee.isPangram("typo"));     // false
    }
}
