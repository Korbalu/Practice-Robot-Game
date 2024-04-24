import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  isVisible: boolean = false;
  role:string = '';

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit() {

  }

  logout() {
    this.userService.logout().subscribe({
      next: () => {
      },
      error: err => {
        console.log(err);
      },
      complete: () => {
        console.log("Logout Complete");
        localStorage.removeItem('token');
        this.router.navigate(["login"])
      }
    })
  }

  login() {
    this.router.navigate(["login"])
  }

  roler() {
    this.userService.roleReceiver().subscribe({
      next: value => {
        this.role = value.role
        if (this.role == "ROLE_SUPERUSER") {
          this.isVisible = true;
        }
      },
      error: err => {
        console.log(err);
      },
      complete: () => {
      }
    })
  }

  protected readonly localStorage = localStorage;
}
