/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/

import java.util.ArrayList;

/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author Kieran Pichai
 */
public class BitmapCompressor {
    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    // Compress algorithm that reads a file in and writes out a compressed version
    public static void compress(int maxBitsCompressing) {
        // Takes in a specified number of bits for codes (user chosen) and calculates the max repeats it can store
        int maxLength = (int)Math.pow(2, maxBitsCompressing) - 1;
        ArrayList<Integer> runLengths = new ArrayList<Integer>();
        boolean previousBit = false;
        boolean currBit;
        int count = 0;
        // While loop reads in data bit by bit
        while (!BinaryStdIn.isEmpty()) {
            currBit = BinaryStdIn.readBoolean();
            // Checks if you have a match with your previous bit
            if (currBit == previousBit) {
                count++;
                // Resets and appends count to arraylist in case you exceed max repeats storable in one code
                if (count > maxLength) {
                    runLengths.add(maxLength);
                    runLengths.add(0);
                    count = 1;
                }
            } else {
                // Lose count so adds current count to arraylist
                runLengths.add(count);
                count = 1;
                previousBit = !previousBit;
            }
        }
        // Writes out number of bits for code, total num of codes, then each code
        BinaryStdOut.write(maxBitsCompressing, 8);
        BinaryStdOut.write(runLengths.size()+1);
        for (int cur : runLengths) {
            BinaryStdOut.write(cur, maxBitsCompressing);
        }
        // Writes out final code
        BinaryStdOut.write(count, maxBitsCompressing);
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    // Expand function that reads in a compressed file and losslessly expands it
    public static void expand() {
        // Reads in both num bits allocated to a code and total num of repeats
        int max = BinaryStdIn.readInt(8);
        int numRepeatCodes = BinaryStdIn.readInt();
        int curByte;
        boolean previousBit = false;
        // Loops through num of repeats and reads in max at a time
        for (int i = 0; i < numRepeatCodes; i++) {
            curByte = BinaryStdIn.readInt(max);
            for (int j = 0; j < curByte; j++) {
                BinaryStdOut.write(previousBit);
            }
            // Flips bit to ensure we have alternating expansion pattern
            previousBit = !previousBit;
        }
        BinaryStdOut.close();
    }

    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            // Reads in code length for codes indicated in the terminal, allows to explore different compression ratios
            compress(Integer.parseInt(args[1]));
        }
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}