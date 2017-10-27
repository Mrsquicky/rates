package com.sedisys.util;

public class ComparableUtils {
    public static <T extends Comparable<? super T>> T min(T... values){
        if (values.length==0){
            return null;
        }
        T minValue = values[0];
        for (int i=1;i<values.length;i++){
            if (values[i].compareTo(minValue)<0){
                minValue = values[i];
            }
        }
        return minValue;
    }

    public static <T extends Comparable<? super T>> T max(T... values){
        if (values.length==0){
            return null;
        }
        T maxValue = values[0];
        for (int i=1;i<values.length;i++){
            if (values[i].compareTo(maxValue)>0){
                maxValue = values[i];
            }
        }
        return maxValue;
    }
}