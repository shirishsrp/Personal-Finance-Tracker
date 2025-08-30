package com.financetracker;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@SpringBootApplication // includes @Configuration
public class Application {

	private static final String SCHEME_NAME = "BearerAuth";
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/*
	 * Configure ModelMapper as spring bean - so thar SC will manage it's life cycle
	 * + provide it as the depcy
	 */
	@Bean // method level annotation - to tell SC , following method
	// rets an object - which has to be managed as a spring bean
	// manages - life cycle +
	public ModelMapper modelMapper() {
		System.out.println("in model mapper creation");
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration()
				/*
				 * To tell ModelMapper to map only those props whose names match in src n dest.
				 * objects
				 */
				.setMatchingStrategy(MatchingStrategies.STRICT)
				/*
				 * To tell ModelMapper not to transfer nulls from src -> dest
				 */
				.setPropertyCondition(Conditions.isNotNull());// use case - PUT
		return mapper;

	}
	
	 

	    @Bean
	    public OpenAPI customOpenAPI() {
	        return new OpenAPI()
	            .components(new Components()
	                .addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
	                    .type(SecurityScheme.Type.HTTP)
	                    .scheme("bearer")
	                    .bearerFormat("JWT")
	                )
	            )
	            .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
	    }

}
