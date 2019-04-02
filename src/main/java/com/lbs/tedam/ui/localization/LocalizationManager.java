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

package com.lbs.tedam.ui.localization;

import org.springframework.stereotype.Component;

import com.lbs.tedam.localization.Localizer;
import com.lbs.tedam.localization.LocalizerManager;
import com.lbs.tedam.ui.util.LocalizationFactory;

@Component
public class LocalizationManager {

	public LocalizationManager() {
		initLocalization();
	}

	private void initLocalization() {
		Localizer localizer = Localizer.instance("tedamface", "localization.tedamface");
		LocalizerManager.putLocalizer("tedamface", localizer);

		Localizer formOpenLocalizer = Localizer.instance("formopen", "localization.formopen");
		LocalizerManager.putLocalizer("formopen", formOpenLocalizer);

		Localizer exceptionLocalizer = Localizer.instance("localizedException", "localization.localizedException");
		LocalizerManager.putLocalizer("localizedException", exceptionLocalizer);

		// TODO language to be learned from the outside
		LocalizerManager.loadLocaleForAll(LocalizationFactory.getDefaultLanguage());
	}

}
