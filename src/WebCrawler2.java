package webCrawler.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;


public class WebCrawler2 implements Runnable{
   private Queue<String> frontier = new LinkedList<String>();
   private ArrayList<String> visited;
   private String seed; 
   private int max_crawl;
   private String domain;
   private String outputFile;
   private String outRepository;

   private HashMap<String, Integer> hpCount = new HashMap<String, Integer>();
   private HashMap<String, String> hpTitle = new HashMap<String, String>();
   private HashMap<String, Integer> hpImage = new HashMap<String, Integer>();
   private HashMap<String, Integer> hpLink = new HashMap<String, Integer>();
   private HashMap<String, Integer> hpResponse = new HashMap<String, Integer>();
   private String UserAgent = 
         "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
   private int count;

   public WebCrawler2(ArrayList<String> visited, String seed, int max_crawl, String domain, String outputFile, String outRepository) {
      this.visited = visited;
      this.seed = seed;
      this.max_crawl = max_crawl;
      this.domain = domain;
      this.count = 0;
      this.outputFile = outputFile;
      this.outRepository = outRepository;
   }

   public boolean isCrawlAllowed(String urlStr) {
      if (urlStr == null || urlStr.equals("")) {
         return false;
      }
      try {
         URL url = new URL(urlStr);

         String hostname = url.getHost();
         String robotUrlStr = url.getProtocol() + "://" + hostname + "/robots.txt";
         URL robotUrl = new URL(robotUrlStr);
         HttpURLConnection conn = (HttpURLConnection)robotUrl.openConnection();
         conn.setRequestMethod("GET");
         conn.setDoOutput(true);
         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String robotsTxt = "";
         String line = null;
         while ((line = reader.readLine()) != null) {
            robotsTxt += line + "\r\n";
         }

         SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
         BaseRobotRules rules = parser.parseContent(hostname, robotsTxt.getBytes("UTF-8"),
               "text/plain", UserAgent);
         return rules.isAllowed(urlStr);

      } catch (IllegalArgumentException e) {
         e.printStackTrace();
         return true;
      } catch (MalformedURLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return true;
      } catch (ProtocolException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return true;
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return true;
      } catch (ClassCastException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return true;
      }
   }

   public static String canonicalURL(String urlStr) {
      try {
         URL url = new URL(urlStr);
         return url.getProtocol() + "://" + url.getHost() + url.getPath();
      } catch (MalformedURLException e) {
         // TODO Auto-generated catch block
         return "";
      }
   }

   public synchronized void findURL(final String seed, final int max_crawl, final String domain) throws IOException {
      frontier.add(seed);
      while (count < max_crawl && !frontier.isEmpty()) {
         try {
            String curr = frontier.poll();

            if (visited.contains(curr)) {
               continue;
            }

            if (!isCrawlAllowed(curr)) {
               continue;
            }

            // get response code
            Connection.Response response = Jsoup.connect(curr).userAgent(UserAgent).timeout(10000).ignoreHttpErrors(true).execute();
            int responseCode = response.statusCode();
            hpResponse.put(curr, responseCode);
            count++;
            hpCount.put(curr, count);
            visited.add(curr);
            System.out.println(curr + " " + count); 

            if (responseCode == 200) {

               // connect current, extract title, increase count, put current into visited
               Document doc = Jsoup.connect(curr).userAgent(UserAgent).timeout(10000).get();
               String title = doc.title();
               hpTitle.put(curr, title);

               // save HTML to repository folder
               String filename;
               FileWriter out;
               BufferedWriter bw;
               filename = this.outRepository + "/" + count + ".html";
               out = new FileWriter(filename);
               bw = new BufferedWriter(out);
               bw.write(doc.toString());
               bw.close();

               // get image count
               Elements images = doc.getElementsByTag("img");
               int imgCount = images.size();
               hpImage.put(curr, imgCount);

               // get link count
               Elements links = doc.select("a[href]");
               int linkCount = links.size();
               hpLink.put(curr, linkCount);

               // save crawled links to frontier
               List<String> urls = new ArrayList<String>();
               for (Element link: links) {
                  String absHref = canonicalURL(link.attr("abs:href"));
                  if (domain != null) {
                     if(link.attr("abs:href").contains(domain)) {
                        if (!visited.contains(absHref)) {
                           urls.add(absHref);
                        }
                     }
                  } else {
                     if (!visited.contains(absHref)) {
                        urls.add(absHref);
                     }
                  }
               }

               // de-dup
               Set<String> urlSet = new HashSet<String>();
               urlSet.addAll(urls);

               for (String link : urlSet) {
                  frontier.add(link);
               }
            }
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
         } catch (NullPointerException e) {
            e.printStackTrace();
         } catch (HttpStatusException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public void crawlerProcessor() throws IOException {

      // run findURL
      findURL(this.seed, this.max_crawl, this.domain);

      // output to reporti.html
      FileWriter out = new FileWriter(this.outputFile);
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
      bw.write("\t\t\t\t<th>title</th>\n");
      bw.write("\t\t\t\t<th>filepath</th>\n");
      bw.write("\t\t\t\t<th>HTTP status code</th>\n");
      bw.write("\t\t\t\t<th>number of outlinks</th>\n");
      bw.write("\t\t\t\t<th>number of images</th>\n");
      bw.write("\t\t\t</tr>\n");
      for (String url: visited) {
         if (hpTitle.get(url) == null) {
            continue;
         }
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
      try {
         crawlerProcessor();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }
}

