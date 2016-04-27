
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadSeeds {
   private ArrayList<String> seeds;
   private ArrayList<Integer> maxCrawls;
   private ArrayList<String> domains;
   private ArrayList<String> outReports;
   private ArrayList<String> outRepositorys;

   public ReadSeeds(String fileName) throws IOException {
      this.seeds = new ArrayList<String>();
      this.maxCrawls = new ArrayList<Integer>();
      this.domains = new ArrayList<String>();
      this.outReports = new ArrayList<String>();
      this.outRepositorys = new ArrayList<String>();
      readInput(fileName);
      initializeOutputFile();
   }


   private void readInput(String fileName) throws IOException {

      FileReader file = new FileReader(fileName);
      BufferedReader in = new BufferedReader(file);

      try {
         String line = in.readLine();
         while (line != null) {
            line = line.trim();
            String[] input = line.split(",");
            seeds.add(input[0]);
            maxCrawls.add(Integer.parseInt(input[1]));
            if (input.length == 3) {
               domains.add(input[2]);
            }
            line = in.readLine();
         }
         in.close();
      }catch (IOException e1) {
         System.out.printf("Error %20s%n"+ e1.toString());
      }
   }

   private void initializeOutputFile() {
      for (int i = 0; i < seeds.size(); i++) {
         String outRepository = "repository" + i;
         String outReport = "report" + i + ".html";
         this.outRepositorys.add(outRepository);
         this.outReports.add(outReport);
         File dir = new File(outRepository);
         dir.mkdir();
      }
   }


   public ArrayList<String> getSeeds()
   {
      return seeds;
   }


   public void setSeeds(ArrayList<String> seeds)
   {
      this.seeds = seeds;
   }


   public ArrayList<Integer> getMaxCrawls()
   {
      return maxCrawls;
   }


   public void setMaxCrawls(ArrayList<Integer> maxCrawls)
   {
      this.maxCrawls = maxCrawls;
   }


   public ArrayList<String> getDomains()
   {
      return domains;
   }


   public void setDomains(ArrayList<String> domains)
   {
      this.domains = domains;
   }


   public ArrayList<String> getOutReports()
   {
      return outReports;
   }


   public void setOutReports(ArrayList<String> outReports)
   {
      this.outReports = outReports;
   }


   public ArrayList<String> getOutRepositorys()
   {
      return outRepositorys;
   }


   public void setOutRepositorys(ArrayList<String> outRepositorys)
   {
      this.outRepositorys = outRepositorys;
   }

}
