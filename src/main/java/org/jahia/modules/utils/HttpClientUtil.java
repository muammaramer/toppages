package org.jahia.modules.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class HttpClientUtil {
    Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private String errorMessage = "";
    private CloseableHttpClient httpClient;

    public HttpClientUtil() {
        this.httpClient = HttpClients.createDefault();
    }

    public String getHtmlPage(URIBuilder uriBuilder) {
        String result = null;
        try {

            HttpGet request = new HttpGet(uriBuilder.build());
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                this.errorMessage = "Error while connecting to url, please check if the url is correct";
                logger.error("EError while connecting to url: {}, please check if the url is correct, HTTP Status {} ", uriBuilder.toString(), response.getStatusLine());
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            response.close();
        } catch (IOException | URISyntaxException e) {
            this.errorMessage = "Error while connecting to url";
            logger.error("Error while connecting to URL: {}", uriBuilder.toString(), e);
        }

        return result;
    }

    public void closeConnection() throws IOException {
        this.httpClient.close();
    }

    /**
     * A method to test if awStats url is reachable
     * @param uriBuilder
     * @return true if response code is 200
     */
    public boolean testConnection(URIBuilder uriBuilder) {
        try {
            HttpGet request = new HttpGet(uriBuilder.build());
            CloseableHttpResponse response = httpClient.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 200) {
                return true;
            } else {
                this.errorMessage += "Unable to connect to:" + uriBuilder.getHost() + uriBuilder.getPath() + ". Error code received: " + responseCode;
                logger.error(this.errorMessage);
            }
            response.close();
        } catch (IOException | URISyntaxException e) {
            this.errorMessage = "Error while connecting to awStats url";
            logger.error("Error while connecting to awStats URL {} ", uriBuilder.toString(), e);
        }
        return false;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

}
