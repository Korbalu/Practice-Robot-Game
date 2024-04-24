import {Component} from '@angular/core';
import {CityService} from "../../services/city.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Router} from "@angular/router";
import {RaceNameModel} from "../../models/race-name-model";
import {CityCreationModel} from "../../models/city-creation-model";

@Component({
  selector: 'app-city-creation',
  templateUrl: './city-creation.component.html',
  styleUrls: ['./city-creation.component.css']
})
export class CityCreationComponent {
  cityForm: FormGroup;
  races: Array<RaceNameModel> = [];

  constructor(private cityService: CityService, private formBuilder: FormBuilder, private router: Router) {
    this.cityForm = this.formBuilder.group({
      'name': [''],
      'race': ['']
    })
    this.cityService.raceLister().subscribe({
      next: (data) =>{
        this.races = data
      },
      error: err => {
        console.log(err)
      },
      complete:() =>{}
    })

  }

  cityCreation() {
    let data: CityCreationModel = {race: '', name: ''};
    data.race = this.cityForm.value.race;
    data.name = this.cityForm.value.name;
    this.cityService.cityCreator(data).subscribe({
      next:()=>{},
      error:err => {
        console.log(err)
      },
      complete:()=>{
        this.router.navigate(["city"])
      }
    })
  }
}
