package utils;

import java.util.Random;

public class RandomUtils {

	private static Random random = new Random();
	private static boolean seedSet = false;
	
	public static void setSeed(long seed){
		seedSet = true;
		random.setSeed(seed);
	}
	
	public static double getRandomDouble(double min, double max){
		if(!seedSet){
			System.err.println("Warning! Random seed not set");
		}
		return random.nextDouble()*(max-min)+min;
	}
	
	public static int getRandomInt(int min, int max){
		if(!seedSet){
			System.err.println("Warning! Random seed not set");
		}
		return random.nextInt(max-min+1)+min;
	}
}
