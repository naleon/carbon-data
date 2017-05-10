/*
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.datasource.reader.mongo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.wso2.carbon.datasource.reader.mongo.config.MongoDataSourceConfiguration;
import org.wso2.carbon.ndatasource.common.DataSourceException;
import org.wso2.carbon.utils.CarbonUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * Utilities to read the configuration for Mongo databases.
 *
 */
public class MongoDataSourceReaderUtil {

    public static MongoClient loadConfiguration(String xmlConfiguration) throws DataSourceException {
        ByteArrayInputStream baos = null;
        try {
            xmlConfiguration = CarbonUtils.replaceSystemVariablesInXml(xmlConfiguration);
            JAXBContext ctx = JAXBContext.newInstance(MongoDataSourceConfiguration.class);
            baos = new ByteArrayInputStream(xmlConfiguration.getBytes());
            MongoDataSourceConfiguration fileConfig = (MongoDataSourceConfiguration) ctx.createUnmarshaller().unmarshal(baos);
            MongoClient result = null;
            MongoClientOptions.Builder builder = MongoClientOptions.builder();
            if (fileConfig.getUrl() != null) {
                if (fileConfig.getSslInvalidHostNameAllowed() != null) {
                    builder.sslInvalidHostNameAllowed(fileConfig.getSslInvalidHostNameAllowed());
                }
                MongoClientURI uri = new MongoClientURI(fileConfig.getUrl(), builder);
                result = new MongoClient(uri);
            } else {
                List<ServerAddress> addressList = new ArrayList<ServerAddress>();
                if (fileConfig.getReplicaSetConfig() != null) {
                    ServerAddress address1 = new ServerAddress(fileConfig.getReplicaSetConfig().getHost1(),
                            Integer.parseInt(fileConfig.getReplicaSetConfig().getPort1()));
                    addressList.add(address1);
                    if (fileConfig.getReplicaSetConfig().getHost2() != null && fileConfig.getReplicaSetConfig().getPort2() != null) {
                        ServerAddress address2 = new ServerAddress(fileConfig.getReplicaSetConfig().getHost2(),
                                Integer.parseInt(fileConfig.getReplicaSetConfig().getPort2()));
                        addressList.add(address2);
                    }
                    if (fileConfig.getReplicaSetConfig().getHost3() != null && fileConfig.getReplicaSetConfig().getPort3() != null) {
                        ServerAddress address3 = new ServerAddress(fileConfig.getReplicaSetConfig().getHost3(),
                                Integer.parseInt(fileConfig.getReplicaSetConfig().getPort3()));
                        addressList.add(address3);
                    }
                } else {
                    ServerAddress address = new ServerAddress(fileConfig.getHost(), Integer.parseInt(fileConfig.getPort()));
                    addressList.add(address);
                }
                MongoCredential credential = null;
                if (fileConfig.getWithSSL() != null) {
                    builder.sslEnabled(fileConfig.getWithSSL());
                }
                if (fileConfig.getSslInvalidHostNameAllowed() != null) {
                    builder.sslInvalidHostNameAllowed(fileConfig.getSslInvalidHostNameAllowed());
                }
                if (fileConfig.getAuthenticationMethodEnum() != null && fileConfig.getUsername() != null) {
                    credential = createCredentials(fileConfig);
                }
                if (credential != null) {
                    result = new MongoClient(addressList, Arrays.asList(new MongoCredential[] { credential }), builder.build());
                } else {
                    result = new MongoClient(addressList, builder.build());
                }
            }
            return result;
        } catch (Exception e) {
            throw new DataSourceException("Error loading Mongo Datasource configuration: " + e.getMessage(), e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException ignore) {
                    // ignore
                }
            }
        }
    }

    private static MongoCredential createCredentials(MongoDataSourceConfiguration fileConfig) {
        MongoCredential credential;
        switch (fileConfig.getAuthenticationMethodEnum()) {
        case SCRAM_SHA_1:
            credential = MongoCredential.createScramSha1Credential(fileConfig.getUsername(), fileConfig.getDatabase(),
                    fileConfig.getPassword().toCharArray());
            break;
        case MONGODB_CR:
            credential = MongoCredential.createMongoCRCredential(fileConfig.getUsername(), fileConfig.getDatabase(),
                    fileConfig.getPassword().toCharArray());
        case LDAP_PLAIN:
            credential = MongoCredential.createPlainCredential(fileConfig.getUsername(), fileConfig.getAuthSource(),
                    fileConfig.getPassword().toCharArray());
        case X_509:
            credential = MongoCredential.createMongoX509Credential(fileConfig.getUsername());
        case GSSAPI:
            credential = MongoCredential.createGSSAPICredential(fileConfig.getUsername());
        case DEFAULT:
        default:
            credential = MongoCredential.createCredential(fileConfig.getUsername(), fileConfig.getDatabase(),
                    fileConfig.getPassword().toCharArray());
        }
        return credential;
    }

}
