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

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Adds link-tags for "add to homescreen" icons to the head-section of the bootstrap page.
 * <p>
 * Generates links of the type
 *
 * <pre>
 * {@code
 * <link rel="icon" sizes="96x96" href="VAADIN/themes/apptheme/icon-96.png">
 * <link rel="apple-touch-icon" sizes="192x192" href=
 * "VAADIN/themes/apptheme/icon-192.png">
 * }
 * </pre>
 * </p>
 */
public class IconBootstrapListener implements BootstrapListener {

    private static final long serialVersionUID = 1L;
    protected String baseUri = "theme://icon-";
    protected String extension = ".png";
    protected String[] rels = {"icon", "apple-touch-icon"};
    protected int[] sizes = {192, 96};

    @Override
    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
        // NOP
    }

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        // Generate link-tags for "add to homescreen" icons
        final Document document = response.getDocument();
        final Element head = document.getElementsByTag("head").get(0);
        for (String rel : rels) {
            for (int size : sizes) {
                String iconUri = baseUri + size + extension;
                String href = response.getUriResolver().resolveVaadinUri(iconUri);
                String s = size + "x" + size;
                Element element = document.createElement("link");
                element.attr("rel", rel);
                element.attr("sizes", s);
                element.attr("href", href);
                head.appendChild(element);
            }
        }
    }

}