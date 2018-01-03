package com.example.derek.test;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

/**
 * Created by Derek on 29/12/2017.
 * from article https://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/
 */

/*
Year,Make,Model,Description,Price
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture ""Extended Edition""","",4900.00
1999,Chevy,"Venture ""Extended Edition, Very Large""",,5000.00
1996,Jeep,Grand Cherokee,"MUST SELL!
air, moon roof, loaded",4799.00
 */
public class CSVParse {
    public static void writeLine(Writer w, List<String> values) throws Exception {
        boolean firstVal = true;
        for (String val : values)  {
            if (!firstVal) {
                w.write(",");
            }
            w.write("\"");
            for (int i=0; i<val.length(); i++) {
                char ch = val.charAt(i);
                if (ch=='\"') {
                    w.write("\"");  //extra quote
                }
                w.write(ch);
            }
            w.write("\"");
            firstVal = false;
        }
        w.write("\n");
    }

    /**
     * Returns a null when the input stream is empty
     */
    public static List<String> readLine(Reader r) throws Exception {
        int ch = r.read();
        while (ch == '\r') {
            ch = r.read();
        }

        if (ch < 0) { //UTF-8 ???????????????????????
            return null;
        }
        Vector<String> store = new Vector<String>();
        StringBuffer curVal = new StringBuffer();
        boolean bInQuotes = false;
        boolean bStarted = false;
        while (ch >= 0) {
            if (bInQuotes) {
                bStarted = true;
                if (ch == '\"') {
                    bInQuotes = false;
                }
                else {
                    curVal.append((char)ch);
                }
            }
            else {
                if (ch == '\"') {
                    bInQuotes = true;
                    if (bStarted) {
                        // if this is the second quote in a value, add a quote
                        // this is for the double quote in the middle of a value
                        curVal.append('\"');
                    }
                }
                else if (ch == ',') {
                    store.add(curVal.toString());
                    curVal = new StringBuffer();
                    bStarted = false;
                }
                else if (ch == '\r') {
                    //ignore CR characters
                    continue;
                }
                else if (ch == '\n') {
                    //end of a line, break out
                    break;
                }
                else {
                    curVal.append((char)ch);
                }
            }
            ch = r.read();
        }
        store.add(curVal.toString());
        return store;
    }
}
