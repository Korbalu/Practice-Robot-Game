import { Component } from '@angular/core';
import {CityService} from "../../services/city.service";
import {ArmyService} from "../../services/army.service";

@Component({
  selector: 'app-builder-recruiter',
  templateUrl: './builder-recruiter.component.html',
  styleUrls: ['./builder-recruiter.component.css']
})
export class BuilderRecruiterComponent {

  constructor(private cityService: CityService, private armyService: ArmyService) {

  }
}
