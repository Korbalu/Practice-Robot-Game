import { Component } from '@angular/core';
import {MissionService} from "../../services/mission.service";
import {MissionExpeditionModel} from "../../models/mission-expedition-model";

@Component({
  selector: 'app-mission',
  templateUrl: './mission.component.html',
  styleUrls: ['./mission.component.css']
})
export class MissionComponent {
  constructor(private missionService: MissionService) {
  }

  expedition(quantity: number){
    let bots: MissionExpeditionModel = {quantity: quantity}
    this.missionService.expeditionMission(bots).subscribe({
      next: () => {
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
  }

  protected readonly Number = Number;
}
