import {Component} from '@angular/core';
import {UnitListModel} from "../../models/unit-list-model";
import {CityService} from "../../services/city.service";
import {MarketService} from "../../services/market.service";
import {LegionCreationModel} from "../../models/legion-creation-model";
import {LegionNameQuantityListModel} from "../../models/legion-name-quantity-list-model";

@Component({
  selector: 'app-market',
  templateUrl: './market.component.html',
  styleUrls: ['./market.component.css']
})
export class MarketComponent {

  units: Array<UnitListModel> = [];
  myVault: number = 0;
  legions: Array<LegionNameQuantityListModel> = [];

  constructor(private cityService: CityService, private marketService: MarketService) {

  }

  ngOnInit() {
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
    this.getQuantityForUnit();
  }

  unitBuyer(quantityP: number, unitP: string) {
    let legion: LegionCreationModel = {unit: unitP, quantity: quantityP}
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

  unitSeller(quantityP: number, unitP: string) {
    let legion: LegionCreationModel = {unit: unitP, quantity: quantityP}
    this.marketService.sellLegion(legion).subscribe({
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

  getQuantityForUnit(){
    this.marketService.ownUnitsLister().subscribe({
      next: (data) => {
        this.legions = data;
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
  }

  getUnitQuantityByLegionName(legionName: string): number | undefined {
    const foundLegion = this.legions.find(legion => legion.legionName === legionName);
    return foundLegion ? foundLegion.unitQuantity : 0;
  }

}
