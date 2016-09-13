package run;

import java.util.ArrayList;
import java.util.List;

import model.Particle;
import utils.OutputFileGenerator;
import utils.OutputXYZFilesGenerator;
import utils.RandomUtils;

public class BrownianMotion {
	
	public static void main(String[] args) {
 		new BrownianMotion(0.05, 0.005, 0.1, 0.0001, 0.5, 0, 0.1, 5, 60, 23456, true, 300);
 		//new BrownianMotion(0.05, 0.005, 0.1, 0.0001, 0.5, -0.1, 0.1, 5, 60, 23456, true, 200);
	}
	
	public BrownianMotion(double bigRadius, double smallRadius, double bigMass, double smallMass, double l, double minV,
			double maxV, int maxErrors, int fps, int seed, boolean print, int N) {
		super();
		this.bigRadius = bigRadius;
		this.smallRadius = smallRadius;
		this.bigMass = bigMass;
		this.smallMass = smallMass;
		L = l;
		this.minV = minV;
		this.maxV = maxV;
		this.maxErrors = maxErrors;
		deltaTime = 1.0 / fps;
		printOutput = print; 
		this.seed = seed;
		RandomUtils.setSeed(seed);
		this.N = N;
		this.run();
	}
	
	public static Statistics stats;

	
	
	private final double bigRadius;
	private final double smallRadius;
	private final double bigMass;
	private final double smallMass;
	private final double L;
	private final double minV;
	private final double maxV;
	private final int maxErrors;
	private final double deltaTime;
	private final boolean printOutput;
	private final int N;
	private final int seed;
	private final double maxTime = 40.0;
	
	
	private double time;

	public void run() {
		double speedMax = 0.0;
		OutputXYZFilesGenerator outputXYZFilesGenerator = new OutputXYZFilesGenerator("animation/", "state");
		List<Particle> particles = createParticles(N, false);
		time = 0;
		int N = particles.size();
		System.out.println("N: "+N);
		double dt;
		double auxTime;
		Particle collider = null;
		Particle toCollide = null;
		boolean verticalWallCollide = false;
		boolean horizontalWallCollide = false;
		double lastTime = 0;
		if (printOutput) {
			outputXYZFilesGenerator.printState(particles);	
		}
		System.out.println("K: "+calculateK(particles));
		while (time < maxTime) {
			dt = Double.MAX_VALUE;
			for (int i = 0; i < N; i++) {
				Particle p = particles.get(i);
				speedMax = Math.max(speedMax, p.getSpeed());
				auxTime = Particle.timeToCollideHorizontalWall(0, L, p);
				if (auxTime < dt) {
					dt = auxTime;
					collider = p;
					toCollide = null;
					horizontalWallCollide = true;
					verticalWallCollide = false;
				}
				auxTime = Particle.timeToCollideVerticalWall(0, L, p);
				if (auxTime < dt) {
					dt = auxTime;
					collider = p;
					toCollide = null;
					horizontalWallCollide = false;
					verticalWallCollide = true;
				}
				for (int j = i + 1; j < N; j++) {
					Particle q = particles.get(j);
					auxTime = Particle.timeToCollide(p, q);
					if (auxTime < dt) {
						dt = auxTime;
						collider = p;
						toCollide = q;
						horizontalWallCollide = false;
						verticalWallCollide = false;
					}
				}
			}
			if (time + dt > lastTime + deltaTime) {
				if (printOutput) {
					outputXYZFilesGenerator.printState(particles);
				}
				lastTime = time;
					
			}
			time += dt;
			for (Particle p : particles) {
				p.move(dt);
			}
			if (toCollide == null) {
				if (horizontalWallCollide) {
					Particle.horizontalWallCollide(collider);
				}
				if (verticalWallCollide) {
					Particle.verticalWallCollide(collider);
				}
			} else {
				Particle.particlesCollide(collider, toCollide);
			}
		}
	}

	public List<Particle> createParticles(int N, boolean centerBigParticle) {
		List<Particle> particles = new ArrayList<Particle>();
		// The particle with id 1 is the one with a big mass
		int id = 1;
		Particle bigParticle;
		if (centerBigParticle) {
			bigParticle = new Particle(id++, L/2, L/2, 0, bigMass, bigRadius);
		} else {
			bigParticle = new Particle(id++, RandomUtils.getRandomDouble(bigRadius, L - bigRadius),
					RandomUtils.getRandomDouble(bigRadius, L - bigRadius), 0, bigMass, bigRadius);	
		}
		particles.add(bigParticle);
		
		while(particles.size() < N) {
			Particle smallParticle = new Particle(id, RandomUtils.getRandomDouble(smallRadius, L - smallRadius),
					RandomUtils.getRandomDouble(smallRadius, L - smallRadius),
					RandomUtils.getRandomDouble(minV, maxV), smallMass, smallRadius);
			boolean areOverlapped = false;
			for (Particle p : particles) {
				if (Particle.areOverlapped(smallParticle, p)) {
					areOverlapped = true;
					break;
				}
			}
			if (!areOverlapped) {
				id++;
				particles.add(smallParticle);	
			}
		}
		return particles;
	}
	
	public List<Particle> createParticles() {
		List<Particle> particles = new ArrayList<Particle>();
		// The particle with id 1 is the one with a big mass
		int id = 1;
		Particle bigParticle = new Particle(id++, RandomUtils.getRandomDouble(bigRadius, L - bigRadius),
				RandomUtils.getRandomDouble(bigRadius, L - bigRadius), 0, 0, bigMass, bigRadius);
		particles.add(bigParticle);
		int errors = 0;
		while (errors < maxErrors) {
			Particle smallParticle = new Particle(id, RandomUtils.getRandomDouble(smallRadius, L - smallRadius),
					RandomUtils.getRandomDouble(smallRadius, L - smallRadius), RandomUtils.getRandomDouble(minV, maxV),
					RandomUtils.getRandomDouble(minV, maxV), smallMass, smallRadius);
			boolean areOverlapped = false;
			for (Particle p : particles) {
				if (Particle.areOverlapped(smallParticle, p)) {
					areOverlapped = true;
					break;
				}
			}
			if (areOverlapped) {
				errors++;
			} else {
				errors = 0;
				id++;
				particles.add(smallParticle);
			}
		}
		return particles;
	}

	public double calculateK(List<Particle> particles) {
		double K = 0.0;
		for (Particle p : particles) {
			K += p.getMass() * Math.pow(p.getSpeed(), 2);
		}
		return 0.5*K;
	}

}
