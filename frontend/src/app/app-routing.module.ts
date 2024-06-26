import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {UserDetailsComponent} from "./components/user-details/user-details.component";
import {CityCreationComponent} from "./components/city-creation/city-creation.component";
import {CityComponent} from "./components/city/city.component";
import {BuilderRecruiterComponent} from "./components/builder-recruiter/builder-recruiter.component";
import {CityListComponent} from "./components/city-list/city-list.component";
import {MarketComponent} from "./components/market/market.component";
import {MissionComponent} from "./components/mission/mission.component";
import {MapComponent} from "./components/map/map.component";
import {LogComponent} from "./components/log/log.component";
import {HomeComponent} from "./components/home/home.component";

const routes: Routes = [
  {path:"", component: HomeComponent},
  {path:"login", component: LoginComponent},
  {path:"user-details", component: UserDetailsComponent},
  {path:"city-creation", component: CityCreationComponent},
  {path:"city", component: CityComponent},
  {path:"builder-recruiter", component: BuilderRecruiterComponent},
  {path:"city-list", component: CityListComponent},
  {path:"market", component: MarketComponent},
  {path:"mission", component: MissionComponent},
  {path:"map", component: MapComponent},
  {path:"log", component: LogComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
