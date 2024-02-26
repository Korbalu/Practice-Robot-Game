import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {RegisterRequestModel} from "../models/register-request-model";
import {LoginRequestModel} from "../models/login-request-model";

const BASE_URL = "http://localhost:8080" + "/api/users";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  token!:string;

  constructor(private http: HttpClient) {

  }

  login(auth: LoginRequestModel): Observable<any> {
    return this.http.post(BASE_URL + "/login", auth);
  }

  register(reg: RegisterRequestModel): Observable<any> {
    return this.http.post(BASE_URL + "/reg", reg)
  }

  logout(): Observable<any> {
    // @ts-ignore
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);

    return this.http.post(BASE_URL + "/logout", {headers});
  }

}
