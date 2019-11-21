package Java.Get52PojieFiles.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Main {

	    
	public static HashMap<String, String> filemap = new HashMap<>();
	
	
	public static void main(String[] args) throws InterruptedException {
		String result = sendGet("https://down.52pojie.cn/list.js");
		result = result.replace("__jsonpCallbackDown52PojieCn(", "");
		result = result.replace(");", "");
		JsonElement Json =  JsonParser.parseString(result);
		iteration_file_path(Json, "","");
		System.out.println("下载文件将存放在本文件/download目录下，目前有" + filemap.size() + "个文件需要下载");
		downFileByList(filemap);
	}

		/**
		 * 解析JSON
		 * @param path
		 * @param parentpath
		 * @param encodepath
		 * @throws InterruptedException
		 */
	public static void iteration_file_path(JsonElement path, String parentpath,String encodepath) throws InterruptedException {
		try {
			if (path.isJsonArray()) {
				for (int i = 0; i < path.getAsJsonArray().size(); i++) {
					iteration_file_path(path.getAsJsonArray().get(i), 
							parentpath  + "/" +path.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString(),
							encodepath + "/"+URLEncoder.encode(path.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString(),"utf-8"));
				}
			} else {
				if (path.getAsJsonObject().has("children")) {
					iteration_file_path(path.getAsJsonObject().get("children").getAsJsonArray(), 
							parentpath,
							encodepath);
				} else {
					filemap.put(parentpath, encodepath);
				}
			}
		} catch (Exception e) {
			System.err.println("解析链接时发生错误，请退出重试！"+e.getMessage());
			Thread.sleep(5000);
			System.exit(0);
		}
		return;
	}
		
	/**
	 * 下载文件到本地
	 * @param map
	 */
	public static void downFileByList(HashMap<String, String> map) {
			Iterator<Entry<String, String>> iter=map.entrySet().iterator();
				while (iter.hasNext()) {
					try {
						Entry<String,String> entry = iter.next();
						String downurl = "https://down.52pojie.cn" + strencode(entry.getValue());
						String filepath = System.getProperty("user.dir") + "\\download" + entry.getKey().replace("/", "\\");
						FileUtils.copyURLToFile(new URL(downurl), new File(filepath));
						System.out.println(entry.getKey() + "--100%");
						iter.remove();
					} catch (Exception e) {
						System.out.println("下载时发生错误:"+e.getMessage());
						continue;
					}
				}

		if (map.size() != 0) {
			while (true) {
				System.out.println("有" + map.size() + "个文件下载失败是否重试?(Y/N)");
				Scanner s = new Scanner(System.in);
				String inp = s.next();
				if (inp.equals("Y") | inp.equals("y")) {
					while (iter.hasNext()) {
						Entry<String,String> entry = iter.next();
						System.out.println(entry.getValue());
				}
					downFileByList(map);
				} else if (inp.equals("N") | inp.equals("n")) {
					System.exit(0);
				} else {
					continue;
				}
			}
		}
		System.out.println("全部内容已经下载成功");
	}
	
	/**
	 * URL转义
	 * @param str
	 * @return
	 */
	public static String strencode(String str) {
		str = str.replace(" ", "%20").replace("+", "%20")
				.replace("?", "%3F").replace("#", "%23")
				.replace("&", "%26").replace("=", "%3D");
		return str;
	}
	
	/**
	 * 发送GET请求
	 * @param str
	 * @return
	 */
    public static String sendGet(String str) {
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
