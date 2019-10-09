package org.boogh.clientservice;

import io.github.jhipster.config.JHipsterProperties;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.boogh.config.ApplicationProperties;
import org.boogh.domain.Report;
import org.boogh.domain.enumeration.ReportState;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TelegramBotService {

    private final JHipsterProperties jHipsterProperties;
    private final ApplicationProperties applicationProperties;

    public TelegramBotService(JHipsterProperties jHipsterProperties, ApplicationProperties applicationProperties) {
        this.jHipsterProperties = jHipsterProperties;
        this.applicationProperties = applicationProperties;
    }

    public void sendReportMessage(Report report, ReportState reportState, Long chatId) {
        String reportUpdateMessage = "Your report with identifier: " + report.getId() + " has been rejected\n  " +
                                    jHipsterProperties.getMail().getBaseUrl() + "/report?id=" + report.getId();
        if (reportState.equals(ReportState.APPROVED)) {
            reportUpdateMessage = "Your report with identifier: " + report.getId() + " has been approved\n " +
                                jHipsterProperties.getMail().getBaseUrl() + "/report?id=" + report.getId();
        }
        try {
            sendMessage(reportUpdateMessage, chatId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, Long chatId) throws IOException {

        final String telegramBotToken = applicationProperties.getTelegram().getBotToken();

        //create http post request to the bot with the chatId specified
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.telegram.org/bot" + telegramBotToken + "/sendMessage");
        httpPost.setHeader("chat_id", chatId.toString());
        httpPost.setHeader("text", message);
        httpPost.setHeader("disable_notification", "false");
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        try {
            HttpEntity entity = httpResponse.getEntity();
            String body = EntityUtils.toString(entity);

            if (httpResponse.getStatusLine().getStatusCode() == 200){
                String messageParam = "message";
            }

            EntityUtils.consume(entity);

        } catch (ClientProtocolException cpe) {
            System.out.println(cpe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            httpResponse.close();
        }

    }

}
