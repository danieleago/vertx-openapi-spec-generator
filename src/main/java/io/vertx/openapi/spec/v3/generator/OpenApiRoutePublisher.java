package io.vertx.openapi.spec.v3.generator;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author ckaratza
 * Exposes the OpenAPI spec as a vertx route.
 */
public final class OpenApiRoutePublisher {

    private final static Map<String, OpenAPI> generatedSpecs = new HashMap<>();

    public synchronized static void publishOpenApiSpec(Router router, String path, String title, String version, String serverUrl) {
        Optional<OpenAPI> spec = Optional.empty();
        if (generatedSpecs.get(path) == null) {
            OpenAPI openAPI = OpenApiSpecGenerator.generateOpenApiSpecFromRouter(router, title, version, serverUrl);
            generatedSpecs.put(path, openAPI);
            spec = Optional.of(openAPI);
        }
        if (spec.isPresent()) {
            Optional<OpenAPI> finalSpec = spec;
            router.get(path + ".json").handler(routingContext ->
                    routingContext.response()
                            .putHeader("Content-Type", "application/json")
                            .end(Json.pretty(finalSpec.get())));
            router.get(path + ".yaml").handler(routingContext ->
                    routingContext.response()
                            .putHeader("Content-Type", "text/plain")
                            .end(Yaml.pretty(finalSpec.get())));
        }
    }
}
