
package net.whydah.sso.util;

import java.util.LinkedList;
import java.util.List;

public final class StringUtil {

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().equals("");
    }


    public static String join(String separator, String[] stringarray) {
        if (stringarray == null)
            return null;
        else
            return join(separator, stringarray, 0, stringarray.length);
    }


    public static String join(String separator, String[] stringarray, int startindex, int count) {
        String result = "";

        if (stringarray == null)
            return null;

        for (int index = startindex; index < stringarray.length && index - startindex < count; index++) {
            if (separator != null && index > startindex)
                result += separator;

            if (stringarray[index] != null)
                result += stringarray[index];
        }

        return result;
    }


    public static String trimEnd(String string, Character... charsToTrim) {
        if (string == null || charsToTrim == null)
            return string;

        int lengthToKeep = string.length();
        for (int index = string.length() - 1; index >= 0; index--) {
            boolean removeChar = false;
            if (charsToTrim.length == 0) {
                if (Character.isWhitespace(string.charAt(index))) {
                    lengthToKeep = index;
                    removeChar = true;
                }
            } else {
                for (int trimCharIndex = 0; trimCharIndex < charsToTrim.length; trimCharIndex++) {
                    if (string.charAt(index) == charsToTrim[trimCharIndex]) {
                        lengthToKeep = index;
                        removeChar = true;
                        break;
                    }
                }
            }
            if (!removeChar)
                break;
        }
        return string.substring(0, lengthToKeep);
    }


    public static String trimStart(String string, Character... charsToTrim) {
        if (string == null || charsToTrim == null)
            return string;

        int startingIndex = 0;
        for (int index = 0; index < string.length(); index++) {
            boolean removeChar = false;
            if (charsToTrim.length == 0) {
                if (Character.isWhitespace(string.charAt(index))) {
                    startingIndex = index + 1;
                    removeChar = true;
                }
            } else {
                for (int trimCharIndex = 0; trimCharIndex < charsToTrim.length; trimCharIndex++) {
                    if (string.charAt(index) == charsToTrim[trimCharIndex]) {
                        startingIndex = index + 1;
                        removeChar = true;
                        break;
                    }
                }
            }
            if (!removeChar)
                break;
        }
        return string.substring(startingIndex);
    }


    public static String trim(String string, Character... charsToTrim) {
        return trimEnd(trimStart(string, charsToTrim), charsToTrim);
    }

    public static boolean stringsEqual(String s1, String s2) {
        if (s1 == null && s2 == null)
            return true;
        else
            return s1 != null && s1.equals(s2);
    }

    public static String wordWrapText(String in, int length) {

        while (in.length() > 0 && (in.charAt(0) == '\t' || in.charAt(0) == ' '))
            in = in.substring(1);
        if (in.length() < length) {
            if (in.contains(newline)) {
                return in.substring(0, in.indexOf(newline)).trim() + newline +
                        wordWrap(in.substring(in.indexOf("\n") + 1), in.length());
            } else return in;
        }

        //:: If Next length Contains Newline, Split There
        if (in.substring(0, length).contains(newline))
            return in.substring(0, in.indexOf(newline)).trim() + newline +
                    wordWrapText(in.substring(in.indexOf("\n") + 1), length);

        //:: Otherwise, Split Along Nearest Previous Space/Tab/Dash
        int spaceIndex = Math.max(Math.max(in.lastIndexOf(" ", length),
                in.lastIndexOf("\t", length)),
                in.lastIndexOf("-", length));

        //:: If No Nearest Space, Split At length
        if (spaceIndex == -1)
            spaceIndex = length;

        //:: Split
        return in.substring(0, spaceIndex).trim() + newline + wordWrapText(in.substring(spaceIndex), length);
    }

    public static List<String> wordWrap(String in, int length) {
        List<String> list = new LinkedList<String>();
        wordWrap(in, length, list);
        return list;
    }

    static void wordWrap(String in, int length, List<String> list) {

        try {


            while (in.length() > 0 && (in.charAt(0) == '\t' || in.charAt(0) == ' ')) {

                in = in.substring(1);

            }

            if (in.length() < length) {

                if (in.contains(newline)) {
                    list.add(in.substring(0, in.indexOf(newline)).trim());
                    wordWrap(in.substring(in.indexOf("\n") + 1), in.length(), list);
                } else {

                    list.add(in);
                }
                return;

            }

            if (in.substring(0, length).contains(newline)) {
                list.add(in.substring(0, in.indexOf(newline)).trim());
                wordWrap(in.substring(in.indexOf("\n") + 1), length, list);
                return;
            }

            //:: Otherwise, Split Along Nearest Previous Space/Tab/Dash
            int spaceIndex = Math.max(in.lastIndexOf(" ", length),
                    in.lastIndexOf("\t", length));

            //:: If No Nearest Space, Split At length
            if (spaceIndex == -1 || spaceIndex == 0) {
                spaceIndex = length;
            }

            //:: Split
            list.add(in.substring(0, spaceIndex).trim());
            wordWrap(in.substring(spaceIndex), length, list);

        } catch (Exception ex) {


        }
        return;

    }

    private static final String newline = System.getProperty("line.separator");

}