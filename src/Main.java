import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Main {

	public static double fundamentalHarm = 110.0;
	public static int nHarm = 128;
	
	public static double sampleRate = 44100.0;
	public static double frequency = 440;
	public static double frequency2 = 90;
	public static double amplitude = 1;
	public static double seconds = 1.0;

	public static void main(String[] args) {

		try{
			createSawWave();
		}catch(IOException e){
			e.printStackTrace();
		}

	}
	
	public static void createSawWave() throws IOException{

		float[] buffer = new float[(int) (seconds * sampleRate)];
		
		//Duration loop for filling the sample with the desired harmonics
		
		for (int sample = 0; sample < buffer.length; sample++) {

			double time = sample / sampleRate;
			
			//Loop for adding harmonics to create a waveform
			
			double twoPiH = 2 * Math.PI * fundamentalHarm;
			
			float totalHarm = 0;
						
			for (int i = 1; i <= nHarm; i++) {
				
				totalHarm += (float) ((amplitude * Math.cos((double)(twoPiH * i) * time))) / i;
								
			}

			buffer[sample] = totalHarm;

		}

		createFile(buffer, sampleRate);
	}

	public static void createSineWave() throws IOException{

		double twoPiF = 2 * Math.PI * frequency;
		double piF = Math.PI * frequency2;

		float[] buffer = new float[(int) (seconds * sampleRate)];

		for (int sample = 0; sample < buffer.length; sample++) {

			double time = sample / sampleRate;

			//F4
			float harmonic1 = (float) (amplitude * Math.cos((double)(twoPiF / Math.pow(2.0, 1.0/3.0)) * time));

			//C4
			float harmonic2 = (float) (amplitude * Math.cos((double)(twoPiF / Math.pow(2.0, 3.0/4.0)) * time));

			buffer[sample] = harmonic1 + harmonic2;

		}

		createFile(buffer, sampleRate);

	}

	public static void createFile(float[] buffer, double sampleRate) throws IOException{

		final byte[] byteBuffer = new byte[buffer.length * 2];
		int bufferIndex = 0;

		//Compressor (not clipping amplitude)

		float max = buffer[0];

		for (int i = 1; i < buffer.length; i++) {
			if (buffer[i] > max) {
				max = buffer[i];
			}
		}

		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = buffer[i] / max;
		}

		for (int i = 0; i < byteBuffer.length; i++) {

			final int x = (int) (buffer[bufferIndex++] * 32767.0);
			byteBuffer[i] = (byte) x;
			i++;
			byteBuffer[i] = (byte) (x >>> 8);
		}

		File out = new File("finalSample.wav");

		boolean bigEndian = false;
		boolean signed = true;
		int bits = 16;
		int channels = 1;

		AudioFormat format;

		format = new AudioFormat((float)sampleRate, bits, channels, signed, bigEndian);
		ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);

		AudioInputStream audioInputStream;

		audioInputStream = new AudioInputStream(bais, format,buffer.length);
		AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
		audioInputStream.close();
		
		System.out.println("File created");

	}

}