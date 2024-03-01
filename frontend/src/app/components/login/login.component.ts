import {Component} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {LoginRequestModel} from "../../models/login-request-model";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  registerForm!: FormGroup;
  loginForm!: FormGroup;
  datas:LoginRequestModel= {email: '', password: ''};

  constructor(private formBuilder: FormBuilder, private router: Router, private userService: UserService) {
    this.registerForm = this.formBuilder.group({
      "name": [],
      "email": [],
      "password": []
    })
    this.loginForm = this.formBuilder.group({
      "email2": [],
      "password2": []
    })
  }

  login(name: string, pass: string) {
    // console.log(this.loginForm.value)
    // let data:LoginRequestModel = this.loginForm.value
    this.datas.password = pass;
    this.datas.email = name;
    this.userService.login(this.datas).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
      },
      error: err => {
        console.log(err);
      },
      complete: () => {
        this.router.navigate(["city"])
      }
    })
  }

  reg() {
    this.userService.register(this.registerForm.value).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
        this.router.navigate(["city-creation"])
      }
    })
  }

}
