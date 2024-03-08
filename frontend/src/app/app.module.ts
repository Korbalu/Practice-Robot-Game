import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { LoginComponent } from './components/login/login.component';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import { UserDetailsComponent } from './components/user-details/user-details.component';
import { CityCreationComponent } from './components/city-creation/city-creation.component';
import { CityComponent } from './components/city/city.component';
import { BuilderRecruiterComponent } from './components/builder-recruiter/builder-recruiter.component';
import { CityListComponent } from './components/city-list/city-list.component';
import { MarketComponent } from './components/market/market.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    UserDetailsComponent,
    CityCreationComponent,
    CityComponent,
    BuilderRecruiterComponent,
    CityListComponent,
    MarketComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
