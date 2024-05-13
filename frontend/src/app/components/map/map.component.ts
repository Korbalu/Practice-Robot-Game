import {Component} from "@angular/core";
import {CityService} from "../../services/city.service";
import {CityListModel} from "../../models/city-list-model";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent {

  map: string[][] = [];
  cities: CityListModel[] = [];

  constructor(private cityService: CityService) {

  }

  ngOnInit() {
    this.cityService.cityLister().subscribe({
      next: (data) => {
        this.cities = data;
        for (const city of this.cities) {
        }
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
        this.mapper()
      }
    })
  }

  mapper() {
    for (let i = 0; i < 21; i++) {
      this.map[i] = [];
      for (let j = 0; j < 21; j++) {
        this.map[i][j] = "o";
      }
    }
    for (const city of this.cities) {
      const {x, y} = city;
      this.map[y][x] = "x";
    }
  }


  getImage(cell: string): string {
    if (cell === "x") {
      return "assets/castleX.jpg";
    } else {
      return "assets/castleO.jpg";
    }
  }
}
