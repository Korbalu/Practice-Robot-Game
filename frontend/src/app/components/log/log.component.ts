import {Component} from '@angular/core';
import {UserService} from "../../services/user.service";
import {LogListModel} from "../../models/log-list-model";

@Component({
  selector: 'app-log',
  templateUrl: './log.component.html',
  styleUrls: ['./log.component.css']
})
export class LogComponent {

  logs: Array<LogListModel> = [];

  constructor(private userService: UserService) {

  }

  ngOnInit() {
    this.userService.loglister().subscribe({
      next: (data) => {
        this.logs = data;
        console.log(data);
      },
      error: err => {
        console.log(err)
      },
      complete: () => {
      }
    })
  }
}
