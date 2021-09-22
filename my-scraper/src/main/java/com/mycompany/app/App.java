package com.mycompany.app;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        
        String url = "https://thepointsguy.com/guide/credit-cards-over-1000-dollars-in-value/";

        Document doc = null;

        try {
            String response = Jsoup.connect(url).execute().body();
			doc = Jsoup.parse(response);
            Element divs = doc.select("div").first();
            String output = divs.outerHtml();

            ArrayList<String> allDivs = new ArrayList<>();
            
            Pattern pattern = Pattern.compile("</?div.*?>");

            Matcher matcher = pattern.matcher(output);
            while (matcher.find()) {
                allDivs.add(matcher.group());
            }
            //System.out.println(allDivs.toString());

            File file = new File("output.csv");
            Writer myWriter = new FileWriter(file);
            String eol = System.getProperty("line.seperator");

            int depth = 0;
            for (int i = 0; i < allDivs.size(); i++) {
                if (allDivs.get(i).charAt(1) == '/') depth--;
                else {
                    depth++;
                    myWriter.append(allDivs.get(i).toString())
                    .append(",")
                    .append(String.valueOf(depth))
                    .append("\n");
                }
            }
            myWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}