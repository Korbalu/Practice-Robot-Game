import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

const BASE_URL = "http://localhost:8080/api/city";
@Injectable({
  providedIn: 'root'
})
export class CityService {
  token!: string | null;
  constructor(private http: HttpClient) {

  }
}
