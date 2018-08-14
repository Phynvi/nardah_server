package com.nardah.content.store;

import com.google.gson.JsonParser;
import com.nardah.content.store.impl.PersonalStore;
import com.nardah.util.GsonUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PersonalStoreSaver {
	
	private static final String PAYMENT_PATH = "/def/content/game/personal_stores_payments.json";
	
	public static void savePayments() {
		new Thread(() -> {
			try(FileWriter fw = new FileWriter("./data" + PAYMENT_PATH)) {
				fw.write(GsonUtils.JSON_PRETTY_ALLOW_NULL.toJson(PersonalStore.SOLD_ITEMS));
			} catch(final Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public static void loadPayments() {
		Path path = Paths.get("data", PAYMENT_PATH);
		
		if(!Files.exists(path)) {
			return;
		}
		
		try(FileReader reader = new FileReader(path.toFile())) {
			JsonParser parser = new JsonParser();
			// PersonalStore.SOLD_ITEMS = new
			// GsonBuilder().create().fromJson(parser.parse(reader), type);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
