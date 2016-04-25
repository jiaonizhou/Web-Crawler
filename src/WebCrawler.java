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


public class WebCrawler {
	public static Queue<String> frontier = new LinkedList<String>();
	public static ArrayList<String> visited = new ArrayList<String>();
	public static HashMap<String, Integer> hpCount = new HashMap<String, Integer>();
	public static HashMap<String, String> hpTitle = new HashMap<String, String>();
	public static HashMap<String, Integer> hpImage = new HashMap<String, Integer>();
	public static HashMap<String, Integer> hpLink = new HashMap<String, Integer>();
	public static HashMap<String, Integer> hpResponse = new HashMap<String, Integer>();
	public static String UserAgent = 
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	public static int count = 0;
	
	public static boolean isCrawlAllowed(String urlStr) {
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
			return false;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
	}
	
	public static String canonicalUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			return url.getProtocol() + "://" + url.getHost() + url.getPath();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return "";
		}
	}
	
	public static void findURL(String seed, int max, String domain) throws IOException {
		frontier.add(seed);
		while (visited.size() < max && !frontier.isEmpty()) {
			try {
				String curr = frontier.poll();
				
				if (!isCrawlAllowed(curr)) {
					continue;
				}
				
				// get response code
				Connection.Response response = Jsoup.connect(curr).userAgent(UserAgent).timeout(10000).execute();
				int responseCode = response.statusCode();
				hpResponse.put(curr, responseCode);
				
				if (responseCode == 200) {
					// connect current, extract title, increase count, put current into visited
					Document doc = Jsoup.connect(curr).userAgent(UserAgent).timeout(10000).get();
					String title = doc.title();
					count ++;
					visited.add(curr);
					hpCount.put(curr, count);
					hpTitle.put(curr, title);
					
					// save HTML to repository folder
					String filename;
					FileWriter out;
					BufferedWriter bw;
					filename = "/Users/hanzili/Desktop/repository/" + count + ".html";
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
						String absHref = canonicalUrl(link.attr("abs:href"));
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
			}
			
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (NullPointerException e) {
		        e.printStackTrace();
		    }
			catch (HttpStatusException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) throws IOException {
		// read in seed, max, domain
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
		
		// create repository directory
		File dir = new File("/Users/hanzili/Desktop/repository");
		dir.mkdir();
		
		// run findURL
		findURL(seed, max, domain);
		
		// output to report.html
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
		for (String url: visited) {
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
}
   
