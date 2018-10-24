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

import io.gravitee.am.common.oidc.ApplicationType;
import io.gravitee.am.model.Client;
import io.gravitee.am.model.common.Page;
import io.gravitee.am.repository.management.api.ClientRepository;
import io.gravitee.am.repository.oauth2.api.AccessTokenRepository;
import io.gravitee.am.service.*;
import io.gravitee.am.service.exception.*;
import io.gravitee.am.service.model.*;
import io.gravitee.am.service.utils.SetterUtils;
import io.gravitee.common.utils.UUID;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class ClientServiceImpl implements ClientService {

    private final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private IdentityProviderService identityProviderService;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private DomainService domainService;

    @Autowired
    private ResponseTypeService responseTypeService;

    @Autowired
    private GrantTypeService grantTypeService;

    @Autowired
    private ScopeService scopeService;



    @Override
    public Maybe<Client> findById(String id) {
        LOGGER.debug("Find client by ID: {}", id);
        return clientRepository.findById(id)
                .map(client -> {
                    // Send an empty array in case of no grant types
                    if (client.getGrantTypes() == null) {
                        client.setGrantTypes(Collections.emptyList());
                    }
                    return client;
                })
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find a client using its ID: {}", id, ex);
                    return Maybe.error(new TechnicalManagementException(
                            String.format("An error occurs while trying to find a client using its ID: %s", id), ex));
                });
    }

    @Override
    public Maybe<Client> findByDomainAndClientId(String domain, String clientId) {
        LOGGER.debug("Find client by domain: {} and client id: {}", domain, clientId);
        return clientRepository.findByClientIdAndDomain(clientId, domain)
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find client by domain: {} and client id: {}", domain, clientId, ex);
                    return Maybe.error(new TechnicalManagementException(
                            String.format("An error occurs while trying to find client by domain: %s and client id: %s", domain, clientId), ex));
                });
    }

    @Override
    public Single<Set<Client>> findByDomain(String domain) {
        LOGGER.debug("Find clients by domain", domain);
        return clientRepository.findByDomain(domain)
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find clients by domain: {}", domain, ex);
                    return Single.error(new TechnicalManagementException(
                            String.format("An error occurs while trying to find clients by domain: %s", domain), ex));
                });
    }

    @Override
    public Single<Page<Client>> findByDomain(String domain, int page, int size) {
        LOGGER.debug("Find clients by domain", domain);
        return clientRepository.findByDomain(domain, page, size)
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find clients by domain: {}", domain, ex);
                    return Single.error(new TechnicalManagementException(
                            String.format("An error occurs while trying to find clients by domain: %s", domain), ex));
                });
    }

    @Override
    public Single<Set<Client>> findByIdentityProvider(String identityProvider) {
        LOGGER.debug("Find clients by identity provider : {}", identityProvider);
        return clientRepository.findByIdentityProvider(identityProvider)
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find clients by identity provider", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to find clients by identity provider", ex));
                });
    }

    @Override
    public Single<Set<Client>> findByCertificate(String certificate) {
        LOGGER.debug("Find clients by certificate : {}", certificate);
        return clientRepository.findByCertificate(certificate)
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find clients by certificate", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to find clients by certificate", ex));
                });
    }

    @Override
    public Single<Set<Client>> findByExtensionGrant(String extensionGrant) {
        LOGGER.debug("Find clients by extension grant : {}", extensionGrant);
        return clientRepository.findByExtensionGrant(extensionGrant)
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find clients by extension grant", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to find clients by extension grant", ex));
                });
    }

    @Override
    public Single<Set<Client>> findAll() {
        LOGGER.debug("Find clients");
        return clientRepository.findAll()
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find clients", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to find clients", ex));
                });
    }

    @Override
    public Single<Page<Client>> findAll(int page, int size) {
        LOGGER.debug("Find clients");
        return clientRepository.findAll(page, size)
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find clients", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to find clients", ex));
                });
    }

    @Override
    public Single<Set<TopClient>> findTopClients() {
        LOGGER.debug("Find top clients");
        return clientRepository.findAll()
                .flatMapObservable(clients -> Observable.fromIterable(clients))
                .flatMapSingle(client -> accessTokenRepository.countByClientId(client.getClientId())
                        .map(oAuth2AccessTokens -> {
                            TopClient topClient = new TopClient();
                            topClient.setClient(client);
                            topClient.setAccessTokens(oAuth2AccessTokens);
                            return topClient;
                        }))
                .toList()
                .map(topClients -> topClients.stream().filter(topClient -> topClient.getAccessTokens() > 0).collect(Collectors.toSet()))
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find top clients", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to find top clients", ex));
                });
    }

    @Override
    public Single<Set<TopClient>> findTopClientsByDomain(String domain) {
        LOGGER.debug("Find top clients by domain: {}", domain);
        return clientRepository.findByDomain(domain)
                .flatMapObservable(clients -> Observable.fromIterable(clients))
                .flatMapSingle(client -> accessTokenRepository.countByClientId(client.getClientId())
                        .map(oAuth2AccessTokens -> {
                            TopClient topClient = new TopClient();
                            topClient.setClient(client);
                            topClient.setAccessTokens(oAuth2AccessTokens);
                            return topClient;
                        }))
                .toList()
                .map(topClients -> topClients.stream().filter(topClient -> topClient.getAccessTokens() > 0).collect(Collectors.toSet()))
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find top clients by domain", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to find top clients by domain", ex));
                });
    }

    @Override
    public Single<TotalClient> findTotalClientsByDomain(String domain) {
        LOGGER.debug("Find total clients by domain: {}", domain);
        return clientRepository.countByDomain(domain)
                .map(totalClients -> {
                    TotalClient totalClient = new TotalClient();
                    totalClient.setTotalClients(totalClients);
                    return totalClient;
                })
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find total clients by domain: {}", domain, ex);
                    return Single.error(new TechnicalManagementException(
                            String.format("An error occurs while trying to find total clients by domain: %s", domain), ex));
                });
    }

    @Override
    public Single<TotalClient> findTotalClients() {
        LOGGER.debug("Find total client");
        return clientRepository.count()
                .map(totalClients -> {
                    TotalClient totalClient = new TotalClient();
                    totalClient.setTotalClients(totalClients);
                    return totalClient;
                })
                .onErrorResumeNext(ex -> {
                    LOGGER.error("An error occurs while trying to find total clients", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to find total clients", ex));
                });
    }

    @Override
    public Single<Client> create(String domain, NewClient newClient) {
        LOGGER.debug("Create a new client {} for domain {}", newClient, domain);
        return clientRepository.findByClientIdAndDomain(newClient.getClientId(), domain)
                .isEmpty()
                    .flatMap(empty -> {
                        if (!empty) {
                            throw new ClientAlreadyExistsException(newClient.getClientId(), domain);
                        } else {
                            Client client = new Client();
                            client.setId(UUID.toString(UUID.random()));
                            client.setClientId(newClient.getClientId());
                            if (newClient.getClientSecret() == null || newClient.getClientSecret().trim().isEmpty()) {
                                client.setClientSecret(UUID.toString(UUID.random()));
                            } else {
                                client.setClientSecret(newClient.getClientSecret());
                            }
                            client.setClientName(newClient.getClientName());
                            client.setDomain(domain);
                            client.setAccessTokenValiditySeconds(Client.DEFAULT_ACCESS_TOKEN_VALIDITY_SECONDS);
                            client.setRefreshTokenValiditySeconds(Client.DEFAULT_REFRESH_TOKEN_VALIDITY_SECONDS);
                            client.setIdTokenValiditySeconds(Client.DEFAULT_ID_TOKEN_VALIDITY_SECONDS);
                            client.setGrantTypes(Client.DEFAULT_GRANT_TYPES);
                            client.setResponseTypes(Client.DEFAULT_RESPONSE_TYPES);
                            client.setEnabled(true);
                            client.setCreatedAt(new Date());
                            client.setUpdatedAt(client.getCreatedAt());

                            return clientRepository.create(client)
                                    .flatMap(client1 -> {
                                        // Reload domain to take care about client creation
                                        return domainService.reload(domain).flatMap(domain1 -> Single.just(client1));
                                    });
                        }
                    })
                .onErrorResumeNext(ex -> {
                    if (ex instanceof AbstractManagementException) {
                        return Single.error(ex);
                    }

                    LOGGER.error("An error occurs while trying to create a client", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to create a client", ex));
                });
    }

    @Override
    public Single<Client> update(String domain, String id, UpdateClient updateClient) {
        LOGGER.debug("Update a client {} for domain {}", id, domain);
        return clientRepository.findById(id)
                .switchIfEmpty(Maybe.error(new ClientNotFoundException(id)))
                .flatMapSingle(client -> {
                    Set<String> identities = updateClient.getIdentities();
                    if (identities == null) {
                        return Single.just(client);
                    } else {
                        return Observable.fromIterable(identities)
                                .flatMapMaybe(identityProviderId -> identityProviderService.findById(identityProviderId))
                                .toList()
                                .flatMap(idp -> Single.just(client));
                    }
                })
                .flatMap(client -> {
                    client.setClientName(updateClient.getClientName());
                    client.setScope(updateClient.getScope());
                    client.setAutoApproveScopes(updateClient.getAutoApproveScopes());
                    client.setAccessTokenValiditySeconds(updateClient.getAccessTokenValiditySeconds());
                    client.setRefreshTokenValiditySeconds(updateClient.getRefreshTokenValiditySeconds());
                    client.setGrantTypes(updateClient.getGrantTypes());
                    client.setRedirectUris(updateClient.getRedirectUris());
                    client.setEnabled(updateClient.isEnabled());
                    client.setIdentities(updateClient.getIdentities());
                    client.setOauth2Identities(updateClient.getOauth2Identities());
                    client.setIdTokenValiditySeconds(updateClient.getIdTokenValiditySeconds());
                    client.setIdTokenCustomClaims(updateClient.getIdTokenCustomClaims());
                    client.setCertificate(updateClient.getCertificate());
                    client.setEnhanceScopesWithUserPermissions(updateClient.isEnhanceScopesWithUserPermissions());
                    client.setGenerateNewTokenPerRequest(updateClient.isGenerateNewTokenPerRequest());
                    client.setUpdatedAt(new Date());

                    return clientRepository.update(client)
                            .flatMap(irrelevant -> {
                                // Reload domain to take care about client update
                                return domainService.reload(domain).flatMap(domain1 -> Single.just(irrelevant));
                            });
                })
                .onErrorResumeNext(ex -> {
                    if (ex instanceof AbstractManagementException) {
                        return Single.error(ex);
                    }

                    LOGGER.error("An error occurs while trying to update a client", ex);
                    return Single.error(new TechnicalManagementException("An error occurs while trying to update a client", ex));
                });
    }

    @Override
    public Completable delete(String clientId) {
        LOGGER.debug("Delete client {}", clientId);
        return clientRepository.findById(clientId)
                .switchIfEmpty(Maybe.error(new ClientNotFoundException(clientId)))
                .flatMapCompletable(client -> clientRepository.delete(clientId).andThen(domainService.reload(client.getDomain()).toCompletable()))
                .onErrorResumeNext(ex -> {
                    if (ex instanceof AbstractManagementException) {
                        return Completable.error(ex);
                    }

                    LOGGER.error("An error occurs while trying to delete client: {}", clientId, ex);
                    return Completable.error(new TechnicalManagementException(
                            String.format("An error occurs while trying to delete client: %s", clientId), ex));
                });
    }

    @Override
    public Single<Client> validateAndRegisterClient(final String domain, final ClientPayload payload) {
        LOGGER.debug("Create a new client {} for domain {}", payload, domain);

        //first ensure payload is valid.
        return this.validateClientPayload(domain, payload).flatMap(isValid -> {
            if(isValid) {
                return this.registerClient(domain, payload);
            }
            return Single.error(new InvalidClientMetadataException());
        });
    }

    private Single<Client> registerClient(final String domain, final ClientPayload payload) {
        Client client = new Client();

        /* set openid request metadata */
        SetterUtils.safeSet(client::setRedirectUris, payload.getRedirectUris());
        SetterUtils.safeSetOrElse(client::setResponseTypes, payload.getResponseTypes(), Client.DEFAULT_RESPONSE_TYPES);
        SetterUtils.safeSetOrElse(client::setGrantTypes, payload.getGrantTypes(), Client.DEFAULT_GRANT_TYPES);
        SetterUtils.safeSetOrElse(client::setApplicationType, payload.getApplicationType(), ApplicationType.WEB);
        SetterUtils.safeSet(client::setContacts, payload.getContacts());
        SetterUtils.safeSet(client::setClientName, payload.getClientName());
        SetterUtils.safeSet(client::setLogoUri, payload.getLogoUri());
        SetterUtils.safeSet(client::setClientUri, payload.getClientUri());
        SetterUtils.safeSet(client::setPolicyUri, payload.getPolicyUri());
        SetterUtils.safeSet(client::setTosUri, payload.getTosUri());

        //--> Must analyse openid specifications for below properties...
        SetterUtils.safeSet(client::setJwksUri, payload.getJwksUri());
        //SetterUtils.safeSet(client::setJwks, payload.getJwks());TODO:Manage it later...
        SetterUtils.safeSet(client::setSectorIdentifierUri, payload.getSectorIdentifierUri());
        SetterUtils.safeSet(client::setSubjectType, payload.getSubjectType());
        SetterUtils.safeSet(client::setIdTokenSignedResponseAlg, payload.getIdTokenSignedResponseAlg());
        SetterUtils.safeSet(client::setIdTokenEncryptedResponseAlg, payload.getIdTokenEncryptedResponseAlg());
        SetterUtils.safeSet(client::setIdTokenEncryptedResponseEnc, payload.getIdTokenEncryptedResponseEnc());
        SetterUtils.safeSet(client::setUserinfoSignedResponseAlg, payload.getUserinfoSignedResponseAlg());
        SetterUtils.safeSet(client::setUserinfoEncryptedResponseAlg, payload.getUserinfoEncryptedResponseAlg());
        SetterUtils.safeSet(client::setUserinfoEncryptedResponseEnc, payload.getUserinfoEncryptedResponseEnc());
        SetterUtils.safeSet(client::setRequestObjectSigningAlg, payload.getRequestObjectSigningAlg());
        SetterUtils.safeSet(client::setRequestObjectEncryptionAlg, payload.getRequestObjectEncryptionAlg());
        SetterUtils.safeSet(client::setRequestObjectEncryptionEnc, payload.getRequestObjectEncryptionEnc());
        SetterUtils.safeSet(client::setTokenEndpointAuthMethod, payload.getTokenEndpointAuthMethod());
        SetterUtils.safeSet(client::setTokenEndpointAuthSigningAlg, payload.getTokenEndpointAuthSigningAlg());
        SetterUtils.safeSet(client::setDefaultMaxAge, payload.getDefaultMaxAge());
        SetterUtils.safeSet(client::setRequireAuthTime, payload.getRequireAuthTime());
        SetterUtils.safeSet(client::setDefaultACRvalues, payload.getDefaultACRvalues());
        SetterUtils.safeSet(client::setInitiateLoginUri, payload.getInitiateLoginUri());
        SetterUtils.safeSet(client::setRequestUris, payload.getRequestUris());

        /* set oauth2 request metadata */
        SetterUtils.safeSet(client::setScope, payload.getScope());
        SetterUtils.safeSet(client::setSoftwareId, payload.getSoftwareId());
        SetterUtils.safeSet(client::setSoftwareVersion, payload.getSoftwareVersion());
        SetterUtils.safeSet(client::setSoftwareStatement, payload.getSoftwareStatement());

        /* openid response metadata */
        client.setId(UUID.toString(UUID.random()));
        client.setClientId(UUID.toString(UUID.random()));
        client.setClientSecret(UUID.toString(UUID.random()));

        /* GRAVITEE.IO custom fields */
        client.setDomain(domain);
        client.setAccessTokenValiditySeconds(Client.DEFAULT_ACCESS_TOKEN_VALIDITY_SECONDS);
        client.setRefreshTokenValiditySeconds(Client.DEFAULT_REFRESH_TOKEN_VALIDITY_SECONDS);
        client.setIdTokenValiditySeconds(Client.DEFAULT_ID_TOKEN_VALIDITY_SECONDS);
        client.setEnabled(true);
        client.setCreatedAt(new Date());
        client.setUpdatedAt(client.getCreatedAt());

        //ensure correspondance between response & grant types.
        grantTypeService.completeGrantTypeCorrespondance(client);

        return clientRepository.create(client)
                .flatMap(justCreatedClient -> {
                    // Reload domain to take care about client creation
                    return domainService.reload(domain).flatMap(domain1 -> Single.just(justCreatedClient));
                });
    }

    /**
     * Validate payload according to openid specifications.
     *
     * https://openid.net/specs/openid-connect-registration-1_0.html#ClientMetadata
     *
     * @param domain String domain
     * @param payload ClientPayload
     */
    private Single<Boolean> validateClientPayload(final String domain, final ClientPayload payload) {
        LOGGER.debug("Validating dynamic client registration payload");

        //Should not be null...
        if(payload==null) {
            return Single.error(new InvalidClientMetadataException());
        }

        //Redirect_uri is required, must be informed and filled without null values.
        if(payload.getRedirectUris()==null ||
                !payload.getRedirectUris().isPresent() ||
                payload.getRedirectUris().get().size()==0 ||
                new HashSet<>(payload.getRedirectUris().get()).contains(null)
        ) {
            return Single.error(new InvalidRedirectUriException());
        }

        //TODO: add check if env Production, then forbid localhost uris...
        //TODO: If application type is web, then no http without and no localhost

        //if response_type provided, they must be valid.
        if(payload.getResponseTypes()!=null) {
            if(!responseTypeService.isValideResponseType(payload.getResponseTypes().get())) {
                return Single.error(new InvalidClientMetadataException("Invalid response type."));
            }
        }

        //if grant_type provided, they must be valid.
        if(payload.getGrantTypes()!=null) {
            if(!grantTypeService.isValideGrantType(payload.getGrantTypes().get())) {
                return Single.error(new InvalidClientMetadataException("Missing or invalid grant type."));
            }
        }

        //Check scopes.
        if(payload.getScope()!=null) {
            return scopeService.validateScope(domain, payload.getScope().get());
        }

        return Single.just(true);
    }
}
