/*
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
import {Component, OnInit, ViewChild} from '@angular/core';
import { DomainService } from "../../../../services/domain.service";
import { DialogService } from "../../../../services/dialog.service";
import { ActivatedRoute, Router } from "@angular/router";
import { SnackbarService } from "../../../../services/snackbar.service";
import { BreadcrumbService } from "../../../../../libraries/ng2-breadcrumb/components/breadcrumbService";
import { SidenavService } from "../../../../components/sidenav/sidenav.service";
import {ClientService} from "../../../../services/client.service";
import * as _ from 'lodash';
import {MatInput} from "@angular/material";

export interface Client {
  id: string;
  clientId: string;
}

@Component({
  selector: 'app-openid-client-registration',
  templateUrl: './client-registration.component.html',
  styleUrls: ['./client-registration.component.scss']
})
export class DomainSettingsOpenidClientRegistrationComponent implements OnInit {

  @ViewChild('chipInput') chipInput: MatInput;

  formChanged: boolean = false;
  domain: any = {};
  allowedClients: Client[];
  clients: any[] = [];

  constructor(private domainService: DomainService, private dialogService: DialogService, private snackbarService: SnackbarService,
              private router: Router, private route: ActivatedRoute, private breadcrumbService: BreadcrumbService, private sidenavService: SidenavService,
              private clientService: ClientService) {
  }

  ngOnInit() {
    this.domain = this.route.snapshot.parent.data['domain'];
    this.clientService.findByDomain(this.domain.id).map(res => res.json()).subscribe(data =>  this.initClients(data));
  }

  initClients(clientList) {
    //Remove non existing (deleted) clients from the domain allowedClientsToRegister
    this.allowedClients = _.map(this.domain.allowedClientsToRegister, function(client) {
      return _.find(clientList, { 'clientId': client });
    }).filter(element => element !== undefined);
    this.clients = _.difference(clientList, this.allowedClients);

    //Update allowed client list if some have been removed...
    if(this.allowedClients.length < this.domain.allowedClientsToRegister.length) {
      this.update();
    }
  }

  addClient(event) {
    this.allowedClients = this.allowedClients.concat(_.remove(this.clients, { 'clientId': event.option.value }));
    this.chipInput['nativeElement'].blur();
    this.formChanged = true;
  }

  removeClient(client) {
    this.clients = this.clients.concat(_.remove(this.allowedClients, function(selectClient) {
      return selectClient.clientId === client.clientId;
    }));
    this.chipInput['nativeElement'].blur();
    this.formChanged = true;
  }

  enableDynamicClientRegistration(event) {
    this.domain.dynamicClientRegistrationEnabled = event.checked;
    //If disabled, ensure to disable open dynamic client registration too.
    if(!event.checked) {
      this.domain.openClientRegistrationEnabled = event.checked;
    }
    this.formChanged = true;
  }

  enableOpenDynamicClientRegistration(event) {
    this.domain.openClientRegistrationEnabled = event.checked;
    //If enabled, ensure to enable dynamic client registration too.
    if(event.checked) {
      this.domain.dynamicClientRegistrationEnabled = event.checked;
    }
    this.formChanged = true;
  }

  update() {
    this.domain.allowedClientsToRegister = _.map(this.allowedClients, client => client.clientId);

    this.domainService.update(this.domain.id, this.domain).subscribe(response => {
      this.domain = response.json();
      this.domainService.notify(this.domain);
      this.sidenavService.notify(this.domain);
      this.breadcrumbService.addFriendlyNameForRoute('/domains/'+this.domain.id, this.domain.name);
      this.snackbarService.open("Domain " + this.domain.name + " updated");
    });
  }

}
