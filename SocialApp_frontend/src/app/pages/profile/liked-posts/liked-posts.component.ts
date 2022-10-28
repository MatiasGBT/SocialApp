import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-liked-posts',
  templateUrl: './liked-posts.component.html',
  styleUrls: ['./liked-posts.component.css']
})
export class LikedPostsComponent implements OnInit {
  public keycloakUser: User;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getKeycloakUser().subscribe(keycloakUser => this.keycloakUser = keycloakUser);
  }

}