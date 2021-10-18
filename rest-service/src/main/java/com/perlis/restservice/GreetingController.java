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


import com.google.api.gax.paging.Page;
import com.google.auth.appengine.AppEngineCredentials;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import java.io.FileInputStream;
import java.io.IOException;

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
/*
		GoogleCredentials credential = GoogleCredentials.getApplicationDefault().createScoped(scopes);
		
		*/
		HttpRequestFactory requestFactory =
				httpTransport.createRequestFactory(new HttpCredentialsAdapter(credentials));
				
				
		HttpRequest request = requestFactory.buildRequest(method.getHttpMethod(), url, content);

		String response = request.execute().parseAsString();
		System.out.println(response);

	}
	}
}
