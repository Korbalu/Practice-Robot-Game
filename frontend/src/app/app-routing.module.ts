import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {UserDetailsComponent} from "./components/user-details/user-details.component";
import {CityCreationComponent} from "./components/city-creation/city-creation.component";
import {CityComponent} from "./components/city/city.component";

const routes: Routes = [
  {path:"login", component: LoginComponent},
  {path:"user-details", component: UserDetailsComponent},
  {path:"city-creation", component: CityCreationComponent},
  {path:"city", component: CityComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
