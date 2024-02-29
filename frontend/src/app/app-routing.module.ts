import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {UserDetailsComponent} from "./components/user-details/user-details.component";
import {CityCreationComponent} from "./components/city-creation/city-creation.component";
import {CityComponent} from "./components/city/city.component";
import {BuilderRecruiterComponent} from "./components/builder-recruiter/builder-recruiter.component";

const routes: Routes = [
  {path:"login", component: LoginComponent},
  {path:"user-details", component: UserDetailsComponent},
  {path:"city-creation", component: CityCreationComponent},
  {path:"city", component: CityComponent},
  {path:"builder-recruiter", component: BuilderRecruiterComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
