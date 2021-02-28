package util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Utility {

    private final static String latinAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    /***
     *
     * @param length of the required String
     * @return  Uppercase String of length [length], made up of 26 characters from latin alphabet
     */
    public static String getRandomString(int length) {
        StringBuilder output = new StringBuilder();

        for (int i=0; i<length; i++) {
            output.append(latinAlphabet.charAt((int) Math.floor(Math.random() * 26)));
        }
        return output.toString();
    }

    public static String getExtensionFromPath(String path) {
        return "getExtensionFromPath not implemented";
    }

    public static String leftPad(String input, String character, int size) {
        //return Arrays.stream(input.split("\n").map(line -> character.repeat(size)+ line).collect(Collectors.joining("\n"))));
        StringBuilder output = new StringBuilder();
        for ( int i=0; i<size; i++ ) {
            output.append(character);
        }
        output.append(input);
        return output.toString();
    }

    public static String leftPad(String input, String character) {
        return leftPad(input, character, 1);
    }

    public static String readUnitl(String input, String separator, int offset) {
        if(offset >= input.length()) {
            return null;
        }
        else {
            return input.substring(offset).split(separator)[0];
        }
    }

}
