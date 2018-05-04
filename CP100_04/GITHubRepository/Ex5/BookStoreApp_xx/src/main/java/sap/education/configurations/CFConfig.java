package sap.education.configurations;

import javax.sql.DataSource;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"spring"})
public class CFConfig extends AbstractCloudConfig{
	
	@Bean
    public DataSource dataSource() {
        return connectionFactory().dataSource();
    }
}
