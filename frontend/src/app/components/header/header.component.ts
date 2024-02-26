import {Component} from '@angular/core';
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {


  constructor(private userService: UserService, private router: Router) {
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

  protected readonly localStorage = localStorage;
}
