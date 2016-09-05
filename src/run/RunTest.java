package run;

import java.util.ArrayList;
import java.util.List;

import model.Particle;
import utils.RandomUtils;

public class RunTest {

	public static void main(String[] args) {
		new RunTest();
	}
	
	private final double bigRadius = 0.05;
	private final double smallRadius = 0.005;
	private final double bigMass = 0.1;
	private final double smallMass = 0.0001;
	private final double L = 0.5;
	private final double minV = -0.1;
	private final double maxV = 0.1;
	private final int maxErrors = 5;
	
	private double time;
	
	public RunTest(){
		List<Particle> particles = createParticles();
		time = 0;
		int N = particles.size();
		double dt;
		double auxTime;
		Particle collider = null;
		Particle toCollide = null;
		boolean verticalWallCollide = false;
		boolean horizontalWallCollide = false;
		while(time < 5){
			dt = Double.MAX_VALUE;
			for(int i = 0; i < N; i++){
				Particle p = particles.get(i);
				auxTime = Particle.timeToCollideHorizontalWall(0, L, p); 
				if(auxTime < dt){
					dt = auxTime;
					collider = p;
					toCollide = null;
					horizontalWallCollide = true;
					verticalWallCollide = false;
				}
				auxTime = Particle.timeToCollideVerticalWall(0, L, p); 
				if(auxTime < dt){
					dt = auxTime;
					collider = p;
					toCollide = null;
					horizontalWallCollide = false;
					verticalWallCollide = true;
				}
				for(int j = i+1; j < N; j++){
					Particle q = particles.get(j);
					auxTime = Particle.timeToCollide(p, q); 
					if(auxTime < dt){
						dt = auxTime;
						collider = p;
						toCollide = q;
						horizontalWallCollide = false;
						verticalWallCollide = false;
					}					
				}				
			}
			time += dt;
			for(Particle p : particles){
				p.move(dt);
			}
			if(toCollide==null){
				if(horizontalWallCollide){
					Particle.horizontalWallCollide(collider);
				}
				if(verticalWallCollide){
					Particle.verticalWallCollide(collider);
				}
			}else{
				Particle.particlesCollide(collider, toCollide);
			}
		}
	}
	
	public List<Particle> createParticles(){
		List<Particle> particles = new ArrayList<Particle>();
		int id = 1;
		Particle bigParticle = new Particle(
			id++, 
			RandomUtils.getRandomDouble(bigRadius, L-bigRadius),
			RandomUtils.getRandomDouble(bigRadius, L-bigRadius),
			0,
			0,
			bigMass,
			bigRadius);
		particles.add(bigParticle);
		int errors = 0;
		while(errors < maxErrors){
			Particle smallParticle = new Particle(
					id, 
					RandomUtils.getRandomDouble(smallRadius, L-smallRadius),
					RandomUtils.getRandomDouble(smallRadius, L-smallRadius),
					RandomUtils.getRandomDouble(minV, maxV),
					RandomUtils.getRandomDouble(minV, maxV),
					smallMass,
					smallRadius);
			boolean areOverlapped = false;
			for(Particle p : particles){
				if(Particle.areOverlapped(smallParticle, p)){
					areOverlapped = true;
					break;
				}
			}
			if(areOverlapped){
				errors++;
			}else{
				errors = 0;
				id++;
				particles.add(smallParticle);
			}
		}
		return particles;
	}
}
