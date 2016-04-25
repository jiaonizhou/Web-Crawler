package webCrawler.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebCrawlerMulti implements Runnable{
   public ArrayList<String> frontier;
   public HashMap<String, Integer> hpCount;
   public HashMap<String, String> hpTitle;
   public HashMap<String, Integer> hpImage;
   public HashMap<String, Integer> hpLink;
   public HashMap<String, Integer> hpResponse;
   public int count;
   
   public WebCrawlerMulti() throws IOException {
      this.frontier = new ArrayList<String>();
      this.hpCount = new HashMap<String, Integer>();
      this.hpTitle = new HashMap<String, String>();
      this.hpImage = new HashMap<String, Integer>();
      this.hpLink = new HashMap<String, Integer>();
      this.hpResponse = new HashMap<String, Integer>();
      this.count = 0;
   }
   
   public synchronized void findURL(String seed, int max, String domain) throws IOException {
      try {
         Document doc = Jsoup.connect(seed).get();
         String title = doc.title();
         count ++;
         frontier.add(seed);
         hpCount.put(seed, count);
         hpTitle.put(seed, title);
               
         URL url;
         BufferedReader in;
         String inputLine;
         String filename;
         FileWriter out;
         BufferedWriter bw;
         filename = "/Users/hanzili/Desktop/repository/" + count + ".html";
         out = new FileWriter(filename);
         bw = new BufferedWriter(out);
         url = new URL(seed);
         in = new BufferedReader(new InputStreamReader(url.openStream()));
         while ((inputLine = in.readLine()) != null) {
            bw.write(inputLine);
         }
         bw.close();
         in.close();
         
         Connection.Response response = Jsoup.connect(seed).execute();
         int responseCode = response.statusCode();
         hpResponse.put(seed, responseCode);
         
         Elements images = doc.getElementsByTag("img");
         int imgCount = images.size();
         hpImage.put(seed, imgCount);
         
         Elements links = doc.select("a[href]");
         int linkCount = links.size();
         hpLink.put(seed, linkCount);
         
         for (Element link: links) {
            if (frontier.size() >= max) {
               return;
            }
            String absHref = link.attr("abs:href");
            if (domain != null) {
               if(link.attr("abs:href").contains(domain)) {
                  if (!frontier.contains(absHref)) {
                     findURL(absHref, max, domain);
                  }
               }
            } else {
               if (!frontier.contains(absHref)) {
                  findURL(absHref, max, domain);
               }
            }
          }
      } catch (IOException e) {
      }
   }
   
   public void crawlerProcessor() throws IOException {
      FileReader in = new FileReader("/Users/hanzili/Desktop/specification.csv");
      BufferedReader br = new BufferedReader(in);
      String line = null;
      String seed = null;
      String domain = null;
      int max = 0;
      if ((line = br.readLine()) != null) {
         String[] input = line.split(",");
         seed = input[0];
         max = Integer.parseInt(input[1]);
         if (input.length == 3) {
            domain = input[2];
         }
      }
      in.close();
      
      File dir = new File("/Users/hanzili/Desktop/repository");
      dir.mkdir();

      findURL(seed, max, domain);
      
      FileWriter out = new FileWriter("/Users/hanzili/Desktop/report.html");
      BufferedWriter bw = new BufferedWriter(out);
      bw.write("<!DOCTYPE html>\n");
      bw.write("\t<head>\n");
      bw.write("\t\t<title>Web Crawler Report</title>\n");
      bw.write("\t\t<meta charset='utf-8' />\n");
      bw.write("\t</head>\n");
      bw.write("\t<body style='text-align:left; font-family:Sans-serif;'>\n");
      bw.write("\t\t<h1>Web Crawler Report</h1>\n");
      bw.write("\t\t<table border='1'>\n");
      bw.write("\t\t\t<tr>\n");
      bw.write("\t\t\t\t<th>url</th>\n");
      bw.write("\t\t\t\t<th>filepath</th>\n");
      bw.write("\t\t\t\t<th>HTTP status code</th>\n");
      bw.write("\t\t\t\t<th>number of outlinks</th>\n");
      bw.write("\t\t\t\t<th>number of images</th>\n");
      bw.write("\t\t\t</tr>\n");
      for (String url: frontier) {
         bw.write("\t\t\t<tr>\n");
         bw.write("\t\t\t\t<td><a href='" + url + "' target='_blank'>" + hpTitle.get(url) + "</a></td>\n");
         bw.write("\t\t\t\t<td><a href='repository/" + hpCount.get(url) + ".html' download>" + hpCount.get(url) + ".html</a></td>\n");
         bw.write("\t\t\t\t<td>" + hpResponse.get(url) + "</td>\n");
         bw.write("\t\t\t\t<td>" + hpLink.get(url) + "</td>\n");
         bw.write("\t\t\t\t<td>" + hpImage.get(url) + "</td>\n");
         bw.write("\t\t\t</tr>\n");
      }  
      bw.write("\t\t</table>\n");
      bw.write("\t</body>\n");
      bw.write("<html>\n"); 
      bw.close();
   }

   @Override
   public void run()
   {
      try
      {
         crawlerProcessor();
      } catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }

}
   
