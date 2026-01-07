package com.college.notification.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxClient {

    @Value("${dropbox.access-token}")
    private String accessToken;

    @Bean
    public DbxClientV2 dbxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("college-notification-app").build();
        return new DbxClientV2(config, accessToken);
    }
}
