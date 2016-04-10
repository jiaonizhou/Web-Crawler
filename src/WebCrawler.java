import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class WebCrawler {
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
		
		Document doc = Jsoup.connect(seed).get();
		// System.out.println(doc);
		
		
	}
}