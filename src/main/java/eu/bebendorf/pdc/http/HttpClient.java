package eu.bebendorf.pdc.http;

import com.google.gson.Gson;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.utils.MultipartRequest;
import eu.bebendorf.pdc.utils.WebSocket;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            if(responseCode>299){
                result.append(readAll(conn.getErrorStream(), conn.getHeaderFieldInt("Content-Length", -1)));
            }else{
                result.append(readAll(conn.getInputStream(), conn.getHeaderFieldInt("Content-Length", -1)));
            }
        }catch(Exception e){
            try {
                responseCode = conn.getResponseCode();
                return new HttpResponse(responseCode, readAll(conn.getErrorStream(), conn.getHeaderFieldInt("Content-Length", -1)));
            }catch(IOException | NullPointerException ex){}
            return new HttpResponse(responseCode, result.toString());
        }
        return new HttpResponse(responseCode, result.toString());
    }

    private static String readAll(InputStream is, int len) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int r;
        while (is.available() > 0 || len > 0){
            r = is.read(data);
            baos.write(data, 0, r);
            len -= r;
        }
        is.close();
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public WebSocket webSocket(String url){
        return new WebSocket(address.replace("http", "ws") + url);
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
