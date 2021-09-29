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

       ArrayList<String> urls = new ArrayList<>();

        urls.add("https://thepointsguy.com/guide/credit-cards-over-1000-dollars-in-value/");
        urls.add("https://thepointsguy.com/news/card-recommendation-for-30000-home-renovation/");
        urls.add("https://thepointsguy.com/reviews/the-equniox-golf-resort-and-spa-luxury-collection/");
        urls.add("https://thepointsguy.com/guide/points-miles-to-croatia/");
        urls.add("https://thepointsguy.com/news/tap-portugal-business-class-50k/");
        urls.add("https://thepointsguy.com/guide/get-max-value-venture-bonus/");
        urls.add("https://thepointsguy.com/news/singapore-mumbai-business-class-award-post-pandemic/");
        urls.add("https://thepointsguy.com/news/the-ultimate-guide-to-earning-and-redeeming-tap-milesgo/");
        urls.add("https://thepointsguy.com/news/capital-one-miles-sweet-spots/");
        urls.add("https://thepointsguy.com/news/credit-cards-getting-now-2021-travel/");
        urls.add("https://thepointsguy.com/guide/new-capital-one-transfer-partners/");
        urls.add("https://thepointsguy.com/news/capital-one-convert-cash-back-miles/");
        urls.add("https://thepointsguy.com/guide/turkish-airlines-hawaii-award/");
        urls.add("https://thepointsguy.com/guide/lie-flat-domestic-business-class-avios/");
        urls.add("https://thepointsguy.com/guide/lie-flat-domestic-business-class-avios/");
        urls.add("https://thepointsguy.com/guide/book-budget-carriers-chase-portal/");
        urls.add("https://millionmilesecrets.com/guides/secret-perk-how-to-get-200-worth-of-room-service-with-this-card/");
        urls.add("https://millionmilesecrets.com/news/wyndham-award-changes-rumor");
        urls.add("https://millionmilesecrets.com/news/american-express-will-obliterate-the-amex-open-savings-program-in-a-few-months");
        urls.add("https://millionmilesecrets.com/2017/04/20/los-angeles-hamilton-tickets");
        urls.add("https://millionmilesecrets.com/reviews/american-express-gold-worth-it/");
        urls.add("https://millionmilesecrets.com/news/increased-delta-credit-card-offers/");
        urls.add("https://millionmilesecrets.com/guides/how-to-create-a-delta-account/");
        urls.add("https://millionmilesecrets.com/guides/how-to-setup-a-marriott-account/");
        urls.add("https://millionmilesecrets.com/2017/04/06/enhance-your-hotel-stay-and-earn-3000-bonus-points/");
        urls.add("https://millionmilesecrets.com/news/apple-bonus-miles-points/");
        urls.add("https://millionmilesecrets.com/guides/amex-membership-rewards-points-at-rocketmiles");
        urls.add("https://millionmilesecrets.com/2017/05/23/4-new-amex-offers-to-help-save-on-dads-fathers-day-gift");
        urls.add("https://millionmilesecrets.com/guides/hotel-room-upgrade-best-hotel-credit-cards/");
        urls.add("https://millionmilesecrets.com/guides/chase-doordash-dashpass-benefits/");
        urls.add("https://millionmilesecrets.com/guides/flights-to-australia/");
        urls.add("https://millionmilesecrets.com/2017/02/28/europe-fare-sale-february-2017/");
        urls.add("https://millionmilesecrets.com/guides/new-to-the-miles-points-hobby-heres-a-simple-way-to-understand-the-value-of-your-travel-rewards/");
        urls.add("https://millionmilesecrets.com/guides/cash-instead-of-miles-and-points");
        urls.add("https://millionmilesecrets.com/2017/12/19/the-trick-to-making-your-airport-experience-much-more-civilized-without-spending-a-fortune");
        urls.add("https://onemileatatime.com/hotel-credit-card-free-nights/");
        urls.add("https://onemileatatime.com/gold-delta-american-express-card-review/");
        urls.add("https://onemileatatime.com/how-to-tell-priority-pass-cards-apart/");
        urls.add("https://onemileatatime.com/capital-one-venture-card/");
        urls.add("https://onemileatatime.com/escape-lounge-msp-review/");
        urls.add("https://onemileatatime.com/capital-one-venture-100k-bonus/");
        urls.add("https://onemileatatime.com/capital-one-lounges/");
        urls.add("https://onemileatatime.com/alaska-airlines-business-card-review/");
        urls.add("https://onemileatatime.com/marriott-bonvoy-business-american-express/");
        urls.add("https://onemileatatime.com/the-best-credit-cards-for-earning-amex-membership-rewards-points/");
        urls.add("https://onemileatatime.com/american-express-green-card/");
        urls.add("https://onemileatatime.com/virgin-lounge-lax-priority-pass/");
        urls.add("https://onemileatatime.com/omaat-lifemiles-promo/");
        urls.add("https://onemileatatime.com/american-express-gold-card/");
        urls.add("https://onemileatatime.com/chase-hyatt-card-anniversary-free-night/");
        urls.add("https://onemileatatime.com/review-american-express-centurion-lounge-dallas-dfw-airport/");
        urls.add("https://onemileatatime.com/how-to-approach-hotel-stays/");
        urls.add("https://onemileatatime.com/citi-aadvantage-executive-credit-card-review/");
        urls.add("https://onemileatatime.com/plastiq/");
        urls.add("https://awardwallet.com/blog/last-chance-for-increased-welcome-offers-on-delta-amex-cards/");
        urls.add("https://awardwallet.com/blog/last-chance-for-increased-welcome-offers-on-delta-amex-cards/");
        urls.add("https://awardwallet.com/blog/additional-delta-mqm-bonuses-for-amex-card-holders-in-2018/");
        urls.add("https://awardwallet.com/blog/delta-credit-card-changes/");
        urls.add("https://awardwallet.com/blog/gold-delta-skymiles-from-american-express-personal-card-review/");
        urls.add("https://awardwallet.com/blog/just-100000-point-offers/");
        urls.add("https://awardwallet.com/blog/bank-rewards-cards-from-our-partners/");
        urls.add("https://awardwallet.com/blog/apply-increased-limited-time-delta-skymiles-cards");
        urls.add("https://awardwallet.com/blog/bank-rewards-cards-from-our-partners/");
        urls.add("https://awardwallet.com/blog/expired-offer");
        urls.add("https://awardwallet.com/blog/advertiser-disclosure");
        urls.add("https://awardwallet.com/blog/alaska-american-buy-miles-bonuses-for-february-2020/");
        urls.add("https://awardwallet.com/blog/convert-citi-double-cash-rewards-to-transferrable-thankyou-points-at-1-to-1/");
        urls.add("https://awardwallet.com/blog/best-credit-card-for-flight-delay-insurance/");
        urls.add("https://awardwallet.com/blog/up-to-40-bonus-when-purchasing-alaska-miles-through-july-3-2018/");
        urls.add("https://awardwallet.com/blog/apply-for-hilton-honors-american-express-business-card/");
        urls.add("https://awardwallet.com/blog/secret-sweet-spots-aeroplan-stopovers-open-jaws/");
        urls.add("https://awardwallet.com/blog/many-rewards-points-miles-credit-card-earn/");
        urls.add("https://awardwallet.com/blog/how-to-renew-global-entry/");
        urls.add("https://awardwallet.com/blog/can-transfer-miles-frequent-flyer-programs/");
        urls.add("https://www.creditcardsexplained.com/articles/amex-gold-delta-skymiles");
        urls.add("https://creditcardsexplained.com/articles/amex-platinum-card-review");
        urls.add("https://creditcardsexplained.com/articles/best-credit-cards-customer-service");
        urls.add("https://creditcardsexplained.com/articles/best-excellent-credit");
        urls.add("https://creditcardsexplained.com/articles/best-credit-cards-help-debt");
        urls.add("https://www.creditcardsexplained.com/articles/new-hilton-honors");
        urls.add("https://creditcardsexplained.com/articles/non-frequent-flyer");

        File file = null;
        Writer myWriter = null;

        try {
            file = new File("output.csv");
            myWriter = new FileWriter(file);
            myWriter.append("class,parnum\n");
        } catch (Exception e) {
            //TODO: handle exception
        }

        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);

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
                
                //Simon
                Elements elements = document.getAllElements();
                for (Element element : elements) {
                    if (element.className() != "") {
                        //System.out.println(element.className());
                        Elements paragraphs = element.select(":root > p");
                        
                        int parnum = paragraphs.size();
                        //System.out.println(parnum);
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
}
