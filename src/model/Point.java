package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class Point {

	static final double EPSILON = 1e-7;
	public double x, y;
	
	public Point(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	public void add(Point p){
		add(p.x, p.y);
	}

	public void add(double x, double y) {
		this.x+=x;
		this.y+=y;
	}
	
	public void applyFunction(Function<Double, Double> f){
		x = f.apply(x);
		y = f.apply(y);
	}
	
	public double abs(){
		return Point.abs(this);
	}
	
	public String toString(){
		return "("+x+", "+y+")";
	}
	
	public Point clone() {
		return new Point(x, y);
	}
	
	static public Point sum(Point p1, Point p2){
		return new Point(p1.x+p2.x, p1.y+p2.y);
	}
	
	static public Point sub(Point p1, Point p2){
		return new Point(p1.x-p2.x, p1.y-p2.y);
	}
	
	static public double abs(Point p){
		return Math.sqrt(abs2(p));
	}
	
	static public double abs2(Point p){
		return p.x*p.x+p.y*p.y;
	}

	static public double dist(Point p1, Point p2){
		return Math.sqrt(dist2(p1,p2));
	}
	
	static public double dist2(Point p1, Point p2){
		return abs2(sub(p1, p2));
	}
	
	static public double scalarProd(Point p1, Point p2){
		return p1.x*p2.x+p1.y*p2.y;
	}
	
	static public boolean arePerpendiculars(Point p1, Point p2){
		return Math.abs(scalarProd(p1, p2))<EPSILON;
	}
	
	static public double vectorProd(Point p1, Point p2){
		return p1.x*p2.y-p2.x*p1.y;
	}
	
	static public boolean areParallels(Point p1, Point p2){
		return paralelogramArea(p1, p2)<EPSILON;
	}
	
	static public double paralelogramArea(Point p1, Point p2, Point p3){
		return paralelogramArea(sub(p1,p2), sub(p3,p2));
	}
	
	static public double paralelogramArea(Point p1, Point p2){
		return Math.abs(vectorProd(p1, p2));
	}
	
	/**
	 * Only works when the z coordinate is zero.
	 * 
	 * @param p1 - first vector
	 * @param p2 - second vector
	 * @return 1 if p1 is clockwise to p2, 0 if p1 is parallel to p2, -1 if p1 is counterclockwise to p2
	 */
	static public int vectorOrientation(Point p1, Point p2){
		double sign = vectorProd(p1, p2);
		if(Math.abs(sign)<EPSILON)
			return 0;
		if(sign>0)
			return 1;
		return -1;
	}
	
	
	/**
	 *
	 * @param p - the pivot of the radial sorting (it has to be below the rest of the point, if more than one applies, select the leftmost of them). 
	 * @return a comparator to do the sorting.
	 */
	static public Comparator<Point> radialComparatorFrom(final Point p){
		return new Comparator<Point>() {
			public int compare(Point o1, Point o2) {
				Point v1 = sub(o1,p);
				Point v2 = sub(o2,p);
				int sign= vectorOrientation(v1, v2);
				if(sign==0){
					double d1 = abs2(v1);
					double d2 = abs2(v2);
					if(Math.abs(d1-d2)<EPSILON)
						return 0;
					if(d1>d2){
						return 1;
					}else{
						return -1;
					}
				}
				return -sign;
			}
		};
	}
	
	static public Comparator<Point> xComparator(){
		return new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				double diffX=o1.x-o2.x;
				if(Math.abs(diffX)<EPSILON){
					double diffY=o1.y-o2.y;
					if(Math.abs(diffY)<EPSILON)
						return 0;
					if(diffY>0)
						return 1;
					return -1;
				}
				if(diffX>0)
					return 1;
				return -1;
			}
		};
	}
	
	static public Comparator<Point> yComparator(){
		return new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				double diffY=o1.y-o2.y;
				if(Math.abs(diffY)<EPSILON){
					double diffX=o1.x-o2.x;
					if(Math.abs(diffX)<EPSILON)
						return 0;
					if(diffX>0)
						return 1;
					return -1;
				}
				if(diffY>0)
					return 1;
				return -1;
			}
		};
	}
	
	/**
	 * 
	 * @param points - array of points sorted radially counterclockwise from the lowest point (if more than one applies the leftmost of them).
	 * @return A list of points, representing the vertices of the Convex Hull.
	 */
	static public List<Point> convexHullEfficient(Point[] points){
		List<Point> result = new ArrayList<Point>();
		if(points.length<4){
			for (int i = 0; i < points.length; i++) {
				result.add(points[i]);
			}
		}else{
			int k=0;
			Point[] stack = new Point[points.length];
			stack[k++]=points[0];
			stack[k++]=points[1];
			int i=2;
			while(i<points.length){
				Point a = stack[k-1];
				Point b = (k>1?stack[k-2]:null);
				if( k>1 && vectorOrientation(sub(points[i],a), sub(b,a))<=0){
					k--;
				}else{
					stack[k++]=points[i++];
				}
			}
			for(i=0; i<k; i++){
				result.add(stack[i]);
			}
		}
		return result;
	}
	
	static public List<Point> convexHull(Point[] points){
		Point[] ppoint = new Point[points.length];
		Point minleftPoint = null;
		Comparator<Point> yComp = yComparator();
		int k=1;
		for(Point p : points){
			if(minleftPoint==null || yComp.compare(p, minleftPoint)<0){
				if(minleftPoint!=null){
					ppoint[k++]=minleftPoint;
				}
				minleftPoint=p;
			}else{
				ppoint[k++]=p;
			}
		}
		Arrays.sort(ppoint,1,k, radialComparatorFrom(minleftPoint));
		ppoint[0]=minleftPoint;
		
		return convexHullEfficient(ppoint);
	}
	
	static public List<Point> convexHull(Collection<Point> points){
		Point[] ppoint = new Point[points.size()];
		Point minleftPoint = null;
		Comparator<Point> yComp = yComparator();
		int k=1;
		for(Point p : points){
			if(minleftPoint==null || yComp.compare(p, minleftPoint)<0){
				if(minleftPoint!=null){
					ppoint[k++]=minleftPoint;
				}
				minleftPoint=p;
			}else{
				ppoint[k++]=p;
			}
		}
		Arrays.sort(ppoint,1,k, radialComparatorFrom(minleftPoint));
		ppoint[0]=minleftPoint;
		
		return convexHullEfficient(ppoint);
	}
}