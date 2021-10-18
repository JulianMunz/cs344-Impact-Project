package com.perlis.restservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.security.GeneralSecurityException;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@CrossOrigin(origins = "http://localhost:8080")
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

	@CrossOrigin(origins = "http://localhost:8080")
	@GetMapping(value = "/getPredictions")
	public void getPredictions() throws GeneralSecurityException, IOException{
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		Discovery discovery = new Discovery.Builder(httpTransport, jsonFactory, null).build();

		RestDescription api = discovery.apis().getRest("ml", "v1").execute();
		RestMethod method = api.getResources().get("projects").getMethods().get("predict");

		JsonSchema param = new JsonSchema();
		String projectId = "wide-river-326910";
		// You should have already deployed a model and a version.
		// For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
		String modelId = "Impact";
		String versionId = "Version2";
		param.set(
				"name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));
		GenericUrl url =
				new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));
		System.out.println(url);

		String contentType = "application/json";
		File requestBodyFile = new File("main.json");
		HttpContent content = new FileContent(contentType, requestBodyFile);
		System.out.println(requestBodyFile);
		System.out.println(content.getLength());
		System.out.println(content);

		List<String> scopes = new ArrayList<>();
		scopes.add("https://www.googleapis.com/auth/cloud-platform");

		GoogleCredentials credential = GoogleCredentials.getApplicationDefault().createScoped(scopes);
		HttpRequestFactory requestFactory =
				httpTransport.createRequestFactory(new HttpCredentialsAdapter(credential));
		HttpRequest request = requestFactory.buildRequest(method.getHttpMethod(), url, content);

		String response = request.execute().parseAsString();
		System.out.println(response);

	}
}
