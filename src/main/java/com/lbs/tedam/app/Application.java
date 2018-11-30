/*
 * Copyright 2014-2019 Logo Business Solutions
 * (a.k.a. LOGO YAZILIM SAN. VE TIC. A.S)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.lbs.tedam.app;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.vaadin.spring.events.annotation.EnableEventBus;

import com.lbs.tedam.app.security.SecurityConfig;
import com.lbs.tedam.data.config.DataConfig;
import com.lbs.tedam.data.service.impl.TedamUserServiceImpl;
import com.lbs.tedam.ui.AppUI;
import com.lbs.tedam.util.DataInitializationUtil;

@SpringBootApplication(scanBasePackageClasses = { AppUI.class, Application.class,
		TedamUserServiceImpl.class, SecurityConfig.class, DataConfig.class, TedamFaceDataConfig.class,
		DataInitializationUtil.class
})
@EnableEventBus
public class Application extends SpringBootServletInitializer {

	@Autowired
	private DataInitializationUtil dataInitUtil;

	public static final String APP_URL = "/";
	public static final String LOGIN_URL = "/login.html";
	public static final String LOGOUT_URL = "/login.html?logout";
	public static final String LOGIN_FAILURE_URL = "/login.html?error";
	public static final String LOGIN_PROCESSING_URL = "/login";
	public static final String REST_URL = "api";

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Bean
	public InitializingBean initializeDatabase() {
		return () -> {
			dataInitUtil.loadInitialData();
		};
	}

}
