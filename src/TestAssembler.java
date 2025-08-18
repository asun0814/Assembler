/**
* Test program for CS318 Programming Assignment 2.
*
* The Assembler.java file that you submit for Programming Assignment 2 should
* pass all of these tests, with no modifications made to this file.
*
* A good strategy for working on this assignment is to work on passing each
* of the tests in order. Comment out Tests 2 through 4, and work on passing
* Test 1. Then work on passing Test 2, etc.
*
*/

import java.io.*;
import java.util.Scanner;

public class TestAssembler {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        System.out.println("Starting Test 1");
        Assembler.assemble("testProg1.s", "testProg1.data", "testProg1.code");
        compareFiles("testProg1.data", "correct_testProg1.data");
        compareFiles("testProg1.code", "correct_testProg1.code");
        System.out.println("Finished Test 1\n");

        System.out.println("Starting Test 2");
        Assembler.assemble("testProg2.s", "testProg2.data", "testProg2.code");
        compareFiles("testProg2.data", "correct_testProg2.data");
        compareFiles("testProg2.code", "correct_testProg2.code");
        System.out.println("Finished Test 2\n");

        System.out.println("Starting Test 3");
        Assembler.assemble("testProg3.s", "testProg3.data", "testProg3.code");
        compareFiles("testProg3.data", "correct_testProg3.data");
        compareFiles("testProg3.code", "correct_testProg3.code");
        System.out.println("Finished Test 3\n");

        System.out.println("Starting Test 4");
        Assembler.assemble("testAllProg.s", "testAllProg.data", "testAllProg.code");
        compareFiles("testAllProg.data", "correct_testAllProg.data");
        compareFiles("testAllProg.code", "correct_testAllProg.code");
        System.out.println("Finished Test 4\n");
    }

    /**
    * Performs a line-by-line comparison of the contents of two files. Prints
    * a messages if there is a difference on a line of the two files.
    *
    * @param file1 One of the files with contents to compare.
    * @param file2 The other file with contents to compare.
    */
    public static void compareFiles(String file1, String file2) throws FileNotFoundException {
        Scanner input1 = new Scanner(new File(file1));
        Scanner input2 = new Scanner(new File(file2));
        String line1, line2;
        int lineNum = 1;

        System.out.println("**** Comparing files: " + file1 + " and " + file2);

        // Read both files until reach the end of one
        while(input1.hasNextLine() && input2.hasNextLine()) {

            // get the next line from both files
            // remove any leading or trailing whitespace
            line1 = input1.nextLine().trim();
            line2 = input2.nextLine().trim();

            // Print an error message if lines have different contents
            if(!line1.equals(line2)) {
                System.out.println("ERROR files not same on line number " + lineNum);
                System.out.println(file1 + ": " + line1);
                System.out.println(file2 + ": " + line2);
            }
            lineNum++;
        }

        // Print an error message if one of the files has more lines
        if(input1.hasNextLine()) {
            System.out.println("ERROR compareFiles file not finished: " + file1);
        }
        if(input2.hasNextLine()) {
            System.out.println("ERROR compareFiles file not finished: " + file2);
        }
    }
}
