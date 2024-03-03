import { Component } from '@angular/core';
import {CityService} from "../../services/city.service";
import {CityListModel} from "../../models/city-list-model";

@Component({
  selector: 'app-city-list',
  templateUrl: './city-list.component.html',
  styleUrls: ['./city-list.component.css']
})
export class CityListComponent {

  cities:Array<CityListModel> = [];

  constructor(private cityService: CityService) {

  }

  ngOnInit(){
    this.cityService.cityLister().subscribe({
      next:(data)=>{
        this.cities = data;
      },
      error:err => {
        console.log(err)
      },
      complete:() =>{
      }
    })
  }
}
