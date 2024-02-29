import { Component } from '@angular/core';
import {CityDetailsModel} from "../../models/city-details-model";
import {CityService} from "../../services/city.service";
import {ArmyService} from "../../services/army.service";
import {BuildingListModel} from "../../models/building-list-model";
import {LegionListModel} from "../../models/legion-list-model";

@Component({
  selector: 'app-city',
  templateUrl: './city.component.html',
  styleUrls: ['./city.component.css']
})
export class CityComponent {
  city!: CityDetailsModel
  buildings: Array<BuildingListModel> = [];
  army: Array<LegionListModel> = [];

  constructor(private cityService: CityService, private armyService: ArmyService) {

  }

  ngOnInit(){
    this.cityService.cityDetailer().subscribe({
      next:(data)=>{
        this.city = data;
      },
      error: err => {
        console.log(err)
      },
      complete:()=>{}
    })
    this.cityService.buildingLister().subscribe({
      next:(data)=>{
        this.buildings = data;
      },
      error: err => {
        console.log(err)
      },
      complete:()=>{}
    })
    this.armyService.legionList().subscribe({
      next:(data)=>{
        this.army = data;
      },
      error: err => {
        console.log(err)
      },
      complete:()=>{}
    })
  }
}
