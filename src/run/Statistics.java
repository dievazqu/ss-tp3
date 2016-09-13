package run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class Statistics {

	private List<Double> dts;
	
	public Statistics() {
		dts = new LinkedList<Double>();
	}
	
	public void adddt(Double dt){
		dts.add(dt);
	}
	
	public void printStats(){
		try{
			File file = new File("stats");
			OutputStream fos = new FileOutputStream(file);
			fos.write(((dts.size()/40.0)+"\n").replace(".", ",").getBytes());
			fos.write((dts.stream().mapToDouble(a->a).average().getAsDouble()+"\n").replace(".", ",").getBytes());
			dts.stream().forEach(a->{
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
