package run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class Statistics {

	private List<Double> speeds1;
	private List<Double> speeds2;
	
	public Statistics() {
		speeds1 = new LinkedList<Double>();
		speeds2 = new LinkedList<Double>();
	}
	
	public void addSpeed1(Double speed){
		speeds1.add(speed);
	}
	
	public void addSpeed2(Double speed){
		speeds2.add(speed);
	}
	
	public void printStats(){
		try{
			File file = new File("stats1");
			OutputStream fos = new FileOutputStream(file);
			speeds1.stream().forEach(a->{
			try{
				fos.write((a+"\n").replace(".", ",").getBytes());
			}catch(Exception e){
				e.printStackTrace();
			}}
			);
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			File file = new File("stats2");
			OutputStream fos = new FileOutputStream(file);
			speeds2.stream().forEach(a->{
			try{
				fos.write((a+"\n").replace(".", ",").getBytes());
			}catch(Exception e){
				e.printStackTrace();
			}}
			);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
