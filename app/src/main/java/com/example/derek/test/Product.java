package com.example.derek.test;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Vector;

/**
 * Created by Derek on 02/01/2018.
 */

/*
1997,"Product 1",7.99,15.99
1999,"Product, 2",9.99,18.99
1999,"Product ("embedded quote") 3",8.99,17.99
1996,"Product 4",4.99,9.99
*/




public class Product {
    int         ID;
    String      sDescription;
    float       fCostPrice;
    float       fSellPrice;

    public Product(int id, String sDescription, float fCostPrice, float fSellPrice) {
    }

    @NonNull
    public static Product constructFromStrings(List<String> values) {

        int     _ID              = Integer.parseInt(values.get(0));
        String  _sDescription    = values.get(1);
        float   _fCostPrice      = Float.parseFloat(values.get(2));
        float   _fSellPrice      = Float.parseFloat(values.get(3));
        return new Product(_ID, _sDescription, _fCostPrice, _fSellPrice);
    }

    public List<String> getValues() {
        Vector<String> values = new Vector<String>();
        values.add(Integer.toString(ID));
        values.add(sDescription);
        values.add(Float.toString(fCostPrice));
        values.add(Float.toString(fSellPrice));
        return values;
    }
}
