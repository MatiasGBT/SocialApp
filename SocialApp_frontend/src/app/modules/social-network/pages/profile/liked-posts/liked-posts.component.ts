import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-liked-posts',
  templateUrl: './liked-posts.component.html',
  styleUrls: ['./liked-posts.component.css']
})
export class LikedPostsComponent implements OnInit {
  public keycloakUser: User;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.keycloakUser = this.authService.keycloakUser;
  }

}