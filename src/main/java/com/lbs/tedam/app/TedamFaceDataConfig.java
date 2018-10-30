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

import com.lbs.tedam.data.DbConnectionInfo;
import com.lbs.tedam.util.PropUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TedamFaceDataConfig {

    /**
     * Creates a new instance of database connection info
     *
     * @return A prototype instance of DbConnectionInfo.
     */
    @Bean
    public DbConnectionInfo getDbConnectionInfo() {
        String driverClassName = PropUtils.getProperty("hibernate.tedamdb.connection.driverClassName");
        String url = PropUtils.getProperty("hibernate.tedamdb.connection.url");
        String userName = PropUtils.getProperty("hibernate.tedamdb.connection.username");
        String password = PropUtils.getProperty("hibernate.tedamdb.connection.password");
        String ddlMode = PropUtils.getProperty("hibernate.tedamdb.connection.ddlMode");
        String showSql = PropUtils.getProperty("hibernate.tedamdb.connection.showSql");
        String dialect = PropUtils.getProperty("hibernate.tedamdb.connection.dialect");
        String dataSource = PropUtils.getProperty("hibernate.tedamdb.connection.dataSourceClassName");
        DbConnectionInfo info = new DbConnectionInfo(driverClassName, url, userName, password, dialect, dataSource);
        info.setDdlMode(ddlMode);
        info.setShowSql(showSql);
        return info;
    }

}
