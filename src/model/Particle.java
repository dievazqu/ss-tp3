package model;

import utils.RandomUtils;

public class Particle {

	private int id;
	private Point position;
	private Point velocity;
	private double radius;
	private double mass;

	public Particle(int id, double x, double y, double vx, double vy, double m, double r) {
		this.id = id;
		this.position = new Point(x, y);
		this.velocity = new Point(vx, vy);
		this.mass = m;
		this.radius = r;
	}
	
	public Particle(int id, double x, double y, double velAbs, double m, double r) {
		this.id = id;
		this.position = new Point(x, y);
		double angle = RandomUtils.getRandomDouble(0, 2*Math.PI);
		this.velocity = new Point(velAbs * Math.cos(angle), velAbs * Math.sin(angle));
		this.mass = m;
		this.radius = r;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return position.x;
	}
	
	public double getY(){
		return position.y;
	}
	
	public double getXVelocity(){
		return velocity.x;
	}
	
	public double getYVelocity(){
		return velocity.y;
	}
	
	public double getMass(){
		return mass;
	}

	public double getRadius() {
		return radius;
	}
	
	public void move(double time){
		Point deltaPosition = velocity.clone();
		deltaPosition.applyFunction(v->v*time);
		position.add(deltaPosition);
	}
	
	public static boolean areOverlapped(Particle p, Particle q){
		return Math.pow(p.position.x-q.position.x, 2) + Math.pow(p.position.y-q.position.y, 2) <= Math.pow(p.radius+q.radius,2);
	}
	
	/**
	 * 
	 * @param xl - leftmost wall x-coordinate
	 * @param xr - rightmost wall x-coordinate
	 * @param p - Particle
	 * @return time to collide
	 */
	public static double timeToCollideVerticalWall(double xl, double xr, Particle p){
		if(Math.abs(p.velocity.x)<Point.EPSILON){
			return Double.POSITIVE_INFINITY;
		}
		if(p.velocity.x>=0){
			return (xr-p.radius-p.position.x) / p.velocity.x;
		}else{
			return (xl+p.radius-p.position.x) / p.velocity.x;
		}
	}
	
	/**
	 * 
	 * @param xb - top wall y-coordinate
	 * @param xt - bottom wall y-coordinate
	 * @param p - Particle
	 * @return time to collide
	 */
	public static double timeToCollideHorizontalWall(double yb, double yt, Particle p){
		if(Math.abs(p.velocity.y)<Point.EPSILON){
			return Double.POSITIVE_INFINITY;
		}
		if(p.velocity.y>=0){
			return (yt-p.radius-p.position.y) / p.velocity.y;
		}else{
			return (yb+p.radius-p.position.y) / p.velocity.y;
		}
	}
	
	public static double timeToCollide(Particle p, Particle q){
		Point deltaR = Point.sub(p.position, q.position).clone();
		Point deltaV = Point.sub(p.velocity, q.velocity).clone();
		double sigma = p.radius+q.radius;
		double prodVR = Point.scalarProd(deltaV, deltaR);
		double prodVV = Point.scalarProd(deltaV, deltaV);
		double prodRRsigma = Point.scalarProd(deltaR, deltaR) - Math.pow(sigma, 2); 
		
		double d = 
			Math.pow(prodVR, 2) -
			prodVV *
			prodRRsigma;
		
		if( d < 0 || prodVR >= 0 ){
			return Double.POSITIVE_INFINITY;
		}else{
			return - (prodVR + Math.sqrt(d)) / (prodVV);
		}
	}
	
	public static void verticalWallCollide(Particle p){
		p.velocity.x*=-1;
	}
	
	public static void horizontalWallCollide(Particle p){
		p.velocity.y*=-1;
	}
	
	/*
	 * Warning! p = xj and q = xi when reading the formulas.
	 */
	public static void particlesCollide(Particle p, Particle q){
		double deltaX = p.position.x - q.position.x;
		double deltaY = p.position.y - q.position.y;
		Point deltaR = Point.sub(p.position, q.position);
		Point deltaV = Point.sub(p.velocity, q.velocity);
		double sigma = p.radius+q.radius;
		double prodVR = Point.scalarProd(deltaV, deltaR);
		double j = 
			2 * p.mass * q.mass * prodVR /
			(sigma * (p.mass+q.mass));
		double jx = j * deltaX / sigma;
		double jy = j * deltaY / sigma;
		
		/* updating particles velocities */
		p.velocity.x -= jx/p.mass;
		p.velocity.y -= jy/p.mass;
		q.velocity.x += jx/q.mass;
		q.velocity.y += jy/q.mass;
	}

	public double getSpeed() {
		return velocity.abs();
	}
}
