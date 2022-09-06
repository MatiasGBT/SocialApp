import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConfigComponent } from './pages/config/config.component';
import { IndexComponent } from './pages/index/index.component';
import { NewpostComponent } from './pages/newpost/newpost.component';
import { NotifComponent } from './pages/notif/notif.component';
import { EditProfileComponent } from './pages/profile/edit-profile/edit-profile.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { SearchUsersComponent } from './pages/search-users/search-users.component';
import { AuthGuard } from './guards/auth.guard';
import { FullPostComponent } from './pages/full-post/full-post.component';

const routes: Routes = [
  {path: '', redirectTo: 'index', pathMatch: 'full'},
  {path: 'index', component: IndexComponent, canActivate: [AuthGuard]},
  {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard]},
  {path: 'profile/edit', component: EditProfileComponent},
  {path: 'profile/:id', component: ProfileComponent, canActivate: [AuthGuard]},
  {path: 'search/:name', component: SearchUsersComponent, canActivate: [AuthGuard]},
  {path: 'notif', component: NotifComponent, canActivate: [AuthGuard]},
  {path: 'config', component: ConfigComponent, canActivate: [AuthGuard]},
  {path: 'newpost', component: NewpostComponent, canActivate: [AuthGuard]},
  {path: 'post/:id', component: FullPostComponent, canActivate: [AuthGuard]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
