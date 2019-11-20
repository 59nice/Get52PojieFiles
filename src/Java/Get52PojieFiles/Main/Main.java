package Java.Get52PojieFiles.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import Java.Get52PojieFiles.Tools.HttpTools;

public class Main {

	    
		public static HashMap<String, String> filemap = new HashMap<>();
	
	
	public static void main(String[] args) throws InterruptedException {
		HttpTools httpsend = new HttpTools();
		String result = httpsend.sendGet("https://down.52pojie.cn/list.js");
		result = result.replace("__jsonpCallbackDown52PojieCn(", "");
		result = result.replace(");", "");
		JsonElement Json = (JsonElement) new JsonParser().parse(result);
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
	 * @param list
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

}
