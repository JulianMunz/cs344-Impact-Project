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
            Document document = doc;
            /*
            Element divs = document.select("div").first();
            String output = divs.outerHtml();

            

            ArrayList<String> allDivs = new ArrayList<>();
            
            Pattern pattern = Pattern.compile("</?div.*?>");
            Pattern paragraphPattern = Pattern.compile("<p>");
            Pattern quotes = Pattern.compile("\"([^\"]*)\"");
            Pattern divClosingTag = Pattern.compile("</div>");
            Matcher matcher = pattern.matcher(output);

            while (matcher.find()) {
                allDivs.add(matcher.group());
            }
            //System.out.println(allDivs.toString());
            */


            File file = new File("output.csv");
            Writer myWriter = new FileWriter(file);
            myWriter.append("class,parnum\n");
            
            //Simon
            Elements elements = document.getAllElements();
            for (Element element : elements) {
                if (element.className() != "") {
                    System.out.println(element.className());
                    Elements paragraphs = element.select(":root > p");
                    
                    int parnum = paragraphs.size();
                    System.out.println(parnum);
                    myWriter
                    .append(element.className())                  
                    .append(",")
                    .append(String.valueOf(parnum))
                    .append("\n");
                }
            }

            

            /*
            String eol = System.getProperty("line.seperator");
            
            int depth = 0;
            Element div = null;
            for (int i = 0; i < allDivs.size(); i++) {
                int paragraphs = 0;
                Matcher divMatches = quotes.matcher(allDivs.get(i).toString());
                if (divMatches.find()) {
                    String match = divMatches.group();
                    match = match.replaceAll("\"", "");
                    Elements matchingDivs = document.getElementsByClass(match);
                    div = matchingDivs.first();
                    if (div != null) {
                        Elements divChildren = div.select(":root > p");
                        paragraphs = divChildren.size();
                    }
                }
                       
                if (allDivs.get(i).charAt(1) == '/') depth--;
                else {
                    depth++;
                    if (div != null) {
                    //System.out.println(div.className());
                    myWriter
                    .append(div.className())                  
                    .append(",")
                    .append(String.valueOf(paragraphs))
                    .append("\n");
                    }
                }
            }
            */
            myWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}
