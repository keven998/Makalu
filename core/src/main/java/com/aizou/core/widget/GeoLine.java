package com.aizou.core.widget;

import android.graphics.PointF;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public 	class GeoLine{	//Ax+By+C = 0
	public static double 	ZERO = 1E-5;	//很小的正数，代替0进行计算
	public static double 	RadianToDegree = 180/ Math.PI;
	
	public double A;
	public double B;
	public double C;
	
	public GeoLine(double a,double b,double c){
		A = a;
		B = b;
		C = c;
	}
	public String toString(){
		return "line Ax+By+C=0:[A,B,c]=[" +A+","+B+","+C+"]";
	}
	public GeoLine(){	//x=0;
		A = 1;
		B = 0;
		C = 0;
	}
	/**
	 * 判断两条直线是否是同一条
	 */
	public static boolean isSameLine(GeoLine line1,GeoLine line2){
		boolean isSame = false;
		if( Math.abs(line1.A * line2.B - line1.B * line2.A)<ZERO){	//平行
			if( Math.abs(line1.A * line2.C - line1.C * line2.A)<ZERO)
				isSame = true;
		}
		return isSame;
	}
	
	/**
	 * 判断一个点(x,y)是否在一个矩形框的范围内(包括边上)
	 * @param point
	 * @param rect
	 * @return 在内部true，否则false
	 */
	public static boolean isPointInRect(PointF point,Rect rect){
		if(rect.left<=point.x && point.x<=rect.right && rect.top<=point.y && point.y<= rect.bottom )
			return true;
		else 
			return false;
	}
	/**
	 * 判断某一点是否在直线上
	 */
	public static boolean isPointInLine(PointF point,GeoLine line1){
		double value = Math.abs(line1.A * point.x + line1.B * point.y + line1.C);
		if(value<ZERO){
			return true;
		}
		return false;		
	}
	
	/**
	 * 2条直线的交点,平行或2条直线相同，将返回null
	 */
	public static PointF intersectionOf2Line(GeoLine line1,GeoLine line2){
		PointF point 	= null;
		double value1 	= line1.A * line2.B - line1.B * line2.A;
		if(Math.abs(value1)<ZERO){		//平行线
			return point;
		}else{
			point = new PointF();
			point.x = (float) ((line1.B*line2.C - line1.C*line2.B )/value1);
			point.y = (float) ((line1.C*line2.A - line1.A*line2.C)/value1);
		}
		return point;
	}
	/**
	 * 计算一条直线，
	 * 与圆(圆心point，半径length)的交点
	 * 返回的list中,如果包含2个交点，则先放x大
	 * 即list.get(0).x >= list.get(1).x
	 * 如果2点x一样大，则y值大的先放
	 */
	public static List<PointF> intersectionWithCircle(PointF point,GeoLine line1,double length){
		if(point ==null || length < ZERO){
			return null;
		}
		
		PointF pA = point;					// 过改点与line1的垂线，与line1的交点
		double len = length;
		if( !isPointInLine(point,line1)){	//该点不在直线上
			GeoLine line2 = verticalLineAtPoint(point,line1);
			pA = intersectionOf2Line(line1,line2);
			double distance = distanceOf2PointF(point,pA);
			if(distance > length){
				return null;
			}
			double tmp = length*length - distance*distance;
			len = Math.sqrt(tmp);
		}
		//现在计算在一条直线上line1的点pA，距离=len的点
		List<PointF> points = new ArrayList<PointF>();
		
		//只有一个交点的情况
		if(len<ZERO){
			points.add(pA);
			return points;
		}
		
		//2个交点的情况
		if(Math.abs(line1.A)<ZERO){				//By+C = 0
			PointF p1 = new PointF();
			p1.x = (float) (pA.x+len);
			p1.y = (float) (-line1.C/line1.B);
			
			PointF p2 = new PointF();
			p2.x = (float) (pA.x-len);
			p2.y = (float) (-line1.C/line1.B);
			
			points.add(p1);
			points.add(p2);
			return points;
		}
		if(Math.abs(line1.B)<ZERO){		//Ax+C = 0
			PointF p1 = new PointF();
			p1.x = (float) (-line1.C/line1.A);
			p1.y = (float) (pA.y+len);
			
			PointF p2 = new PointF();
			p2.x = (float) (-line1.C/line1.A);
			p2.y = (float) (pA.y-len);
			
			points.add(p1);
			points.add(p2);
			return points;
		}
		//Ax+By+C=0
		double k = -line1.A/line1.B;
		double deltaX = Math.sqrt(len * len / (1 + k * k));
		double deltaY = k*deltaX;
		
		PointF p1 = new PointF();
		p1.x = (float) (pA.x+deltaX);
		p1.y = (float) (pA.y+deltaY);
		PointF p2 = new PointF();
		p2.x = (float) (pA.x-deltaX);
		p2.y = (float) (pA.y-deltaY);	
		
		points.add(p1);
		points.add(p2);
		return points;
	}
	
	/**
	 * 计算2个点之间的距离
	 */
	public static double distanceOf2PointF(PointF pA,PointF pB){
		double value = (pA.x-pB.x)*(pA.x-pB.x) +(pA.y-pB.y)*(pA.y-pB.y);
		return Math.sqrt(value);
	}
	
	/**
	 * 计算pA对center的角度
	 * 原点为center,逆时针为正 -180-180
	 */
	public static float fieldAngleToPoint(PointF center,PointF pA){
		double deltaX = pA.x-center.x;
		double deltaY = pA.y-center.y;
		
		if( Math.abs(deltaY)<ZERO){	//在X轴上
			if(deltaX >ZERO)
				return 0f;
			return 180f;
		}
		
		if( Math.abs(deltaX)<ZERO){	//在Y轴上
			if(deltaY >ZERO)
				return 90f;
			return -90f;
		}
		
		double len = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		double radius = Math.acos(deltaX / len);
		double angle = radius * RadianToDegree;
		
		//1,2象限不用处理，34象限要处理
		if(deltaY<ZERO){
			angle = -angle;
		}
		return (float) angle;
	}
	
	/**
	 * 过某一点，垂直于另外一条直线的线
	 */
	public static GeoLine verticalLineAtPoint(PointF point,GeoLine line1){
		if(point == null || line1==null)
			return null;
		
		GeoLine line2 = new GeoLine();
		line2.A	= line1.B;
		line2.B = -line1.A;
		line2.C = -line2.A*point.x - line2.B*point.y;
		return line2;
	}
	/**
	 * 求经过两点的直线
	 */
	public static GeoLine linePass2Points(PointF p1,PointF p2){
		if(null ==p1 || null==p2)
			return null;
		
		GeoLine line	= new GeoLine();
		line.A			= p1.y - p2.y;
		line.B			= p2.x - p1.x;
		if(Math.abs(line.A)<ZERO && Math.abs(line.B)<ZERO )
			return null;	//同一点
		
		line.C			= -p1.x*line.A - p1.y*line.B;
		return line;
	}
	/**
	 * 经过一点p1，斜率为k的直线方程
	 */
	public static GeoLine linePointSlope(PointF p1, double k) {
		GeoLine line	= new GeoLine();
		line.A 			= -k;
		line.B 			= 1;
		line.C 			= k * p1.x - p1.y;
		return line;
	}
	/**
	 * 经过一点，平行于一条直线的直线
	 */
	public static GeoLine lineParaPoint(PointF p1,GeoLine line1){
		GeoLine line2 	= new GeoLine();
		line2.A			= line1.A;
		line2.B			= line1.B;
		line2.C			= -line1.A*p1.x - line1.B*p1.y;
		return line2;
	}
	/**
	 * 平行于一条直线line1,且距离=len的直线line2(待求)
	 * len>0表示line2在line1上方
	 * len<0表示在下方
	 */
	public static GeoLine lineParaDistance(double len,GeoLine line1){
		GeoLine line2	= new GeoLine();
		line2.A			= line1.A;
		line2.B			= line1.B;
		double dc		= len * Math.sqrt(line1.A * line1.A + line1.B * line1.B);
		if(line1.B>0){
			line2.C			= line1.C - dc;
		}else{
			line2.C			= line1.C + dc;
		}
		return line2;
	}
}
