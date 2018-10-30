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

package com.lbs.tedam.ui.components.window.uploadedfiles;

import com.lbs.tedam.model.TedamFile;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.util.TedamFileUtils;
import com.vaadin.spring.annotation.SpringComponent;
import org.vaadin.spring.annotation.PrototypeScope;

@SpringComponent
@PrototypeScope
public class UploadedFilesDataProvider extends AbstractDataProvider<TedamFile> {

    private static final long serialVersionUID = 1L;

    public void setFilePath(String filePath) {
        buildListDataProvider(TedamFileUtils.getFiles(filePath));
    }

}