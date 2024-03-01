import {Component} from '@angular/core';
import {CityService} from "../../services/city.service";
import {ArmyService} from "../../services/army.service";
import {UnitListModel} from "../../models/unit-list-model";
import {AllBuildingsListModel} from "../../models/all-buildings-list-model";
import {LegionCreationModel} from "../../models/legion-creation-model";
import {BuildingCreationModel} from "../../models/building-creation-model";

@Component({
  selector: 'app-builder-recruiter',
  templateUrl: './builder-recruiter.component.html',
  styleUrls: ['./builder-recruiter.component.css']
})
export class BuilderRecruiterComponent {
  units: Array<UnitListModel> = [];
  buildings: Array<AllBuildingsListModel> = [];

  constructor(private cityService: CityService, private armyService: ArmyService) {

  }

  ngOnInit() {
    this.cityService.everyBuildingLister().subscribe({
      next: (data) => {
        this.buildings = data;
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
    this.armyService.unitList().subscribe({
      next: (data2) => {
        this.units = data2;
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
  }

  recruiter(quantityP: number, unitP:string){
    let legion: LegionCreationModel = {unit : unitP, quantity: quantityP}
    this.armyService.createLegion(legion).subscribe({
      next: () => {
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
  }

  buildingBuilder(buildingP:string){
    let building:BuildingCreationModel = {building: buildingP}
    this.cityService.buildingBuilder(building).subscribe({
      next: () => {
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
        console.log("Building Built")
      }
    })
  }
}
