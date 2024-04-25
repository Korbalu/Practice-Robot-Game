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

  login() {
    const data: LoginRequestModel = this.loginForm.value
    data.email = this.loginForm.value.email2;
    data.password = this.loginForm.value.password2;
    this.userService.login(data).subscribe({
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
    console.log(this.registerForm.value)
    console.log(typeof this.registerForm.value)
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
