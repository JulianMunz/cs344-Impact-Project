package com.perlis.restservice;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

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

		// I used scanner in order to get the url
		Scanner in = new Scanner(System.in);
		System.out.println("enter url : ");
		url = in.nextLine();
		in.close();

		Document doc = null;
		try {
			String response = Jsoup.connect(url).execute().body();
			doc = Jsoup.parse(response);
			Elements elements = doc.select("*"); // matches all elements

			int no_of_sections = 0;
			for (Element e : elements) {
				no_of_sections++;

			}

			// transfer data to an array in order to remove repeated sections
			int count = 0;
			String[] sections = new String[no_of_sections];
			for (Element e : elements) {
				sections[count] = String.valueOf(e);
				count++;
			}

			// remove subsections
			for (int i = 0; i < no_of_sections; i++) {
				for (int j = 0; j < no_of_sections; j++) {

					String a = sections[i];
					String b = sections[j];
					if (i != j && isSubstring(b.trim(), a.trim()) != -1) {
						sections[j] = "";

					}

				}

			}

			// count number of sections after removing subsections
			int count_unique = 0;
			for (int i = 0; i < no_of_sections; i++) {
				if (sections[i] != "") {
					count_unique++;
				}
			}

			// transferring unique sections into array
			int k = 0;
			String individual_sections[] = new String[count_unique];
			for (int i = 0; i < no_of_sections; i++) {
				if (sections[i] != "") {
					individual_sections[k] = sections[i];
					k++;
				}
			}
			for (int i = 0; i < count_unique; i++) {
				System.out.println(individual_sections[i]);
				if ((i + 1) != count_unique) {
					System.out.print(",");
				}
			}
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		// scraping failed
		return "failed";
	}

	static int isSubstring(String s1, String s2) {
		int M = s1.length();
		int N = s2.length();

		/* A loop to slide pat[] one by one */
		for (int i = 0; i <= N - M; i++) {
			int j;

			/*
			 * For current index i, check for pattern match
			 */
			for (j = 0; j < M; j++)
				if (s2.charAt(i + j) != s1.charAt(j))
					break;

			if (j == M)
				return i;
		}

		return -1;
	}

}
