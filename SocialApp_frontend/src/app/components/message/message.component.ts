import { Component, Input, OnInit } from '@angular/core';
import { Message } from 'src/app/models/message';
import { User } from 'src/app/models/user';

@Component({
  selector: 'message-comp',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {
  @Input() public message: Message;
  @Input() public keycloakUser: User;

  constructor() { }

  ngOnInit(): void {
  }

}