package eu.bebendorf.pdc.http;

import com.google.gson.Gson;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.utils.MultipartRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClient {
    @Getter
    private String address;
    @Getter @Setter
    private String token = null;

    private static Gson GSON = new Gson();

    public HttpClient(String url){
        this.address = url;
    }

    public <T> T request(String method, String url, Class<T> responseType) throws RequestException {
        return request(method, url, null, responseType);
    }

    public <T> T request(String method, String url, Object body, Class<T> responseType) throws RequestException {
        return requestRaw(method, url, body==null?null:GSON.toJson(body)).getBodyOrError(responseType);
    }

    public HttpResponse requestRawMultipart(String method, String url, Map<String, Object> multipart){
        try {
            MultipartRequest request = new MultipartRequest(method, address + url, token);
            for(String key : multipart.keySet()){
                if(multipart.get(key) instanceof File){
                    request.addFilePart(key, (File) multipart.get(key));
                }else{
                    request.addFormField(key, multipart.get(key).toString());
                }
            }
            return request.finish();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new HttpResponse(0, "Unknown Connection Error");
    }

    public HttpResponse requestRaw(String method, String url, String body){
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;
        int responseCode = 0;
        try{
            URL theUrl = new URL(address + url);
            conn = (HttpURLConnection) theUrl.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            if(token != null)
                conn.setRequestProperty("Authorization", "Bearer " + token);
            if(body!=null){
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(body);
                writer.flush();
                writer.close();
                os.close();
            }
            responseCode = conn.getResponseCode();
            BufferedReader rd;
            if(responseCode>299){
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }else{
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        }catch(Exception e){
            try {
                responseCode = conn.getResponseCode();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                return new HttpResponse(responseCode, result.toString());
            }catch(IOException | NullPointerException ex){}
            return new HttpResponse(responseCode, result.toString());
        }
        return new HttpResponse(responseCode, result.toString());
    }

    public static String urlEncode(String value){
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String jsonEncode(Object object){
        return GSON.toJson(object);
    }

    public static String queryParams(Map<String, String> params){
        List<String> p = new ArrayList<>();
        for(String key : params.keySet()){
            p.add(key+"="+urlEncode(params.get(key)));
        }
        return String.join("&", p);
    }


}
