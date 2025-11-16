package com.hospedaya.backend.infraestructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Contribuye información básica de la aplicación al endpoint /actuator/info.
 */
@Component
public class AppInfoContributor implements InfoContributor {

    @Value("${info.app.name:HospedaYa Backend}")
    private String appName;

    @Value("${info.app.description:API REST de HospedaYa}")
    private String appDescription;

    @Value("${info.app.version:unknown}")
    private String appVersion;

    @Value("${info.app.environment:unknown}")
    private String appEnvironment;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> app = new HashMap<>();
        app.put("name", appName);
        app.put("description", appDescription);
        app.put("version", appVersion);
        app.put("environment", appEnvironment);

        builder.withDetail("app", app);
    }
}
