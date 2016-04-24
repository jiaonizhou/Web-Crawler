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
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


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
	
	public static void findURL(String seed, int max, String domain) throws IOException {
		frontier.add(seed);
		while (visited.size() < max && !frontier.isEmpty()) {
			try {
				String curr = frontier.poll();
				System.out.println(curr);
				
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
					URL url;
					BufferedReader in;
					String inputLine;
					String filename;
					FileWriter out;
					BufferedWriter bw;
					filename = "repository/" + count + ".html";
					out = new FileWriter(filename);
					bw = new BufferedWriter(out);
					url = new URL(curr);
					in = new BufferedReader(new InputStreamReader(url.openStream()));
					while ((inputLine = in.readLine()) != null) {
						bw.write(inputLine);
					}
					bw.close();
					in.close();
					
					// get image count
					Elements images = doc.getElementsByTag("img");
					int imgCount = images.size();
					hpImage.put(curr, imgCount);
					
					// get link count
					Elements links = doc.select("a[href]");
					int linkCount = links.size();
					hpLink.put(curr, linkCount);
					
					// save crawled links to frontier
					for (Element link: links) {
						String absHref = link.attr("abs:href");
						if (domain != null) {
							if(link.attr("abs:href").contains(domain)) {
								if (!visited.contains(absHref)) {
									frontier.add(absHref);
								}
							}
				    	} else {
				    		if (!visited.contains(absHref)) {
				    			frontier.add(absHref);
				    		}
				    	}
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
		FileReader in = new FileReader("specification.csv");
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
		File dir = new File("repository");
		dir.mkdir();
		
		// run findURL
		findURL(seed, max, domain);
		
		// output to report.html
		FileWriter out = new FileWriter("report.html");
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
   
