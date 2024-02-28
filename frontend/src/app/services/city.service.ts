import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {CityCreationModel} from "../models/city-creation-model";
import {RaceNameModel} from "../models/race-name-model";

const BASE_URL = "http://localhost:8080/api/city";
@Injectable({
  providedIn: 'root'
})
export class CityService {
  token!: string | null;
  constructor(private http: HttpClient) {

  }

  cityCreator(city: CityCreationModel):Observable<any>{
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.post(BASE_URL + "/create", city, {headers})
  }

  raceLister():Observable<Array<RaceNameModel>>{
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.get<Array<RaceNameModel>>(BASE_URL + "/races", {headers});
  }
}
