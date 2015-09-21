package com.xuejian.client.lxp.common.utils;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/9/18.
 */
public class LocationUtils {
    private ArrayList<Point> list;
    private ArrayList<Double> Xlist;
    private ArrayList<Double> Ylist;

    public LocationUtils() {
        list = new ArrayList<>();
        Xlist = new ArrayList<>();
        Ylist = new ArrayList<>();
        initData();
    }

    private void initData() {
        list.add(new Point(49.207884, 87.570003));
        list.add(new Point(39.433194, 73.463557));
        list.add(new Point(30.077871, 81.080163));
        list.add(new Point(28.545185, 97.559655));
        list.add(new Point(23.291036, 105.250085));
        list.add(new Point(21.939444, 101.764337));
        list.add(new Point(21.612970, 107.960625));
        list.add(new Point(3.465682, 111.739922));
        list.add(new Point(24.336318, 122.696374));
        list.add(new Point(39.917516, 124.278405));
        list.add(new Point(42.625255, 130.606530));
        list.add(new Point(48.467628, 135.042902));
        list.add(new Point(53.538000, 122.726250));
        list.add(new Point(41.702830, 104.796564));
    }

    public boolean containsPoint(Point point) {
        int verticesCount = list.size();
        int nCross = 0;
        if (point.getLat() < 3||point.getLat()>54) return false;
        if (point.getLng() > 135 || point.getLng() < 73) return false;
        for (int i = 0; i < verticesCount; ++i) {
            Point p1 = list.get(i);
            Point p2 = list.get((i + 1) % verticesCount);

            // 求解 y=p.y 与 p1 p2 的交点
            if (p1.getLat() == p2.getLat()) {   // p1p2 与 y=p0.y平行
                continue;
            }
            if (point.getLat() < Math.min(p1.getLat(), p2.getLat())) { // 交点在p1p2延长线上
                continue;
            }
            if (point.getLat() >= Math.max(p1.getLat(), p2.getLat())) { // 交点在p1p2延长线上
                continue;
            }
            // 求交点的 X 坐标
            double x = (point.getLat() - p1.getLat()) * (p2.getLng() - p1.getLng())
                    / (p2.getLat() - p1.getLat()) + p1.getLng();
            if (x > point.getLat()) { // 只统计单边交点
                nCross++;
            }
        }
        // 单边交点为偶数，点在多边形之外
        return (nCross % 2 == 1);
    }

    //    int    polySides  =  how many cornersthe polygon has
//     float  polyX[]    =  horizontalcoordinates of corners
//  float  polyY[]    =  verticalcoordinates of corners
//  float  x,y       =  point to be tested;
    public boolean pointInPolygon(Point _point) {
        if (_point.getLat() < 3||_point.getLat()>54) return false;
        if (_point.getLng() > 135 || _point.getLng() < 73) return false;
        Xlist.clear();
        Ylist.clear();
        for (Point point : list) {
            Xlist.add(point.lat);
            Ylist.add(point.lng);
        }
        double x = _point.lat;
        double y = _point.lng;
        Double polyX[] = Xlist.toArray(new Double[]{});
        Double polyY[] = Ylist.toArray(new Double[]{});
        int polySides = list.size();
        int i, j = polySides - 1;
        boolean oddNodes = false;
        for (i = 0; i < polySides; i++) {
            if ((polyY[i] < y && polyY[j] >= y
                    || polyY[j] < y && polyY[i] >= y)
                    && (polyX[i] <= x || polyX[j] <= x)) {
                oddNodes ^= (polyX[i] + (y - polyY[i]) / (polyY[j] - polyY[i]) * (polyX[j] - polyX[i]) < x);
            }
            j = i;
        }
        return !oddNodes;
    }

    public static class Point {
        double lat;
        double lng;

        public Point(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}
