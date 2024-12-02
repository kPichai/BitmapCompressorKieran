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
    public static void compress() {
        boolean previousBit = false;
        int count = 0;
        while (!BinaryStdIn.isEmpty()) {
            boolean currBit = BinaryStdIn.readBoolean();
            if (currBit != previousBit) {
                if (count > 255) {
                    BinaryStdOut.write(255, 8);
                    BinaryStdOut.write(0, 8);
                    BinaryStdOut.write(count - 255, 8);
                } else {
                    BinaryStdOut.write(count, 8);
                }
                count = 1;
            } else {
                count++;
            }
        }
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {
        boolean isCompressed;
        boolean isZero;
        while (!BinaryStdIn.isEmpty()) {
            isCompressed = BinaryStdIn.readBoolean();
            if (isCompressed) {
                isZero = BinaryStdIn.readBoolean();
                char c = BinaryStdIn.readChar(6);
                for (int i = 0; i < c; i++) {
                    BinaryStdOut.write(!isZero);
                }
            } else {
                for (int i = 0; i < 7; i++) {
                    BinaryStdOut.write(BinaryStdIn.readBoolean());
                }
            }
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
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}