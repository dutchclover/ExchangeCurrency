package com.dgroup.parser;

import java.util.HashMap;
import java.util.Map;

public class Translit {

    private static final Map<String, String> letters = new HashMap<String, String>();
    static {
        letters.put("А", "A");
        letters.put("Б", "B");
        letters.put("В", "V");
        letters.put("Г", "G");
        letters.put("Д", "D");
        letters.put("Е", "E");
        letters.put("Ё", "E");
        letters.put("Ж", "ZH");
        letters.put("З", "Z");
        letters.put("И", "I");
        letters.put("Й", "Y");
        letters.put("К", "K");
        letters.put("Л", "L");
        letters.put("М", "M");
        letters.put("Н", "N");
        letters.put("О", "O");
        letters.put("П", "P");
        letters.put("Р", "R");
        letters.put("С", "S");
        letters.put("Т", "T");
        letters.put("У", "U");
        letters.put("Ф", "F");
        letters.put("Х", "H");
        letters.put("Ц", "TS");
        letters.put("Ч", "CH");
        letters.put("Ш", "SH");
        letters.put("Щ", "SCH");
        letters.put("Ъ", "'");
        letters.put("Ы", "YI");
        letters.put("Ъ", "'");
        letters.put("Э", "JE");
        letters.put("Ю", "YU");
        letters.put("Я", "YA");
        letters.put("а", "a");
        letters.put("б", "b");
        letters.put("в", "v");
        letters.put("г", "g");
        letters.put("д", "d");
        letters.put("е", "e");
        letters.put("ё", "e");
        letters.put("ж", "zh");
        letters.put("з", "z");
        letters.put("и", "i");
        letters.put("й", "y");
        letters.put("к", "k");
        letters.put("л", "l");
        letters.put("м", "m");
        letters.put("н", "n");
        letters.put("о", "o");
        letters.put("п", "p");
        letters.put("р", "r");
        letters.put("с", "s");
        letters.put("т", "t");
        letters.put("у", "u");
        letters.put("ф", "f");
        letters.put("х", "h");
        letters.put("ц", "ts");
        letters.put("ч", "ch");
        letters.put("ш", "sh");
        letters.put("щ", "sch");
        letters.put("ь", "~");
        letters.put("ы", "yi");
        letters.put("ъ", "~");
        letters.put("э", "je");
        letters.put("ю", "yu");
        letters.put("я", "ya");
        letters.put(" ", "_");
    }



    public static String toTranslit(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i<text.length(); i++) {
            String l = text.substring(i, i+1);
            if (letters.containsKey(l)) {
                sb.append(letters.get(l));
                if(i>0 && (l.equals("ц") || l.equals("Ц"))){
                	sb.append("s");
                }
                if(i>0 && (l.equals("ш") || l.equals("Ш") || l.equals("щ") || l.equals("Щ"))){
                    sb.insert(sb.length()-2,"s");
                }
            }else {
                sb.append(l);
            }
        }
        return sb.toString();
    }

}