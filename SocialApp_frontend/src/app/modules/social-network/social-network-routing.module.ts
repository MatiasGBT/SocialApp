import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ConfigComponent } from './pages/config/config.component';
import { IndexComponent } from './pages/index/index.component';
import { NewpostComponent } from './pages/newpost/newpost.component';
import { NotifComponent } from './pages/notif/notif.component';
import { EditProfileComponent } from './pages/profile/edit-profile/edit-profile.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { SearchUsersComponent } from './pages/search-users/search-users.component';
import { FullPostComponent } from './pages/full-post/full-post.component';
import { FriendsComponent } from './pages/profile/users-list/users-list.component';
import { CommentPageComponent } from './pages/comment-page/comment-page.component';
import { ChatComponent } from './pages/profile/chat/chat.component';
import { CallComponent } from './pages/profile/call/call.component';
import { LikedPostsComponent } from './pages/profile/liked-posts/liked-posts.component';
import { AuthGuard } from '../../guards/auth.guard';

const routes: Routes = [
  {path: '', redirectTo: 'index/friends', pathMatch: 'full'},
  {path: 'index', redirectTo: 'index/friends', pathMatch: 'full'},
  {path: 'index/:page', component: IndexComponent, canActivate: [AuthGuard]},
  {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard]},
  {path: 'profile/edit', component: EditProfileComponent, canActivate: [AuthGuard]},
  {path: 'profile/liked', component: LikedPostsComponent, canActivate: [AuthGuard]},
  {path: 'profile/:id', component: ProfileComponent, canActivate: [AuthGuard]},
  {path: 'profile/lists/:page/:id', component: FriendsComponent, canActivate: [AuthGuard]},
  {path: 'profile/chat/:id', component: ChatComponent, canActivate: [AuthGuard]},
  {path: 'profile/call/:id', component: CallComponent, canActivate: [AuthGuard]},
  {path: 'profile/call/:id/:peerId', component: CallComponent, canActivate: [AuthGuard]},
  {path: 'search/:name', component: SearchUsersComponent, canActivate: [AuthGuard]},
  {path: 'notif', component: NotifComponent, canActivate: [AuthGuard]},
  {path: 'config', component: ConfigComponent, canActivate: [AuthGuard]},
  {path: 'newpost', component: NewpostComponent, canActivate: [AuthGuard]},
  {path: 'post', component: FullPostComponent, canActivate: [AuthGuard]},
  {path: 'post/:id', component: FullPostComponent, canActivate: [AuthGuard]},
  {path: 'comment/:id', component: CommentPageComponent, canActivate: [AuthGuard]}
]

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SocialNetworkRoutingModule { }
