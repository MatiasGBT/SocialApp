import { Component, OnInit } from '@angular/core';
import { PostService } from 'src/app/services/post.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css', '../../../assets/index_profile.css']
})
export class IndexComponent implements OnInit {
  public idUser: number;
  public userWantsOldFeed: boolean = false;
  public isLastPage: boolean;

  constructor(private userService: UserService, private postService: PostService) { }

  ngOnInit(): void {
    this.userService.getKeycloakUser().subscribe(user => this.idUser = user.idUser);
    this.postService.isLastFeedPageEmitter.subscribe(isLastPage => this.isLastPage = isLastPage);
  }

  public getOldFeed() {
    this.userWantsOldFeed = true;
    this.isLastPage = false;
  }
}
