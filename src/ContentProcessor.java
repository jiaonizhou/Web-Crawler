package webCrawler.src;

/*
 * Noise Removal
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class ContentProcessor {

   String finalText; // main content after noise removal
   int tokenPosition; // keep track of token position when doing tokenizing
   ArrayList<Token> tokens;
   // (key, value) : (position of token, number of tags below the position)
   HashMap<Integer, Integer> tagCountLookUp = new HashMap<>(); 
   int optStart; // when doing optimizing, the start token position
   int optEnd; // when doing optimizing, the end token position

   public ContentProcessor(String fileName) throws IOException {
      this.finalText = "";
      this.tokenPosition = 0;
      this.tokens = new ArrayList<>();
      this.optStart = 0;
      this.optEnd = 0;
      
      readInput(fileName); //read input file from the repository
      setTagCountLookUp();
      findOptimum();
      generateFinalText();
      //tokensToString();
   }
   

   public void readInput(String fileName) throws IOException {
      FileReader fileReader = new FileReader(fileName);
      String content = "";     
      int i ;
      while ((i = fileReader.read()) != -1) {
         char ch = (char)i;
         content += ch; 
      }

      //clean this HTML to avoid cross-site scripting (XSS)
      String safeContent1 = Jsoup.clean(content, Whitelist.basic());      
      String safeContent2 = safeContent1.replaceAll("&nbsp", "");
      String safeContent = safeContent2.replaceAll("&amp", "");
     
      i = 0;
      int textStartChar = i;
      int textEndChar = i;
      int tagStartChar = i;
      int tagEndChar = i;

      for (; i < safeContent.length() - 1; i++) {
         char c = safeContent.charAt(i);

         if (c == '<') {

            if (textStartChar != -1) {
               textEndChar = i;
               String textString = safeContent.substring(textStartChar, textEndChar);
               if (!textString.equals("\\s")) {
                  countText(textString);
               }
            }

            tagStartChar = i;
         } 

         if (c == '>') {
            tagEndChar = i + 1;
            String tagString = safeContent.substring(tagStartChar, tagEndChar);
            countTag(tagString);

            char c2 = safeContent.charAt(i + 1);
            if (c2 != '<') {
               textStartChar = i + 1;
            } else {
               textStartChar = -1;
            }
         }
      }

      // the last tag in the whole document
      countTag(safeContent.substring(tagStartChar));

   }


   private void countText(String str) {
      String[] fields = str.split("\\s");
      for (int i = 0; i < fields.length; i++) {
         String s = fields[i];
         String ss = "";
         
         if (s.startsWith("&") && s.length() == 4) {
            continue;
         }

         if ((ss.length() == 0) && !s.isEmpty() && !s.equals("\\s+")) {
            Token newToken = new Token(s, this.tokenPosition, 0);
            this.tokenPosition++;
            this.tokens.add(newToken);
         } 
         
      }
   }


   private void countTag(String str) {
      Token newToken = new Token(str, this.tokenPosition, 1);
      this.tokenPosition++;
      this.tokens.add(newToken);
   }


   private void setTagCountLookUp() {
      int num = 0;
      for (int i = 0; i < this.tokens.size(); i++) {
         this.tagCountLookUp.put(i, num);
         if (this.tokens.get(i).getMarker() == 1) {
            num++;
         }
      }
   }

   
   private void findOptimum() {
      int max = 0;
      int size = this.tokens.size();

      for (int i = 0; i < size - 1; i++) {
         if (this.tokens.get(i).getMarker() == 1) {
            continue;
         }

         for (int j = i + 1; j < size; j++) {
            if (this.tokens.get(j).getMarker() == 1) {
               continue;
            }

            if (max < objNum(i, j)) {
               //System.out.println(max);
               max = objNum(i, j);
               this.optStart = i;
               this.optEnd = j;
            }
         }
      }
   }


   private int objNum(int i, int j) {
      int tagNumBelowi = this.tagCountLookUp.get(i);
      int tagNumAbovej = this.tokens.size() - this.tagCountLookUp.get(j) - 1;

      int textNumBelowi = i - tagNumBelowi;
      int textNumBelowj = j - this.tagCountLookUp.get(j);
      int textBetweenij = textNumBelowj - textNumBelowi;

      if (this.tokens.get(j).getMarker() == 0) {
         textBetweenij++;
      }

      return (tagNumBelowi + tagNumAbovej + textBetweenij);
   }


   private void generateFinalText() {
      System.out.println("Main Content, Word Token Start at Token #" + this.optStart
            + "; Word Token End at Token #" + this.optEnd);

      for (int i = this.optStart; i <= this.optEnd; i++) {
         if (this.tokens.get(i).getMarker() == 0) {
            this.finalText += this.tokens.get(i).getTokenString() + " ";
         }
      }
      System.out.println("After Content Processing, Main Content is Retrieved "
            + "as the following: ");
      System.out.println(finalText);
      System.out.println();
   }

   
   private void tokensToString() {
      for (Token i : this.tokens) {
         System.out.println(i.getPosition() + ": " + i.getTokenString() + "; " 
      + i.getMarker());
      }
   }
   
   
   public String getFinalText() {
      return finalText;
   }

   public void setFinalText(String finalText) {
      this.finalText = finalText;
   }

   public int getTokenPosition() {
      return tokenPosition;
   }

   public void setTokenPosition(int tokenPosition) {
      this.tokenPosition = tokenPosition;
   }

   public ArrayList<Token> getTokens() {
      return tokens;
   }

   public void setTokens(ArrayList<Token> tokens) {
      this.tokens = tokens;
   }

   public HashMap<Integer, Integer> getTagCountLookUp() {
      return tagCountLookUp;
   }

   public void setTagCountLookUp(HashMap<Integer, Integer> tagCountLookUp) {
      this.tagCountLookUp = tagCountLookUp;
   }

   public int getOptStart() {
      return optStart;
   }

   public void setOptStart(int optStart) {
      this.optStart = optStart;
   }

   public int getOptEnd() {
      return optEnd;
   }

   public void setOptEnd(int optEnd) {
      this.optEnd = optEnd;
   }
   
   
}
