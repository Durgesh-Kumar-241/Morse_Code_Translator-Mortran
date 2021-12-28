package com.dktechhub.morsecode;

import java.util.HashMap;
import java.util.Scanner;

public class McodeConverter {

     static final int UNIT =350;
     static final int DOT = UNIT;
     static final int DASH =3*UNIT;
     static final int LETTER_PARTS_GAP=UNIT;


     static final int LETTER_GAP =3*UNIT;
     static final int WORD_GAP = 7*UNIT;


    private static String morse[] = new String[36];
    private static HashMap<String,Character> english = new HashMap<>();


    public McodeConverter()
    {
        morse[0]="_ _ _ _ _";
        morse[1]=". _ _ _ _";
        morse[2]=". . _ _ _";
        morse[3]=". . . _ _";
        morse[4]=". . . . _";
        morse[5]=". . . . .";
        morse[6]="_ . . . .";
        morse[7]="_ _ . . .";
        morse[8]="_ _ _ . .";
        morse[9]="_ _ _ _ .";
        morse['A'-55]=". _";
        morse['B'-55]="_ . . .";
        morse['C'-55]="_ . _ .";
        morse['D'-55]="_ . .";
        morse['E'-55]=".";
        morse['F'-55]=". . _ .";
        morse['G'-55]="_ _ .";
        morse['H'-55]=". . . .";
        morse['I'-55]=". .";
        morse['J'-55]=". _ _ _";
        morse['K'-55]="_ . _";
        morse['L'-55]=". _ . .";
        morse['M'-55]="_ _";
        morse['N'-55]="- .";
        morse['O'-55]="_ _ _";
        morse['P'-55]=". _ _ .";
        morse['Q'-55]="_ _ . _";
        morse['R'-55]=". _ .";
        morse['S'-55]=". . .";
        morse['T'-55]="_";
        morse['U'-55]=". . _";
        morse['V'-55]=". . . _";
        morse['W'-55]=". _ _";
        morse['X'-55]="_ . . _";
        morse['Y'-55]="_ . _ _";
        morse['Z'-55]="_ _ . .";
        for(int i=0;i< morse.length;i++)
        {
            if(i>=10)
            {
                english.put(morse[i], (char) (i+55));
            }else {
                english.put(morse[i], (char) ('0'+i));
            }
        }


    }




    public  String decodeUSA(String encoded)
    {   encoded=encoded.trim();
        StringBuilder stringBuilder = new StringBuilder();
        String[] words = encoded.split("       ");
        for(int i=0;i<words.length;i++)
        {
            String[] chars = words[i].split("   ");
            for (int j=0;j< chars.length;j++) {
                String mchar = chars[j];
                //System.out.println(mchar+" "+english.get(mchar));
                stringBuilder.append(english.get(mchar));

            }

            stringBuilder.append(' ');
        }


        return stringBuilder.toString();
    }

    public  String encodeUSA(String decoded)
    {
        if(decoded.length()==0)
            return decoded;
        decoded = decoded.toUpperCase();


        StringBuilder sb = new StringBuilder();

        String[] words = decoded.split(" ");
        for(String word:words)
        {
            for(int i=0;i< word.length();i++)
            {
                char c = word.charAt(i);
                if(c>='0'&&c<='9')
                {
                    sb.append(morse[c-'0']);
                }else if(c>='A'&&c<='Z')
                {
                    sb.append(morse[c-55]);
                }
                if(i!=word.length()-1)
                sb.append("   ");
            }
            sb.append("       ");
        }

        return sb.toString().trim();
    }
}
