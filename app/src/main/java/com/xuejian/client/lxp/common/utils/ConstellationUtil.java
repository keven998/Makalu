package com.xuejian.client.lxp.common.utils;

import com.xuejian.client.lxp.R;

/**
 * Created by yibiao.qin on 2015/7/18.
 */
public class ConstellationUtil {

    private static int res[]=new int[]{R.drawable.capricorn,R.drawable.aquarius,R.drawable.pisces,
            R.drawable.aries,R.drawable.taurus,R.drawable.gemini,R.drawable.cancer,
            R.drawable.leo,R.drawable.virgo,R.drawable.libra,R.drawable.scorpio,
            R.drawable.sagittarius};
    public  enum Constellation {
        Capricorn(0, "capricorn"), Aquarius(1, "aquarius"), Pisces(2, "pisces"), Aries(3,
                "aries"), Taurus(4, "taurus"), Gemini(5, "gemini"), Cancer(6, "cancer"), Leo(
                7, "leo"), Virgo(8, "virgo"), Libra(9, "libra"), Scorpio(10, "scorpio"), Sagittarius(
                11, "sagittarius");

        private Constellation(int code, String chineseName) {
            this.code = code;
            this.chineseName = chineseName;
        }
        private int code;
        private  String chineseName;

        public int getCode() {
            return this.code;
        }
        public String getChineseName() {
            return this.chineseName;
        }
    }

    public static final Constellation[] constellationArr = {
            Constellation.Aquarius, Constellation.Pisces, Constellation.Aries,
            Constellation.Taurus, Constellation.Gemini, Constellation.Cancer,
            Constellation.Leo, Constellation.Virgo, Constellation.Libra,
            Constellation.Scorpio, Constellation.Sagittarius,
            Constellation.Capricorn
    };

    public static final int[] constellationEdgeDay = { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };

    public static int calculateConstellation(String birthday) {
        if (birthday == null || birthday.trim().length() == 0)
            return 0;
        String[] birthdayElements = birthday.split("-");
        if (birthdayElements.length != 3)
            return 0;
        int month = Integer.parseInt(birthdayElements[1]);
        int day = Integer.parseInt(birthdayElements[2]);
        if (month == 0 || day == 0 || month > 12)
            return 0;
        month = day < constellationEdgeDay[month - 1]?month - 1:month;
        return month > 0?res[constellationArr[month - 1].code]: res[constellationArr[11].code];
    }
}