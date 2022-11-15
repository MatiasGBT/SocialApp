import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IndexComponent } from './pages/index/index.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { NotifComponent } from './pages/notif/notif.component';
import { ConfigComponent } from './pages/config/config.component';
import { NewpostComponent } from './pages/newpost/newpost.component';
import { NgxDropzoneModule } from 'ngx-dropzone';
import { EditProfileComponent } from './pages/profile/edit-profile/edit-profile.component';
import { NgxPhotoEditorModule } from 'ngx-photo-editor';
import { SearchUsersComponent } from './pages/search-users/search-users.component';
import { PostComponent } from './components/post/post.component';
import { FullPostComponent } from './pages/full-post/full-post.component';
import { UserComponent } from './components/user/user.component';
import { FriendsComponent } from './pages/profile/users-list/users-list.component';
import { PostsListComponent } from './components/posts-list/posts-list.component';
import { CommentComponent } from './components/comment/comment.component';
import { CommentPageComponent } from './pages/comment-page/comment-page.component';
import { ChatComponent } from './pages/profile/chat/chat.component';
import { MessageComponent } from './components/message/message.component';
import { CallComponent } from './pages/profile/call/call.component';
import { LikedPostsComponent } from './pages/profile/liked-posts/liked-posts.component';
import { SharedModule } from '../shared/shared.module';
import { SocialNetworkRoutingModule } from './social-network-routing.module';

@NgModule({
  declarations: [
    IndexComponent,
    ProfileComponent,
    NotifComponent,
    ConfigComponent,
    NewpostComponent,
    EditProfileComponent,
    SearchUsersComponent,
    PostComponent,
    FullPostComponent,
    UserComponent,
    FriendsComponent,
    PostsListComponent,
    CommentComponent,
    CommentPageComponent,
    ChatComponent,
    MessageComponent,
    CallComponent,
    LikedPostsComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    NgxDropzoneModule,
    NgxPhotoEditorModule,
    SocialNetworkRoutingModule
  ]
})
export class SocialNetworkModule { }