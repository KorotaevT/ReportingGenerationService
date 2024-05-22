package cs.vsu.ReportingGenerationService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "ReportGenerationService API", version = "1.0"))
public class ReportingGenerationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportingGenerationServiceApplication.class, args);
	}

}