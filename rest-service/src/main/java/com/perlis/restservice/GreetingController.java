package com.perlis.restservice;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.security.GeneralSecurityException;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.util.ArrayList;


import com.google.api.gax.paging.Page;
import com.google.auth.appengine.AppEngineCredentials;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

import java.io.IOException;


@RestController
@RequestMapping("/api")
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();


	@CrossOrigin
	@GetMapping(path = "/scrape")
	public ResponseEntity oldScrape(@RequestParam(value = "url", defaultValue = "https://www.google.com/") String url) {
		Document doc = null;
		ArrayList<HashMap<String, String>> sect_list = new ArrayList<>();
		try {
			String response = Jsoup.connect(url).execute().body();
			doc = Jsoup.parse(response);
			Document document = doc;

			Elements elements = document.getAllElements();
			for (Element element : elements) {
				if (element.className() != "") {
					Elements paragraphs = element.select(":root > p");
					HashMap obj = new HashMap();
					int parnum = paragraphs.size();
					obj.put("name", element.className());
					obj.put("par_num", String.valueOf(parnum));
					sect_list.add(obj);
				}
			}
			return new ResponseEntity(sect_list, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// scraping failed
		return null;
	}


	public void scrape(String url) {
		Document doc = null;
		File main_file = null;
		File header_file = null;
		File ad_file = null;
		File dis_file = null;

		Writer main_writer = null;
		Writer header_writer = null;
		Writer ad_writer = null;
		Writer dis_writer = null;

		try {
			main_file = new File("main.json");
			main_writer = new FileWriter(main_file);
			//main_writer.append("class,parnum\n");
			main_writer.append("{instances: [");

			header_file = new File("header.json");
			header_writer = new FileWriter(header_file);
			header_writer.append("{instances: [");

			ad_file = new File("adbanner.json");
			ad_writer = new FileWriter(ad_file);
			ad_writer.append("{instances: [");

			dis_file = new File("disclaimer.json");
			dis_writer = new FileWriter(dis_file);
			dis_writer.append("{instances: [");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String response = Jsoup.connect(url).execute().body();
			doc = Jsoup.parse(response);
			Document document = doc;
			Elements elements = document.getAllElements();
			int grandtotal = elements.size();
			int count = 0;
			int depth = 0;
			for (Element element : elements) {
				depth++;
				count++;
				if (element.tagName() == "html") depth = 0;
				String clname = element.className();
				//Main_Text
				Elements paragraphs = element.select(":root > p");
				int parnum = paragraphs.size();

				main_writer
						.append("[")
						.append(String.valueOf(parnum))
						.append("]");
				if (count != elements.size())
					main_writer.append(",");
				else
					main_writer.append("]}");

				//Header
				int score = 0;
				if (element.tagName() == "header") {
					score = 1000;
				}
				if (clname.contains("header")) score+=100;
				Elements children = element.children();

				for (Element child : children) {
					if (child.className().contains("logo")) {
						score++;
						break;
					}
				}
				for (Element child : children) {
					if (child.className().contains("nav")) {
						score+=10;
						break;
					}
				}
				header_writer
						.append("[")
						.append(String.valueOf(score))
						.append("]");
				if (count != elements.size())
					header_writer.append(",");
				else
					header_writer.append("]}");

				//Footer
				//WIP
				if (element.tagName() == "footer"
						|| (element.className().contains("footer")
						&& !element.className().contains("above"))) {
					int nothing = 0;
				}


				//Adbanner
				int numdeals = 0;
				int numlinks = 0;
				int thisdepth = 0;
				int total = 0;
				Elements deepchildren = element.select(":root *");
				total = deepchildren.size();
				if (total != 0) {
					for (Element deepchild : deepchildren) {
						String text = deepchild.ownText();
						if ((text.matches("(.*)[0-9]+(.*)points(.*)")
								|| text.matches("(.*)[0-9]+(.*)Points(.*)")
								|| text.matches("(.*)[0-9]+(.*)miles(.*)")
								|| text.matches("(.*)[0-9]+(.*)Miles(.*)"))
								&& text.length() < 60)
						{
							numdeals++;
						}
					}

					Elements childlinks = element.select(":root a");
					numlinks = childlinks.size();
					thisdepth = depth;
				}
				else total = 1;

				ad_writer
						.append("[")
						.append(String.valueOf((float)numdeals/(float)total))
						.append(",")
						.append(String.valueOf((float)numlinks/(float)total))
						.append(",")
						.append(String.valueOf((float)thisdepth/(float)grandtotal))
						.append("]");
				if (count != elements.size())
					ad_writer.append(",");
				else
					ad_writer.append("]}");


				//Disclaimer
				int discore = 0;
				if (clname.contains("disclosure") || clname.contains("disclaimer")) {
					discore = 10;
				}
				String distext = element.ownText();
				if (distext.contains("advertis")) discore++;
				if (distext.contains("partner")) discore++;
				if (distext.contains("links")) discore++;
				if (distext.contains("offer")) discore++;

				dis_writer
						.append("[")
						.append(String.valueOf(discore))
						.append("]");
				if (count != elements.size())
					dis_writer.append(",");
				else
					dis_writer.append("]}");

			}

			main_writer.flush();
			header_writer.flush();
			ad_writer.flush();
			dis_writer.flush();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void getPredictions() throws GeneralSecurityException, IOException{
		// You can specify a credential file by providing a path to GoogleCredentials.
  		// Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
  		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/diego/Documents/MY CODE FOR PROJECT/CS 344 PROJECT/cs344-Impact-Project/rest-service/src/main/resources/principal-bond-329416-05640e69962e.json"))
        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
  		Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();



  		System.out.println("Buckets:");
		Page<Bucket> buckets = storage.list();
		for (Bucket bucket : buckets.iterateAll()) {
			System.out.println(bucket.toString());


			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			Discovery discovery = new Discovery.Builder(httpTransport, jsonFactory, null).build();

			RestDescription api = discovery.apis().getRest("ml", "v1").execute();
			RestMethod method = api.getResources().get("projects").getMethods().get("predict");

			JsonSchema param = new JsonSchema();
			String projectId = "principal-bond-329416";
			// You should have already deployed a model and a version.
			// For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
			String modelId = "Impact";
			String versionId = "Version1";
			param.set(
					"name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));
			GenericUrl url = new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));
			System.out.println(url);
			String contentType = "application/json";
			File requestBodyFile = new File("main.json");
			HttpContent content = new FileContent(contentType, requestBodyFile);
			System.out.println(requestBodyFile);
			System.out.println(content.getLength());
			System.out.println(content);

			List<String> scopes = new ArrayList<>();
			scopes.add("https://www.googleapis.com/auth/cloud-platform");
			/*
				GoogleCredentials credential = GoogleCredentials.getApplicationDefault().createScoped(scopes);
			*/
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory(new HttpCredentialsAdapter(credentials));
			HttpRequest request = requestFactory.buildRequest(method.getHttpMethod(), url, content);

			String response = request.execute().parseAsString();
			//System.out.println(response);

			JSONObject json = new JSONObject(response);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			JsonNode root = rootNode.path("predictions");
			int count = 0;

			// counts numbers of predictions
			ArrayNode arrayNode = (ArrayNode) root;
			for (int i = 0; i < arrayNode.size(); i++) {
				JsonNode arrayElement = arrayNode.get(i);
				for (int j = 0; j < arrayElement.size(); j++) {
					JsonNode values = arrayElement.get("dense_1");
				}
				count++;
			}

			// classifies as important or not based on predictions
			String elements[] = new String[count];
			int k = 0;
			for (int i = 0; i < arrayNode.size(); i++) {
				JsonNode arrayElement = arrayNode.get(i);
				for (int j = 0; j < arrayElement.size(); j++) {
					JsonNode values = arrayElement.get("dense_1");
					if (values.get(0).asDouble() > values.get(1).asDouble()) {
						elements[k] = "maintext";
					} else {
						elements[k] = "none";
					}
					k++;
				}
			}
			System.out.println(count);

			for (int i = 0; i < elements.length; i++) {
				System.out.println(elements[i]);
			}

		}
	}
}
