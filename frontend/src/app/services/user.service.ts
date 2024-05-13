import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {RegisterRequestModel} from "../models/register-request-model";
import {LoginRequestModel} from "../models/login-request-model";
import {UserListModel} from "../models/user-list-model";
import {RoleSenderModel} from "../models/role-sender-model";
import {LogListModel} from "../models/log-list-model";

const BASE_URL = "http://localhost:8080/api";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  token!: string | null;

  constructor(private http: HttpClient) {

  }

  login(auth: LoginRequestModel): Observable<any> {
    return this.http.post(BASE_URL + "/users/login", auth);
  }

  register(reg: RegisterRequestModel): Observable<any> {
    return this.http.post(BASE_URL + "/users/reg", reg)
  }

  logout(): Observable<any> {
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.post(BASE_URL + "/users/logout", {headers});
  }

  userLister(): Observable<any> {
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.get<Array<UserListModel>>(BASE_URL + "/userlist", {headers});
  }
  roleReceiver():Observable<RoleSenderModel>{
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.get<RoleSenderModel>(BASE_URL + "/userrole", {headers})
  }
  loglister():Observable<any>{
    this.token = localStorage.getItem("token");
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.get<Array<LogListModel>>(BASE_URL + "/logs", {headers})
  }
}
