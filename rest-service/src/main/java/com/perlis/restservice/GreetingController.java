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

	public Elements scrape(String url) {
		Document doc = null;
		File main_file = null;
		File footer_file = null;
		File header_file = null;
		File ad_file = null;
		File dis_file = null;

		Writer main_writer = null;
		Writer footer_writer = null;
		Writer header_writer = null;
		Writer ad_writer = null;
		Writer dis_writer = null;

		try {
			main_file = new File("main.json");
			main_writer = new FileWriter(main_file);
			// main_writer.append("class,parnum\n");
			main_writer.append("{\"instances\": [");

			footer_file = new File("footer.json");
			footer_writer = new FileWriter(footer_file);
			footer_writer.append("{\"instances\": [");

			header_file = new File("header.json");
			header_writer = new FileWriter(header_file);
			header_writer.append("{\"instances\": [");

			ad_file = new File("adbanner.json");
			ad_writer = new FileWriter(ad_file);
			ad_writer.append("{\"instances\": [");

			dis_file = new File("disclaimer.json");
			dis_writer = new FileWriter(dis_file);
			dis_writer.append("{\"instances\": [");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String response = Jsoup.connect(url).execute().body();
			doc = Jsoup.parse(response);
			Document document = doc;
			Elements elements = document.getAllElements();
			int grandtotal = elements.size();
			System.out.println(""+grandtotal);
			int count = 0;
			int depth = 0;
			for (Element element : elements) {
				depth++;
				count++;
				if (element.tagName() == "html")
					depth = 0;
				String clname = element.className();
				// Main_Text
				Elements paragraphs = element.select(":root > p");
				int parnum = paragraphs.size();
				if (parnum > 30)
					parnum = 30;
				parnum = parnum * parnum;
				main_writer.append("[").append(String.valueOf(parnum)).append("]");
				if (count != elements.size())
					main_writer.append(",");
				else
					main_writer.append("]}");

				// Header
				int score = 0;
				if (element.tagName() == "header") {
					score = 1;
				}
				if (clname.contains("header"))
					score = 1;
				header_writer.append("[").append(String.valueOf(score)).append("]");
				if (count != elements.size())
					header_writer.append(",");
				else
					header_writer.append("]}");

				// Footer
				int footerscore = 0;
				if (element.tagName() == "footer") {
					footerscore = 1;
				}
				footer_writer.append("[").append(String.valueOf(footerscore)).append("]");
				if (count != elements.size())
					footer_writer.append(",");
				else
					footer_writer.append("]}");

				// Adbanner
				int adscore = 0;
				if (element.tagName() == "aside") {
					adscore = 1;
				}
				if (clname.contains("sidebar")) {
					adscore = 1;
				}

				ad_writer.append("[").append(String.valueOf(adscore)).append("]");
				if (count != elements.size())
					ad_writer.append(",");
				else
					ad_writer.append("]}");

				// Disclaimer
				int discore = 0;

				String distext = element.ownText();
				if (distext.toLowerCase().contains("advertis"))
					discore++;
				if (distext.toLowerCase().contains("partner"))
					discore++;
				if (distext.toLowerCase().contains("links"))
					discore++;
				if (distext.toLowerCase().contains("offer"))
					discore++;
				if (distext.toLowerCase().contains("listed"))
					discore++;
				if (distext.toLowerCase().contains("opinions"))
					discore++;
				if (distext.toLowerCase().contains("terms apply"))
					discore++;
				if (distext.toLowerCase().contains("compensat"))
					discore++;
				if (distext.toLowerCase().contains("refer"))
					discore++;
				if (distext.toLowerCase().contains("products"))
					discore++;
				if (distext.toLowerCase().contains("reviewed"))
					discore++;
				if (distext.toLowerCase().contains("approved"))
					discore++;
				if (distext.toLowerCase().contains("endorsed"))
					discore++;

				if (discore > 2)
					discore = 1;
				else
					discore = 0;

				if (clname.contains("disclosure") || clname.contains("disclaimer")) {
					discore = 1;
				}

				dis_writer.append("[").append(String.valueOf(discore)).append("]");
				if (count != elements.size())
					dis_writer.append(",");
				else
					dis_writer.append("]}");

			}
			System.out.println(""+count);
			main_writer.flush();
			footer_writer.flush();
			header_writer.flush();
			ad_writer.flush();
			dis_writer.flush();
			return elements;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	@CrossOrigin
	@GetMapping(value = "/getPredictions")
	public ArrayList<String> getPredictions(@RequestParam(value="url",defaultValue="https://www.google.com/")String s_url) throws GeneralSecurityException, IOException {
		// GCP credentials
		Elements elements = scrape(s_url);
		System.out.println(s_url);
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(
				"src/main/resources/"))
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
		Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

		String cls[] = null;

		ArrayList mains = new ArrayList<String>();
		//String[] sections = { "main", "adbanner", "header", "footer", "disclaimer" };
		//int index = 0;

		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		Discovery discovery = new Discovery.Builder(httpTransport, jsonFactory, null).build();
		RestDescription api = discovery.apis().getRest("ml", "v1").execute();
		RestMethod method = api.getResources().get("projects").getMethods().get("predict");
		JsonSchema param = new JsonSchema();
		String projectId = "";
		// You should have already deployed a model and a version.
		// For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.

		String modelId1 = "maintext";
		String versionId_MainText = "maintext";
		param.set("name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId1, versionId_MainText));
		GenericUrl url_maintext = new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));

		String modelId2 = "adbanner";
		String versionId_adBanner = "adbanner";
		param.set("name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId2, versionId_adBanner));
		GenericUrl url_adbanner = new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));

		String modelId3 = "header";
		String versionId_header = "header";
		param.set("name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId3, versionId_header));
		GenericUrl url_header = new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));

		String modelId4 = "footer";
		String versionId_footer = "footer";
		param.set("name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId4, versionId_footer));
		GenericUrl url_footer = new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));

		String modelId5 = "disclaimer";
		String versionId_disclaimer = "disclaimer";
		param.set("name",
				String.format("projects/%s/models/%s/versions/%s", projectId, modelId5, versionId_disclaimer));
		GenericUrl url_disclaimer = new GenericUrl(
				UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));

		// Read json file that is going to be sent to GCP
		String contentType = "application/json";
		File requestBodyFileForMaintext = new File("main.json");
		File requestBodyFileForadBanner = new File("adbanner.json");
		File requestBodyFileForheader = new File("header.json");
		File requestBodyFileForfooter = new File("footer.json");
		File requestBodyFileFordisclaimer = new File("disclaimer.json");

		HttpContent contentForMainText = new FileContent(contentType, requestBodyFileForMaintext);
		HttpContent contentForadBanner = new FileContent(contentType, requestBodyFileForadBanner);
		HttpContent contentForheader = new FileContent(contentType, requestBodyFileForheader);
		HttpContent contentForfooter = new FileContent(contentType, requestBodyFileForfooter);
		HttpContent contentFordisclaimer = new FileContent(contentType, requestBodyFileFordisclaimer);

		List<String> scopes = new ArrayList<>();
		scopes.add("https://www.googleapis.com/auth/cloud-platform");
		/*
		 * GoogleCredentials credential =
		 * GoogleCredentials.getApplicationDefault().createScoped(scopes);
		 */

		// Request sent to GCP
		HttpRequestFactory requestFactory = httpTransport.createRequestFactory(new HttpCredentialsAdapter(credentials));
		HttpRequest requestForMainText = requestFactory.buildRequest(method.getHttpMethod(), url_maintext,
				contentForMainText);
		HttpRequest requestForadBanner = requestFactory.buildRequest(method.getHttpMethod(), url_adbanner,
				contentForadBanner);
		HttpRequest requestForheader = requestFactory.buildRequest(method.getHttpMethod(), url_header,
				contentForheader);
		HttpRequest requestForfooter = requestFactory.buildRequest(method.getHttpMethod(), url_footer,
				contentForfooter);
		HttpRequest requestFordisclaimer = requestFactory.buildRequest(method.getHttpMethod(), url_disclaimer,
				contentFordisclaimer);

		String responseForMainText = requestForMainText.execute().parseAsString();
		String responseForadBanner = requestForadBanner.execute().parseAsString();
		String responseForheader = requestForheader.execute().parseAsString();
		String responseForfooter = requestForfooter.execute().parseAsString();
		String responseFordisclaimer = requestFordisclaimer.execute().parseAsString();


		// converts results from gcp to json
		ObjectMapper mapper1 = new ObjectMapper();
		JsonNode rootNode1 = mapper1.readTree(responseForMainText);
		JsonNode root1 = rootNode1.path("predictions");
		int countForMainText = 0;

		ObjectMapper mapper2 = new ObjectMapper();
		JsonNode rootNode2 = mapper2.readTree(responseForadBanner);
		JsonNode root2 = rootNode2.path("predictions");
		int countForadBanner = 0;

		ObjectMapper mapper3 = new ObjectMapper();
		JsonNode rootNode3 = mapper3.readTree(responseForheader);
		JsonNode root3 = rootNode3.path("predictions");
		int countForHeader = 0;


		ObjectMapper mapper4 = new ObjectMapper();
		JsonNode rootNode4 = mapper4.readTree(responseForfooter);
		JsonNode root4 = rootNode4.path("predictions");
		int countForfooter = 0;


		ObjectMapper mapper5 = new ObjectMapper();
		JsonNode rootNode5 = mapper5.readTree(responseFordisclaimer);
		JsonNode root5 = rootNode5.path("predictions");
		int countFordisclaimer = 0;

		// counts number of predictions - Maintext
		ArrayNode arrayNode1 = (ArrayNode) root1;
		ArrayNode arrayNode2 = (ArrayNode) root2;
		ArrayNode arrayNode3 = (ArrayNode) root3;
		ArrayNode arrayNode4 = (ArrayNode) root4;
		ArrayNode arrayNode5 = (ArrayNode) root5;
		
		// classifies as important or not based on predictions
		cls = new String[countForMainText + countForadBanner + countForHeader + countForfooter + countFordisclaimer];
		System.out.println(countForMainText);
		System.out.println(countForadBanner);
		System.out.println(countForHeader);
		System.out.println(countForfooter);
		System.out.println(countFordisclaimer);
	//cls = new String[countForMainText + countForHeader  + countFordisclaimer];

		//initialize
		for (int i = 0; i < cls.length; i++)
			cl[i] = "none";

		// maintext
		for (int i = 0; i < arrayNode1.size(); i++) {
			JsonNode arrayElement = arrayNode1.get(i);
			for (int j = 0; j < arrayElement.size(); j++) {
				JsonNode values = arrayElement.get("dense_1");
				if (values.get(0).asDouble() > values.get(1).asDouble())
					cls[j] = "main";
			}
		}

		// adbanner
		for (int i = 0; i < arrayNode2.size(); i++) {
			JsonNode arrayElement = arrayNode2.get(i);
			for (int j = 0; j < arrayElement.size(); j++) {
				JsonNode values = arrayElement.get("softmax_2");
				if (values.get(0).asDouble() > values.get(1).asDouble())
					cls[j] = "adbanner";
			}
		}

		// header
		for (int i = 0; i < arrayNode3.size(); i++) {
			JsonNode arrayElement = arrayNode3.get(i);
			for (int j = 0; j < arrayElement.size(); j++) {
				JsonNode values = arrayElement.get("softmax_1");
				if (values.get(0).asDouble() > values.get(1).asDouble())
					cls[j] = "header";
			}
		}

		// footer
		for (int i = 0; i < arrayNode4.size(); i++) {
			JsonNode arrayElement = arrayNode4.get(i);
			for (int j = 0; j < arrayElement.size(); j++) {
				JsonNode values = arrayElement.get("softmax_2");
				if (values.get(0).asDouble() > values.get(1).asDouble())
					cls[j] = "footer";
			}
		}

		// disclaimer
		for (int i = 0; i < arrayNode5.size(); i++) {
			JsonNode arrayElement = arrayNode5.get(i);
			for (int j = 0; j < arrayElement.size(); j++) {
				JsonNode values = arrayElement.get("softmax");
				if (values.get(0).asDouble() > values.get(1).asDouble())
					cls[j] = "disclaimer";
			}
		}


			int count = 0;
			for (Element element : elements) {
				if (cls[count].equals("main")) {
					//System.out.println("ELEMENT: " + element.outerHtml());
					mains.add("1"+element.className());
				}
				if (cls[count].equals("footer")) {
					//System.out.println("ELEMENT: " + element.outerHtml());
					mains.add("2"+element.className());
				}
				if (cls[count].equals("header")) {
					//System.out.println("ELEMENT: " + element.outerHtml());
					mains.add("3"+element.className());
				}
				if (cls[count].equals("adbanner")) {
					//System.out.println("ELEMENT: " + element.outerHtml());
					mains.add("4"+element.className());
				}
				if (cls[count].equals("disclaimer")) {
					//System.out.println("ELEMENT: " + element.outerHtml());
					mains.add("5"+element.className());
				}
				count++;
			}

			
		/*
		 for (int i = 0; i < cls.length; i++) {
		 System.out.println(cls[i]);
		 }
*/

		return mains;
	}
}
