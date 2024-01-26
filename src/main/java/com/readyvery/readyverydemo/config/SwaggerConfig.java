package com.readyvery.readyverydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@OpenAPIDefinition(
	info = @Info(
		title = "ReadyVery 서비스 API 명세서",
		description = "☕ [점주사이드] 간편 선결제 테이크아웃 서비스, 레디베리 \uD83E\uDD64",
		version = "v1"
	)
)
@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI customOpenApi() {

		// OpenAPI openAPI = new OpenAPI()
		// 	.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
		// 	.components(new Components()
		// 		.addSecuritySchemes("bearerAuth", new SecurityScheme()
		// 			.type(SecurityScheme.Type.HTTP)
		// 			.scheme("bearer")
		// 			.bearerFormat("JWT")
		// 			.in(SecurityScheme.In.HEADER)
		// 			.name("Authorization")));
		//
		// // Define the login API path
		// openAPI.path("/api/v1/user/login", new PathItem()
		// 	.post(new Operation()
		// 			.operationId("loginUser")
		// 			.summary("Login user")
		// 			.description("Login with email and password")
		// 			.requestBody(new RequestBody()
		// 				.description("User credentials")
		// 				.content(new Content()
		// 					.addMediaType("application/json", new MediaType()
		// 						.schema(new Schema<>()
		// 							.type("object")
		// 							.addProperty("email", new Schema<>().type("string"))
		// 							.addProperty("password", new Schema<>().type("string")))))
		// 				.required(true))
		// 		// You can add responses and other attributes as needed
		// 	);
		//
		// return openAPI;

		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
			.components(new Components()
				.addSecuritySchemes("bearerAuth", new SecurityScheme()
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.in(SecurityScheme.In.HEADER)
					.name("Authorization")))

			.path("/api/v1/user/login", new PathItem()
				.post(new Operation()
						.operationId("loginUser")
						.summary("자체 로그인")
						.description("자체 로그인 기능")
						.requestBody(new RequestBody()
							.description("User credentials")
							.content(new Content()
								.addMediaType("application/json", new MediaType()
									.schema(new Schema<>()
										.type("object")
										.addProperty("email", new Schema<>().type("string").example("test"))
										.addProperty("password", new Schema<>().type("string").example("test1234")))))
							.required(true))
						.responses(new ApiResponses()
							.addApiResponse("200", new ApiResponse()
								.description("Successful login")
								.content(new Content()
									.addMediaType("application/json", new MediaType()
										.schema(new Schema<>()
											.type("object")
											.addProperty("success", new Schema<>().type("boolean"))
											.addProperty("accessToken", new Schema<>().type("string"))
											.addProperty("refreshToken", new Schema<>().type("string"))
											.addProperty("message", new Schema<>().type("string")))))))
					// Add other responses or attributes as needed
				)
			);

	}
}
