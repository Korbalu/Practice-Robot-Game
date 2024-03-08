import { Component } from '@angular/core';
import {UnitListModel} from "../../models/unit-list-model";
import {CityService} from "../../services/city.service";
import {MarketService} from "../../services/market.service";
import {LegionCreationModel} from "../../models/legion-creation-model";

@Component({
  selector: 'app-market',
  templateUrl: './market.component.html',
  styleUrls: ['./market.component.css']
})
export class MarketComponent {

  units: Array<UnitListModel> = [];
  myVault: number = 0;

  constructor(private cityService: CityService, private marketService:MarketService) {

  }

  ngOnInit(){
    this.marketService.marketUnitList().subscribe({
      next: (data) => {
        this.units = data;
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
    this.cityService.userDetailer().subscribe({
      next: (data) => {
        this.myVault = data.vault;
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
  }

  unitBuyer(quantityP: number, unitP:string){
    let legion: LegionCreationModel = {unit : unitP, quantity: quantityP}
    this.marketService.buyLegion(legion).subscribe({
      next: () => {
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
        this.ngOnInit();
      }
    })
  }

}
