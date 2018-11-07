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
package io.gravitee.am.service.model;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class UpdateDomain {

    /**
     * Domain name.
     */
    @NotNull
    private String name;

    /**
     * Domain description.
     */
    private String description;

    /**
     * Domain enabled.
     */
    @NotNull
    private boolean enabled;

    /**
     * Domain open Dynamic Client Registration enabled
     */
    @NotNull
    private boolean dynamicClientRegistrationEnabled;

    /**
     * Domain open Dynamic Client Registration enabled
     */
    @NotNull
    private boolean openClientRegistrationEnabled;

    /**
     * Domain allowed clients to use Dynamic Client Registration
     */
    private Set<String> allowedClientsToRegister;

    /**
     * Domain HTTP path.
     */
    private String path;

    private Set<String> identities;

    private Set<String> oauth2Identities;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
