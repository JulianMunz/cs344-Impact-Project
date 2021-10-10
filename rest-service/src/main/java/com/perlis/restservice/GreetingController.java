package com.perlis.restservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/scrape")
	public String scrape(@RequestParam(value = "url", defaultValue = "https://www.google.com/") String url) {
		Document doc = null;
		ArrayList<JSONObject> sect_list = new ArrayList<>();
		try {
			String response = Jsoup.connect(url).execute().body();
			doc = Jsoup.parse(response);
			Document document = doc;

			Elements elements = document.getAllElements();
			for (Element element : elements) {
				if (element.className() != "") {
					Elements paragraphs = element.select(":root > p");
					JSONObject obj = new JSONObject();
					int parnum = paragraphs.size();
					obj.append("name", element.className());
					obj.append("par_num", String.valueOf(parnum));
					sect_list.add(obj);
				}
			}
			System.out.println();
			PythonHandler py = new PythonHandler("/classifier.py");
			return py.output(sect_list.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// scraping failed
		return null;
	}
}
