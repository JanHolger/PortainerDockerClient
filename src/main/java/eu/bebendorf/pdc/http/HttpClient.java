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
        return requestRaw(method, url, makeBody(body)).getBodyOrError(responseType);
    }

    private byte[] makeBody(Object body){
        if(body == null)
            return null;
        if(body instanceof byte[])
            return (byte[]) body;
        if(body instanceof String)
            return ((String) body).getBytes(StandardCharsets.UTF_8);
        return makeBody(GSON.toJson(body));
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

    public HttpResponse requestRawString(String method, String url, String body){
        return requestRaw(method, url, body.getBytes(StandardCharsets.UTF_8));
    }

    public HttpResponse requestRaw(String method, String url, byte[] body){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
                os.write(body);
                os.flush();
                os.close();
            }
            responseCode = conn.getResponseCode();
            if(responseCode>299){
                baos.write(readAll(conn.getErrorStream()));
            }else{
                baos.write(readAll(conn.getInputStream()));
            }
        }catch(Exception e){
            try {
                responseCode = conn.getResponseCode();
                return new HttpResponse(responseCode, readAll(conn.getErrorStream()));
            }catch(IOException | NullPointerException ex){}
            return new HttpResponse(responseCode, baos.toByteArray());
        }
        return new HttpResponse(responseCode, baos.toByteArray());
    }

    private static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int r = 0;
        while (r != -1){
            r = is.read(data);
            if(r != -1)
                baos.write(data, 0, r);
        }
        is.close();
        return baos.toByteArray();
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
