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
package io.gravitee.am.gateway.handler.vertx.handler.oidc.endpoint;

import io.gravitee.am.gateway.handler.oidc.response.DynamicClientRegistrationResponse;
import io.gravitee.am.service.ClientService;
import io.gravitee.am.service.model.ClientPayload;
import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.MediaType;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dynamic Client Registration is a protocol that allows OAuth client applications to register with an OAuth server.
 * Specifications are defined by OpenID Foundation and by the IETF as RFC 7591 too.
 * They define how a client may submit a request to register itself and how should be the response.
 *
 * See <a href="https://openid.net/specs/openid-connect-registration-1_0.html">Openid Connect Dynamic Client Registration</a>
 * See <a href="https://tools.ietf.org/html/rfc7591"> OAuth 2.0 Dynamic Client Registration Protocol</a>
 *
 * @author Alexandre FARIA
 * @author GraviteeSource Team
 */
public class DynamicClientRegistrationEndpoint implements Handler<RoutingContext> {

    private ClientService clientService;

    private final Logger logger = LoggerFactory.getLogger(DynamicClientRegistrationEndpoint.class);

    public DynamicClientRegistrationEndpoint(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void handle(RoutingContext context) {
        logger.debug("Dynamic client registration endpoint");

        String domain = context.get("domain");
        ClientPayload payload = context.getBodyAsJson().mapTo(ClientPayload.class);

        //Register application
        clientService.validateAndRegisterClient(domain, payload)
                .subscribe(
                        client -> context.response()
                                .putHeader(HttpHeaders.CACHE_CONTROL, "no-store")
                                .putHeader(HttpHeaders.PRAGMA, "no-cache")
                                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                .end(Json.encodePrettily(DynamicClientRegistrationResponse.fromClient(client)))
                        , error -> context.fail(error)
                );
    }
}
