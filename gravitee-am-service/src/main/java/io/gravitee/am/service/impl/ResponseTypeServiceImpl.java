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
package io.gravitee.am.service.impl;

import io.gravitee.am.service.ResponseTypeService;
import io.gravitee.am.service.exception.InvalidClientMetadataException;
import org.springframework.stereotype.Component;

import java.util.*;

import static io.gravitee.am.common.oauth2.ResponseType.CODE;
import static io.gravitee.am.common.oauth2.ResponseType.TOKEN;
import static io.gravitee.am.common.oidc.ResponseType.ID_TOKEN;

/**
 * @author Alexandre FARIA
 * @author GraviteeSource Team
 */
@Component
public class ResponseTypeServiceImpl implements ResponseTypeService {

    private static final Set<String> VALID_RESPONSE_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            CODE, TOKEN, ID_TOKEN
    )));

    /**
     * Throw InvalidClientMetadataException if null or empty, or contains unknown response types.
     * @param responseTypes Array of response_type to validate.
     */
    @Override
    public boolean isValideResponseType(List<String> responseTypes) {
        if(responseTypes==null || responseTypes.isEmpty()) {
            return false;
        }

        for(String responseType:responseTypes) {
            if(!this.isValideResponseType(responseType)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Throw InvalidClientMetadataException if null or contains unknown response types.
     * @param responseType String to response_type validate.
     */
    @Override
    public boolean isValideResponseType(String responseType) {
        return VALID_RESPONSE_TYPES.contains(responseType);
    }
}
