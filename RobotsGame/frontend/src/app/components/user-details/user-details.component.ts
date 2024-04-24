import {Component, OnInit} from '@angular/core';
import {UserListModel} from "../../models/user-list-model";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.css']
})
export class UserDetailsComponent implements OnInit {
  users: Array<UserListModel> = [];


  constructor(private userService: UserService) {
  }

  ngOnInit() {
    this.userService.userLister().subscribe({
      next: value => {
        this.users = value;
      },
      error: err => {
        console.log(err);
      },
      complete: () => {
        console.log("Users Listed!")
      }
    })
  }
}
