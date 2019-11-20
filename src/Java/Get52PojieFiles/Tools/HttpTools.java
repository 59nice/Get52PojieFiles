package Java.Get52PojieFiles.Tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public  class HttpTools {
	 /**
     * Get方法
     */
    public String sendGet(String str) {
        try {
            URL url = new URL(str);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true); 
            connection.setRequestMethod("GET"); 
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) { // 读取数据
                result.append(line + "\n");
            }
            connection.disconnect();
            //System.out.println(result.toString());
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
   
}
