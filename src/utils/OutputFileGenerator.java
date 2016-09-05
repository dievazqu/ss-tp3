package utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import model.Particle;

public class OutputFileGenerator {

	private int frameNumber;
	private String path;
	
	public OutputFileGenerator(String directory, String file){
		frameNumber = 0;
		this.path = directory+file;
		try{
			Files.createDirectories(Paths.get(directory));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
		
	public void printState(List<Particle> particles){
		List<String> lines = new LinkedList<String>();
		lines.add(String.valueOf(particles.size()));
		lines.add("ParticleId xCoordinate yCoordinate xDisplacement yDisplacement Radius R G B Transparency");
		lines.add("0 0 0 0 0 0 0 0 0 1");
		lines.add("0 0.5 0 0 0 0 0 0 0 1");
		lines.add("0 0 0.5 0 0 0 0 0 0 1");
		lines.add("0 0.5 0.5 0 0 0 0 0 0 1");
		for(Particle p : particles){
			lines.add(getInfo(p));
		}
		writeFile(lines);
	}
	
	private String getInfo(Particle p) {
		return p.getId()+" "+p.getX()+" "+p.getY()+" "+p.getXVelocity()+" "+p.getYVelocity()+" "+p.getRadius()+" 0 0 1 0";
	}


	private void writeFile(List<String> lines){
		Path file = Paths.get(path+frameNumber+".xyz");
		frameNumber++;
		try {
			Files.write(file, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
