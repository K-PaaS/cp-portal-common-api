package org.container.platform.common.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;


/**
 * Swagger Config 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.08.24
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("K-PaaS Container Platform Common API Docs")
                .version("v1.0")
                .description("This is a API Document created with swagger.")
                .license(getLicense());
    }

    private License getLicense() {
        License license = new License();
        license.setName("Apache 2.0");
        license.setIdentifier("Apache-2.0");
        return license;
    }
}