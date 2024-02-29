import { Component } from '@angular/core';
import {CityDetailsModel} from "../../models/city-details-model";
import {CityService} from "../../services/city.service";

@Component({
  selector: 'app-city',
  templateUrl: './city.component.html',
  styleUrls: ['./city.component.css']
})
export class CityComponent {
  city!: CityDetailsModel

  constructor(private cityService: CityService) {

  }

  ngOnInit(){
    this.cityService.cityDeailer().subscribe({
      next:(data)=>{
        this.city = data;
      },
      error: err => {
        console.log(err)
      },
      complete:()=>{}
    })
  }
}
