import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebCrawler {
	
	public static ArrayList<String> findURL(String seed, String domain) throws IOException {
		Document doc = Jsoup.connect(seed).get();
		// System.out.println(doc);
		Elements links = doc.select("a[href]");
		ArrayList<String> frontier = new ArrayList<String>();
		
		for (Element link: links) {
			String absHref = link.attr("abs:href");
			if (domain != null) {
				if(link.attr("abs:href").contains(domain)) {
					frontier.add(absHref);
				}
	    	} else {
	    		frontier.add(absHref);
	    	}
	    }
		return frontier;
	}
	
	public static void main(String args[]) throws IOException {
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
		// System.out.println(seed);
		// System.out.println(max);
		// System.out.println(domain); 
		ArrayList<String> frontier = findURL(seed, domain);
		int size = frontier.size();
		FileWriter fw = new FileWriter("report.html");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("<!DOCTYPE html>\n");
		bw.write("\t<head>\n");
		bw.write("\t\t<title>Web Crawler Report</title>\n");
		bw.write("\t\t<meta charset='utf-8' />\n");
		bw.write("\t</head>\n");
		bw.write("\t<body style='text-align:center; font-family:Sans-serif;'>\n");
		bw.write("\t\t<h1>URL</h1>\n");
		for (String url: frontier) {
			bw.write("\t\t<p>" + url + "</p>\n");
		}
		bw.write("\t</body>\n");
		bw.write("<html>\n");
		bw.close();
		
		
	}
	
	

}

	
	    
	    
	    
	    
	    
	    
	    
