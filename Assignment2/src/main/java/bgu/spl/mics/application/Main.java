package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.services.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;

import java.io.Writer;
import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

	private static Thread Leia,HanSolo,C3PO,R2D2,Lando;
	public static void main(String[] args) {
		try {
			init(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		startSimulate();
		exportOutput(args[1]);


	}
	private static void startSimulate(){
		Diary.cdl=new CountDownLatch(4);
		Leia.start();
		HanSolo.start();
		C3PO.start();
		R2D2.start();
		Lando.start();


		try {
			Leia.join();
			HanSolo.join();
			C3PO.join();
			R2D2.join();
			Lando.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void init(String path) throws IOException {
		Input input = jsonReader.readJson(path);
		Leia = new Thread(new LeiaMicroservice(input.getAttacks()));
		HanSolo = new Thread(new HanSoloMicroservice());
		C3PO = new Thread(new C3POMicroservice());
		R2D2 = new Thread(new R2D2Microservice(input.getR2D2()));
		Lando = new Thread(new LandoMicroservice(input.getLando()));
		Ewoks.getInstance(input.Ewoks);
		Diary.getInstance();
	}

	private static void exportOutput(String path){
		try {
			Gson g = new Gson();
			Writer writer = new FileWriter(path);
			g.toJson(Diary.getInstance(),writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
