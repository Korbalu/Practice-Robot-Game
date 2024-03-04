import {Component} from '@angular/core';
import {CityService} from "../../services/city.service";
import {CityListModel} from "../../models/city-list-model";
import {ArmyService} from "../../services/army.service";
import {BattleModel} from "../../models/battle-model";

@Component({
  selector: 'app-city-list',
  templateUrl: './city-list.component.html',
  styleUrls: ['./city-list.component.css']
})
export class CityListComponent {

  cities: Array<CityListModel> = [];

  constructor(private cityService: CityService, private armyService: ArmyService) {

  }

  ngOnInit() {
    this.cityService.cityLister().subscribe({
      next: (data) => {
        this.cities = data;
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
  }

  toBattle(enemy: string) {
    let enemyName: BattleModel = {enemyName: enemy}
    this.armyService.battleer(enemyName).subscribe({
      next: () => {
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
        console.log("Battle is over.")
      }
    })
  }
}
