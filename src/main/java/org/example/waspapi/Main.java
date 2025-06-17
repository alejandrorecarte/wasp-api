package org.example.waspapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@OpenAPIDefinition(
        info = @Info(
                title = "WaspAPI",
                version = "1.0.3",
                description = "APIs Swagger WaspAPI",
                contact = @Contact(name = "Alejandro Recarte", email = "alejandro.recarte.rebollo@gmail.com"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Ambiente local")
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")  // nombre del esquema que declares en configuración
        }
)
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
