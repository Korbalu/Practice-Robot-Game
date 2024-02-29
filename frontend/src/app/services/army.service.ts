import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {LegionCreationModel} from "../models/legion-creation-model";
import {LegionListModel} from "../models/legion-list-model";
import {UnitListModel} from "../models/unit-list-model";

const BASE_URL = "http://localhost:8080/api/army";

@Injectable({
  providedIn: 'root'
})
export class ArmyService {
  token!: string | null;

  constructor(private http: HttpClient) {

  }

  createLegion(legion: LegionCreationModel): Observable<any> {
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.put(BASE_URL, legion, {headers});
  }

  legionList(): Observable<any> {
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.get<Array<LegionListModel>>(BASE_URL, {headers})
  }

  unitList():Observable<any>{
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.get<Array<UnitListModel>>(BASE_URL + "/units", {headers})
  }
}
