package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class OutputFileGenerator {
	
	private String path;
	private List<String> lines;
	
	public OutputFileGenerator(String directory, String file){
		this.path = directory+file;
		try{
			Files.createDirectories(Paths.get(directory));
		}catch(Exception e){
			e.printStackTrace();
		}
		lines = new LinkedList<String>();
	}
	
	public void addLine(String line){
		lines.add(line);
	}

	public void writeFile(){
		Path file = Paths.get(path+".txt");
		try {
			Files.write(file, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
