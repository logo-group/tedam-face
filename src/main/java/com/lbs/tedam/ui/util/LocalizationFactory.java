package com.lbs.tedam.ui.util;

import java.util.Locale;

import org.springframework.stereotype.Component;

import com.lbs.tedam.util.PropUtils;

@Component
public class LocalizationFactory {

	public static Locale getDefaultLanguage() {
		String language = PropUtils.getProperty("tedam.defaultLanguage");
		String country = PropUtils.getProperty("tedam.defaultCountry");
		return new Locale(language, country);
	}

}
