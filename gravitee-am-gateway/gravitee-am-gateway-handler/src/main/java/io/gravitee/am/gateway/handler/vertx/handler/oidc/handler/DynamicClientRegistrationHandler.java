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
package io.gravitee.am.gateway.handler.vertx.handler.oidc.handler;

import io.gravitee.am.gateway.handler.oauth2.token.AccessToken;
import io.gravitee.am.gateway.handler.oauth2.token.TokenService;
import io.gravitee.am.gateway.handler.oauth2.token.impl.DefaultAccessToken;
import io.gravitee.am.gateway.handler.oidc.exception.ClientRegistrationDisabledException;
import io.gravitee.am.model.Domain;
import io.reactivex.Maybe;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
public class DynamicClientRegistrationHandler implements Handler<RoutingContext> {

    private TokenService tokenService;
    private Domain domain;

    private final Logger logger = LoggerFactory.getLogger(DynamicClientRegistrationHandler.class);

    public DynamicClientRegistrationHandler(Domain domain, TokenService tokenService) {
        this.domain = domain;
        this.tokenService = tokenService;
    }

    @Override
    public void handle(RoutingContext context) {
        logger.debug("Dynamic client registration handler");

        //1st check if dynamic client registration is enabled.
        if(!domain.isDynamicClientRegistrationEnabled()) {
            logger.debug("Dynamic client registration is disabled");
            context.fail(new ClientRegistrationDisabledException());
            return;
        }

        //Do not apply security check if open dynamic client registration is enabled.
        if(domain.isOpenClientRegistrationEnabled()) {
            logger.debug("Open Dynamic client registration is enabled");
            context.next();
            return;
        }

        //Extract credentials from the request.
        tokenService
                .extractAccessToken(context)
                .flatMap(accessToken -> tokenService.getAccessToken(accessToken.getValue()))
                .filter(this::clientAllowedAndTokenNotExpired)
                .switchIfEmpty(Maybe.error(new ClientRegistrationDisabledException()))
                    .subscribe(
                            token -> {
                                context.put(AccessToken.ACCESS_TOKEN, token);
                                context.put("domain",domain.getId());
                                context.next();
                            },
                            error -> context.fail(error)
                    );
    }

    private boolean clientAllowedAndTokenNotExpired(AccessToken accessToken) {
        DefaultAccessToken token = (DefaultAccessToken) accessToken;
        return domain.getAllowedClientsToRegister().contains(token.getClientId()) &&
                token.getExpireAt().after(new Date());
    }
}

