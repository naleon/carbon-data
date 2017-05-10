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
package org.wso2.carbon.datasource.reader.mongo.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Options for a replicaSet.
 *
 */
@XmlRootElement(name = "replicaSetOptions")
public class ReplicaSetOptionsConfig {

    private String host1;

    private String port1;

    private String host2;

    private String port2;

    private String host3;

    private String port3;

    private String replicaSetName;

    @XmlElement(name = "host1", required = true, nillable = false)
    public String getHost1() {
        return host1;
    }

    public void setHost1(String host1) {
        this.host1 = host1;
    }

    @XmlElement(name = "port1", defaultValue = "27017")
    public String getPort1() {
        return port1;
    }

    public void setPort1(String port1) {
        this.port1 = port1;
    }

    @XmlElement(name = "host2")
    public String getHost2() {
        return host2;
    }

    public void setHost2(String host2) {
        this.host2 = host2;
    }

    @XmlElement(name = "port2")
    public String getPort2() {
        return port2;
    }

    public void setPort2(String port2) {
        this.port2 = port2;
    }

    @XmlElement(name = "host3")
    public String getHost3() {
        return host3;
    }

    public void setHost3(String host3) {
        this.host3 = host3;
    }

    @XmlElement(name = "port3")
    public String getPort3() {
        return port3;
    }

    public void setPort3(String port3) {
        this.port3 = port3;
    }

    @XmlElement(name = "replicaSetName")
    public String getReplicaSetName() {
        return replicaSetName;
    }

    public void setReplicaSetName(String replicaSetName) {
        this.replicaSetName = replicaSetName;
    }

}
