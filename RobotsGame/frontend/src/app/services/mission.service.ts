import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {MissionExpeditionModel} from "../models/mission-expedition-model";

const BASE_URL = "http://localhost:8080/api/mission";

@Injectable({
  providedIn: 'root'
})

export class MissionService {
  token!: string | null;

  constructor(private http: HttpClient) {

  }

  expeditionMission(quantity: MissionExpeditionModel): Observable<any> {
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.post(BASE_URL + "/expedition", quantity,{headers})
  }
}
