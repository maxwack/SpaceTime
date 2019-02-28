package com.mobile.hinde.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constant {


    public static final int SUN_CODE = 1;
    public static final int MOON_CODE = 2;
    public static final int ISS_CODE = 3;
    public static final int INSIGHT_CODE =4;
    public static final int VOYAGER1_CODE = 5;
    public static final int VOYAGER2_CODE = 6;

    public static final String SUN_NAME = "SUN";
    public static final String MOON_NAME = "MOON";
    public static final String ISS_NAME = "ISS";
    public static final String INSIGHT_NAME ="INSIGHT";
    public static final String VOYAGER1_NAME = "VOYAGER1";
    public static final String VOYAGER2_NAME = "VOYAGER2";

    public static final long SUN_MONEY = 50;
    public static final long MOON_MONEY = 10;
    public static final long VOYAGER1_MONEY= 100;


    public static final long VOYAGER1_UNLOCK = 500;

    public static final Map<Integer, String> NAME_FROM_CODE;

    static {
        Map<Integer, String> map = new HashMap();
        map.put(SUN_CODE, SUN_NAME);
        map.put(MOON_CODE, MOON_NAME);
        map.put(ISS_CODE, ISS_NAME);
        map.put(INSIGHT_CODE, INSIGHT_NAME);
        map.put(VOYAGER1_CODE, VOYAGER1_NAME);
        map.put(VOYAGER2_CODE, VOYAGER2_NAME);

        NAME_FROM_CODE = Collections.unmodifiableMap(map);
    }

    public static final Map<String, Integer> CODE_FROM_NAME;

    static {
        Map<String, Integer> map = new HashMap();
        map.put(SUN_NAME, SUN_CODE);
        map.put(MOON_NAME, MOON_CODE);
        map.put(ISS_NAME, ISS_CODE);
        map.put(INSIGHT_NAME, INSIGHT_CODE);
        map.put(VOYAGER1_NAME, VOYAGER1_CODE);
        map.put(VOYAGER2_NAME, VOYAGER2_CODE);

        CODE_FROM_NAME = Collections.unmodifiableMap(map);
    }

    public static final Map<String, Long> MONEY_FROM_NAME;

    static {
        Map<String, Long> map = new HashMap();
        map.put(SUN_NAME, SUN_MONEY);
        map.put(MOON_NAME, MOON_MONEY);
        map.put(VOYAGER1_NAME, VOYAGER1_MONEY);

        MONEY_FROM_NAME = Collections.unmodifiableMap(map);
    }

    public static final Map<String, Long> UNLOCK_FROM_NAME;

    static {
        Map<String, Long> map = new HashMap();
        map.put(VOYAGER1_NAME, VOYAGER1_UNLOCK);

        UNLOCK_FROM_NAME = Collections.unmodifiableMap(map);
    }
}
