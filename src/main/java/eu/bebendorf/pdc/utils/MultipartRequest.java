package eu.bebendorf.pdc.utils;

import eu.bebendorf.pdc.http.HttpResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MultipartRequest {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection conn;
    private String charset = "utf-8";
    private OutputStream outputStream;
    private PrintWriter writer;
    public MultipartRequest(String method, String requestURL, String token) throws IOException {
        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(requestURL);
        conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=\"" + boundary + "\"");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        outputStream = conn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    public void addFilePart(String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE_FEED);
        writer.flush();
    }

    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    public HttpResponse finish() throws IOException {
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        StringBuilder result = new StringBuilder();
        int responseCode = 0;
        try {
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
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
            }catch(IOException | NullPointerException ex){}
        }
        return new HttpResponse(responseCode, result.toString());
    }

}
