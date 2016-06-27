package TimmyBot;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class TimmyBot {
	
	static RegulatedMotor leftMotor;
	static RegulatedMotor rightMotor;
	static EV3ColorSensor sensorMonedas;
	static EV3TouchSensor sensorTacto;
	static EV3UltrasonicSensor sensorUR;
	static boolean pago;
	
	public enum modos{
		IZQUIERDA, DERECHA;
	}
	
	static modos modo;

	public static void main(String[] args) {
		modo = modos.IZQUIERDA; //default echa confites de la izq
		pago = false;
		float [] lightSamples = new float[1];
		int colorMoneda = 13;
		int colorDetectado;
		SampleProvider lightSample;
		
		//se inicializan los sensores
		
		leftMotor = new EV3LargeRegulatedMotor(MotorPort.D);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		sensorMonedas = new EV3ColorSensor(SensorPort.S1);
		sensorTacto = new EV3TouchSensor(SensorPort.S3);
		sensorUR = new EV3UltrasonicSensor(SensorPort.S4);
		
		leftMotor.setSpeed(300);
		rightMotor.setSpeed(300);
		
		//se detecta la luz actual
		sensorMonedas.setFloodlight(true);
		
		System.out.println("Timmy Candy Machine. Presione cualquier boton...");
		Button.waitForAnyPress();
		LCD.clearDisplay();
		System.out.println("Inserte una moneda de 100 colones y gire la perilla");
		while(true){
			//lightSample = sensorMonedas.getAmbientMode();
			//lightSample.fetchSample(lightSamples, 0);
			//newLightValue = lightSamples[0];
			
			colorDetectado = sensorMonedas.getColorID();
			if(colorDetectado == colorMoneda){
				LCD.clearDisplay();
				System.out.println("Moneda detectada");
				boolean monedaInsertada = false;
				while(!monedaInsertada){
					colorDetectado = sensorMonedas.getColorID();
					if(colorDetectado != colorMoneda){
						monedaInsertada = true;
						setupConfites();
						System.out.println("Ain't nuthin but a peanut!");
						Delay.msDelay(3000);
						LCD.clearDisplay();
					}
				}
			}
		}

	}
	
	public static void setupConfites(){
		System.out.println("LIGHTWEIGHT");
		SampleProvider sensorSample;
		float [] samples = new float[1];
		float distanciaInicial;
		
		imprimirModo();
		//lightweight
		
		sensorSample = sensorUR.getDistanceMode();
		sensorSample.fetchSample(samples, 0);
		distanciaInicial = samples[0];
		
		boolean detectoMano = false;
		while(!detectoMano){
			sensorSample = sensorTacto.getTouchMode();
			sensorSample.fetchSample(samples, 0);
			if(samples[0] == 1){
				cambiarModo();
			}
			Delay.msDelay(500);
			sensorSample.fetchSample(samples, 0);
			if(samples[0] == 1){
				cambiarModo();
			}
			sensorSample = sensorUR.getDistanceMode();
			sensorSample.fetchSample(samples, 0);
			if(samples[0] <= 0.06){
				//dar confites
				detectoMano = true;
				Delay.msDelay(1000);
				if(modo == modos.IZQUIERDA){
					leftMotor.rotateTo(45);
					Delay.msDelay(500);
					leftMotor.rotateTo(-60);
				}
				else{
					rightMotor.rotateTo(45);
					Delay.msDelay(500);
					leftMotor.rotateTo(-60);
				}
			}		
		}
	}
	
	public static void cambiarModo(){
		modo = (modo == modo.IZQUIERDA)? modo.DERECHA: modo.IZQUIERDA;
		imprimirModo();
	}
	
	public static void imprimirModo(){
		LCD.clearDisplay();
		if(modo == modos.IZQUIERDA){
			System.out.println("M&M's");
		}
		else{
			System.out.println("SKITTLES");
		}
	}
}
