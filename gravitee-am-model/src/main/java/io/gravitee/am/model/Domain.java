/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.am.model;

import io.gravitee.am.model.login.LoginForm;

import java.util.Date;
import java.util.Set;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Domain {

    /**
     * Domain identifier.
     */
    private String id;

    /**
     * Domain name.
     */
    private String name;

    /**
     * Domain description.
     */
    private String description;

    /**
     * Domain enabled.
     */
    private boolean enabled;

    /**
     * Domain Dynamic Client Registration enabled
     */
    private boolean dynamicClientRegistrationEnabled;

    /**
     * Domain open Dynamic Client Registration enabled
     */
    private boolean openClientRegistrationEnabled;

    /**
     * Domain allowed clients to use Dynamic Client Registration
     */
    private Set<String> allowedClientsToRegister;

    /**
     * Domain master flag.
     */
    private boolean master;

    /**
     * Domain creation date
     */
    private Date createdAt;

    /**
     * Domain last updated date
     */
    private Date updatedAt;

    /**
     * Domain HTTP path
     */
    private String path;

    private LoginForm loginForm;

    private Set<String> identities;

    private Set<String> oauth2Identities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDynamicClientRegistrationEnabled() { return dynamicClientRegistrationEnabled; }

    public void setDynamicClientRegistrationEnabled(boolean dynamicClientRegistrationEnabled) {
        this.dynamicClientRegistrationEnabled = dynamicClientRegistrationEnabled;
    }

    public boolean isOpenClientRegistrationEnabled() { return openClientRegistrationEnabled; }

    public void setOpenClientRegistrationEnabled(boolean openClientRegistrationEnabled) {
        this.openClientRegistrationEnabled = openClientRegistrationEnabled;
    }

    public Set<String> getAllowedClientsToRegister() { return allowedClientsToRegister; }

    public void setAllowedClientsToRegister(Set<String> allowedClientsToRegister) {
        this.allowedClientsToRegister = allowedClientsToRegister;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public Set<String> getIdentities() {
        return identities;
    }

    public void setIdentities(Set<String> identities) {
        this.identities = identities;
    }

    public Set<String> getOauth2Identities() {
        return oauth2Identities;
    }

    public void setOauth2Identities(Set<String> oauth2Identities) {
        this.oauth2Identities = oauth2Identities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;

        return id.equals(domain.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
