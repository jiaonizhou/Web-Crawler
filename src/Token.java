package webCrawler.src;

public class Token {
   private String tokenString;
   private int position;
   private int marker; // 0: indicates word; 1: indicates tag 
   
   public Token (String token, int position, int marker) {
      this.tokenString = token;
      this.position = position;
      this.marker = marker;      
   }

   public String getTokenString() {
      return tokenString;
   }

   public void setTokenString(String tokenString) {
      this.tokenString = tokenString;
   }

   public int getPosition() {
      return position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public int getMarker() {
      return marker;
   }

   public void setMarker(int marker) {
      this.marker = marker;
   }
     
}
