/**
* Assembler for the CS318 simple computer simulation
*/
import java.io.*;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;


public class Assembler {
    private static List<String> assembleCode;

    /**
     * Assembles the code file. When this method is finished, the dataFile and
     * codeFile contain the assembled data segment and code segment, respectively.
     *
     * @param inFile   The pathname to the assembly language file to be assembled.
     * @param dataFile The pathname where the data segment file should be written.
     * @param codeFile The pathname where the code segment file should be written.
     */


    public static void assemble(String inFile, String dataFile, String codeFile)
            throws FileNotFoundException, IOException {

        // DO NOT MAKE ANY CHANGES TO THIS METHOD
        ArrayList<LabelOffset> labels = pass1(inFile, dataFile, codeFile);
        pass2(inFile, dataFile, codeFile, labels);
    }

    /**
     * First pass of the assembler. Writes the number of bytes in the data segment
     * and code segment to their respective output files. Returns a list of
     * code segment labels and their relative offsets.
     *
     * @param inFile   The pathname of the file containing assembly language code.
     * @param dataFile The pathname for the data segment binary file.
     * @param codeFile The pathname for the code segment binary file.
     * @return List of the code segment labels and relative offsets.
     * @throws RuntimeException if the assembly code file does not have the
     *                          correct format, or another error while processing the assembly code file.
     */
    private static ArrayList<LabelOffset> pass1(String inFile, String dataFile, String codeFile)
            throws IOException {
        // PROGRAMMING ASSIGNMENT 2: COMPLETE THIS METHOD
        // Track the number of words based on the commas
        int wordCount = 0;
        // Byte size for data segment
        int byteSize1 = 0;
        // Byte size for the code segment
        int byteSize2 = 0;
        int insCount = 0;
        // Returned list of the code segment labels and relative offset
        ArrayList<LabelOffset> labels = new ArrayList<>();
        // Targets
        char target1 = ',';
        String [] target2 = {"ADD", "SUB", "AND", "ORR", "LDR", "STR", "CBZ"};

        // Create two new files to write the data and code segment
        File newData = new File(dataFile);
        File newCode = new File(codeFile);

        BufferedReader br = null;
        PrintWriter writer1 = null;
        PrintWriter writer2 = null;
        // Open and read the file
        try {
            FileReader fr = new FileReader(inFile);
            br = new BufferedReader(fr);

            if (newData.exists() && newCode.exists()) {
                System.out.println("File already exists");
            } else {
                if (newData.createNewFile() == true && newCode.createNewFile() == true) {
                    System.out.println("File created: " + newData.getName());
                    System.out.println("File created: " + newCode.getName());
                }
            }

            // Write the size into the two files
            writer1 = new PrintWriter(
                    new FileWriter(dataFile));
            writer2 = new PrintWriter(
                    new FileWriter(codeFile));

            // Read the instruction numbers by lines
            String line;
            String copyLine;
            int o = 0;
            while ((line = br.readLine() )!= null) {
                LabelOffset codeLabel = new LabelOffset();

                // Whether the line contains colon, indicating the start code segment
                for (int i = 0; i < target2.length; i++) {
                    if (line.contains(target2[i])) {
                        insCount++;
                    }
                }
                // When the line contains the branch instruction
                if (line.contains(target2[1]) == false && line.contains("B ") == true) {
                    insCount++;
                }

                // Lines contain the label
                if (line.trim().contains(":") == true && line.trim().startsWith("main:") == false){
                    copyLine = line.trim();
                    copyLine = copyLine.replace(":", "");
                    codeLabel.label = copyLine;
                    // Set the offset of the label object
                    o = insCount * 4;
                    codeLabel.offset = o;
                }
                // Add the LabelOffset object to the list
                if (codeLabel.offset != 0) {
                    labels.add(codeLabel);
                }

                // Data segment starts with a dot,removing the space
                // Boolean to determine if the current line contains a comma
                boolean commaFound = false;
                if (line.trim().startsWith(".word") == true){
                    // Process the line by character to count the word count in the data segment
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == target1) {
                            wordCount++;
                            commaFound = true;
                        }
                    }
                    // If a line contains commas, the word count is equal to the comma numbers plus 1
                    if (commaFound == true) {
                        wordCount++;
                    } else {
                        // When there's no comma in the line, but there's one value following ".word"
                        String [] line2 = line.split(" ");
                        if (line2.length > 1) {
                            wordCount++;
                        }
                    }
                }
            }
//            for (int i = 0; i < labels.size(); i++) {
//                System.out.println("Label:" + labels.get(i).label + " offset:" + labels.get(i).offset);
//            }
            // Calculate the byte size of the two segments
            byteSize1 = wordCount * 4;
            byteSize2 = (insCount + 1) * 4;
            writer1.print(byteSize1);
            writer2.print(byteSize2);


        }
        catch (Throwable t) {
            throw new FileNotFoundException("File not found");
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (writer1 != null && writer2 != null) {
                    writer1.close();
                    writer2.close();
                }
            }
            catch (IOException e) {
                throw new IOException("Error closing the file: " + inFile, e);
            }
        }

        // placeholder return. Student should replace with correct return.
        return labels;

    }

    /**
     * Second pass of the assembler. Writes the binary data and code files.
     *
     * @param inFile   The pathname of the file containing assembly language code.
     * @param dataFile The pathname for the data segment binary file.
     * @param codeFile The pathname for the code segment binary file.
     * @param labels   List of the code segment labels and relative offsets.
     * @throws RuntimeException if there is an error when processing the assembly
     *                          code file.
     */
    public static void pass2(String inFile, String dataFile, String codeFile,
                             ArrayList<LabelOffset> labels) throws FileNotFoundException, IOException {
        // PROGRAMMING ASSIGNMENT 2: COMPLETE THIS METHOD
        int limit = 32;
        int insCount = 0;
        // Binary conversion for the instructions
        boolean [] binLine = new boolean[limit];
        boolean [] binIns = new boolean[limit];
        boolean [] destReg = new boolean[5];
        boolean [] dataReg = new boolean[5];
        boolean [] baseReg = new boolean[5];
        boolean [] immediate = new boolean[9];
        boolean [] immediate2 = new boolean[19];
        boolean [] immediate3 = new boolean[26];
        boolean [] sourceReg1 = new boolean[5];
        boolean [] sourceReg2 = new boolean[5];
        boolean [] addOpcode = {false, false, false, true, true, false, true, false, false, false, true};
        boolean [] subOpcode = {false, false, false, true, true, false, true, false, false, true, true};
        boolean [] andOpcode = {false, false, false, false, true, false, true, false, false, false, true};
        boolean [] orrOpcode = {false, false, false, false, true, false, true, false, true, false, true};
        boolean [] ldrOpcode = {false, true, false, false, false, false, true, true, true, true, true};
        boolean [] strOpcode = {false, false, false, false, false, false, true, true, true, true, true};
        boolean [] bOpcode = {true, false, true, false, false, false};
        boolean [] cbzOpcode = {false, false, true, false, true, true, false, true};
        boolean[] end = {false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, true, false,
                false, false, true, false, true, false, true, true};

        // List to store the integer values
        ArrayList<Long> values = new ArrayList<>();
        String[] insturctions = {"ADD", "SUB", "AND", "ORR", "LDR", "STR", "CBZ"};
        // Create two new files to write the data and code segment
        File newData = new File(dataFile);
        File newCode = new File(codeFile);

        BufferedReader br = null;
        PrintWriter writer1 = null;
        PrintWriter writer2 = null;
        // Open and read the file
        try {
            FileReader fr = new FileReader(inFile);
            br = new BufferedReader(fr);

            if (newData.exists() && newCode.exists()) {
                System.out.println("File already exists");
            } else {
                if (newData.createNewFile() == true && newCode.createNewFile() == true) {
                    System.out.println("File created: " + newData.getName());
                    System.out.println("File created: " + newCode.getName());
                }
            }

            // Write the size into the two files
            writer1 = new PrintWriter(
                    new FileWriter(dataFile, true));
            writer2 = new PrintWriter(
                    new FileWriter(codeFile, true));

            // Read the instruction numbers by lines
            String line;
            String copyLine;
            int currentPosition = 0;
            writer2.println();
            // Read the file by lines
            while ((line = br.readLine()) != null) {
                // Track the instructions number
                for (int i = 0; i < insturctions.length; i++) {
                    if (line.contains(insturctions[i])) {
                        insCount++;
                    }
                }
                // When the line contains the branch instruction
                if (line.contains(insturctions[1]) == false && line.contains("B ") == true) {
                    insCount++;
                }

                if (line.trim().startsWith(".word") == true) {
                    // Process the line by character to count the word count in the data segment
                    copyLine = line.replace(".word", "");
                    copyLine = copyLine.trim();
//                    System.out.println("The new line is :" + copyLine);
                    if (copyLine.contains(",") == true) {
                        String[] line_split = copyLine.split(",");
                        // Add the values to a list for binary conversion later
                        for (int i = 0; i < line_split.length; i++) {
                            values.add(Long.parseLong(line_split[i]));

                        }
                    } else {
                        // When there's no comma in the line, but there's one value following ".word"
                        String copyLine2 = line.trim();
                        String[] line2 = copyLine2.split(" ");

                        if (line2.length > 1) {
                            values.add(Long.parseLong(line2[line2.length - 1]));
                        }
                    }
                }

                // When the ADD, SUB, ORR, AND instructions are found in the file
                if (line.trim().startsWith(insturctions[0]) == true ||
                        line.trim().startsWith(insturctions[1]) == true ||
                        line.trim().startsWith(insturctions[2]) == true ||
                        line.trim().startsWith(insturctions[3]) == true) {
                    copyLine = line.replace(",", " ");
                    copyLine = copyLine.trim();
                    String[] line_split = copyLine.split(" ");
                    // Get the destination register
                    line_split[1] = line_split[1].replaceAll("R", "");
                    // Get the source register 1
                    line_split[2] = line_split[2].replaceAll("R", "");
                    // Get the source register 2
                    line_split[3] = line_split[3].replaceAll("R", "");

                    // Binary conversion
                    destReg = Binary.uDecToBin(Long.parseLong(line_split[1]), 5);
                    sourceReg1 = Binary.uDecToBin(Long.parseLong(line_split[2]), 5);
                    sourceReg2 = Binary.uDecToBin(Long.parseLong(line_split[3]), 5);

                    for (int i = 0; i < binIns.length; i++) {
                        // Initialization
                        binLine[i] = false;
                        // Destination register in the 0-4 bit
                        // Source register 1 in the 5-9 bit
                        // Source register 2 in the 16-20 bit
                        for (int j = 0; j < 5; j++) {
                            binLine[j] = destReg[j];
                            binLine[j + 5] = sourceReg1[j];
                            binLine[j + 16] = sourceReg2[j];
                        }

                        // Opcode in the 21-31 bit
                        for (int k = 0; k < 11; k++) {
                            if (line_split[0].equals("ADD")) {
                                binLine[k + 21] = addOpcode[k];
                            } else if (line_split[0].equals("SUB")) {
                                binLine[k + 21] = subOpcode[k];
                            } else if (line_split[0].equals("AND")) {
                                binLine[k + 21] = andOpcode[k];
                            } else if (line_split[0].equals("ORR")) {
                                binLine[k + 21] = orrOpcode[k];
                            }
                        }
                        // Write on the code file
                        writer2.print(binLine[i] + " ");
                        // Write eight boolean per line
                        if ((i + 1) % 8 == 0) {
                            writer2.print("\n");
                        }
                    }
                }

                // When the LDR, STR instructions are found in the file
                if (line.trim().startsWith(insturctions[4]) == true ||
                        line.trim().startsWith(insturctions[5]) == true) {
                    copyLine = line.replace(",", " ");
                    copyLine = copyLine.replace("[", "");
                    copyLine = copyLine.replace("]", "");
                    copyLine = copyLine.trim();
                    String[] line_split = copyLine.split(" ");
                    // Get the data register
                    line_split[1] = line_split[1].replaceAll("R", "");
                    // Get the base register
                    line_split[2] = line_split[2].replaceAll("R", "");
                    // Get the immediate
                    line_split[3] = line_split[3].replaceAll("#", "");

//                    System.out.println("This is the ins:" + line_split[0]);
//                    System.out.println("This is dest register:" + line_split[1]);
//                    System.out.println("This is source register 1:" + line_split[2]);
//                    System.out.println("This is source register 2:" + line_split[3]);

                    // Binary conversion
                    dataReg = Binary.uDecToBin(Long.parseLong(line_split[1]), 5);
                    baseReg = Binary.uDecToBin(Long.parseLong(line_split[2]), 5);
                    immediate = Binary.sDecToBin(Long.parseLong(line_split[3]), 9);

                    for (int i = 0; i < binIns.length; i++) {
                        // Initialization
                        binLine[i] = false;
                        // Data register in the 0-4 bit
                        // Base register 1 in the 5-9 bit
                        // Immediate in the 12-20 bit
                        for (int j = 0; j < 5; j++) {
                            binLine[j] = dataReg[j];
                            binLine[j + 5] = baseReg[j];
                            binLine[j + 12] = immediate[j];
                        }
                        // Opcode in the 21-31 bit
                        for (int k = 0; k < 11; k++) {
                            if (line_split[0].equals("LDR")) {
                                binLine[k + 21] = ldrOpcode[k];
                            } else if (line_split[0].equals("STR")) {
                                binLine[k + 21] = strOpcode[k];
                            }
                        }
                        // Write on the code file
                        writer2.print(binLine[i] + " ");
                        // Write eight boolean per line
                        if ((i + 1) % 8 == 0) {
                            writer2.print("\n");
                        }
                    }
                }

                // When the CBZ instruction is found in the file
                if (line.trim().startsWith(insturctions[6])) {
                    copyLine = line.replace(",", " ");
                    copyLine = copyLine.trim();
                    String[] line_split = copyLine.split(" ");
                    currentPosition = (insCount - 1) * 4;
//                    System.out.println("Current position of the CBZ ins is:" + currentPosition);
//                    System.out.println("The label is:" + line_split[2]);
                    // Get the destination register
                    line_split[1] = line_split[1].replaceAll("R", "");
                    long distance = 0;
                    // Calculate the difference to get the immediate
                    for (int i = 0; i < labels.size(); i++) {
                        // Find the label from the list and compute the distance by getting the offset of the label
                        if (line_split[2].equals(labels.get(i).label)) {
                            distance = labels.get(i).offset - currentPosition;
//                            System.out.println("The distance between " + labels.get(i).offset + " and " +
//                            currentPosition + " is " + distance);
                        }
                    }
                    // Binary conversion
                    destReg = Binary.uDecToBin(Long.parseLong(line_split[1]), 5);
                    immediate2 = Binary.sDecToBin(distance, 19);

                    for (int i = 0; i < binIns.length; i++) {
                        // Initialization
                        binLine[i] = false;
                        // Destination register in the 0-4 bit
                        // Immediate in the 5-23 bit
                        // Opcode in the 24-31 bit
                        for (int j = 0; j < 5; j++) {
                            binLine[j] = destReg[j];
                        }
                        // Immediate in the 5-23 bit
                        for (int w = 0; w < 19; w++) {
                            binLine[w + 5] = immediate2[w];
                        }
                        // Opcode in the 24-31 bit
                        for (int k = 0; k < 8; k++) {
                            if (line_split[0].equals("CBZ")) {
                                binLine[k + 24] = cbzOpcode[k];
                            }
                        }
                        // Write on the code file
                        writer2.print(binLine[i] + " ");
                        // Write eight boolean per line
                        if ((i + 1) % 8 == 0) {
                            writer2.print("\n");
                        }
                    }

                }
                // When the B instruction is found
                if (line.trim().startsWith("B ") == true) {
                    copyLine = line.trim();
                    String[] line_split = copyLine.split(" ");
                    currentPosition = (insCount - 1) * 4;
//                    System.out.println("Current position of the B ins is:" + currentPosition);
//                    System.out.println("The label is:" + line_split[1]);

                    long distance = 0;
                    // Calculate the difference to get the immediate
                    for (int i = 0; i < labels.size(); i++) {
                        // Find the label from the list and compute the distance by getting the offset of the label
                        if (line_split[1].equals(labels.get(i).label)) {
                            distance = labels.get(i).offset - currentPosition;
//                            System.out.println("The distance between " + labels.get(i).offset + " and " +
//                                    currentPosition + " is " + distance);
                        }
                    }
                    // Binary conversion
                    immediate3 = Binary.sDecToBin(distance, 26);
                    for (int i = 0; i < binIns.length; i++) {
                        // Initialization
                        binLine[i] = false;
                        // Immediate in the 0-25 bit
                        // Opcode in the 26-31 bit
                        for (int j = 0; j < 25; j++) {
                            binLine[j] = immediate3[j];
                        }
                        // Opcode in the 26-31 bit
                        for (int k = 0; k < 6; k++) {
                            if (line_split[0].equals("B")) {
                                binLine[k + 26] = bOpcode[k];
                            }
                        }
                        // Write on the code file
                        writer2.print(binLine[i] + " ");
                        // Write eight boolean per line
                        if ((i + 1) % 8 == 0) {
                            writer2.print("\n");
                        }

                }
                }
            }

            // Write on the data file
            writer1.println();
            for (int i = 0; i < values.size(); i++) {
                binLine = Binary.sDecToBin(values.get(i), 32);
                for (int j = 0; j < binLine.length; j++) {
                    writer1.print(binLine[j] + " ");
                    // Write eight boolean per line
                    if ((j + 1) % 8 == 0) {
                        writer1.print("\n");
                    }
                }
            }
            // Write on the code file
            for (int i = 0; i < end.length; i++) {
                writer2.print(end[i] + " ");
                // Write eight boolean per line
                if ((i + 1) % 8 == 0) {
                    writer2.print("\n");
                }
        }
    }
        catch (Throwable t) {
            throw new FileNotFoundException("File not found");
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (writer1 != null && writer2 != null) {
                    writer1.close();
                    writer2.close();
                }
            }
            catch (IOException e) {
                throw new IOException("Error closing the file: " + inFile, e);
            }
        }

    }
}
