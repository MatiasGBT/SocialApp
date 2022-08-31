import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConfigComponent } from './components/config/config.component';
import { IndexComponent } from './components/index/index.component';
import { NewpostComponent } from './components/newpost/newpost.component';
import { NotifComponent } from './components/notif/notif.component';
import { EditProfileComponent } from './components/profile/edit-profile/edit-profile.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SearchUsersComponent } from './components/search-users/search-users.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

const routes: Routes = [
  {path: '', redirectTo: 'index', pathMatch: 'full'},
  {path: 'index', component: IndexComponent, canActivate: [AuthGuard], data: {role: 'user'}},
  {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard], data: {role: 'user'}},
  {path: 'profile/edit', component: EditProfileComponent},
  {path: 'profile/:id', component: ProfileComponent, canActivate: [AuthGuard], data: {role: 'user'}},
  {path: 'search/:name', component: SearchUsersComponent, canActivate: [AuthGuard], data: {role: 'user'}},
  {path: 'notif', component: NotifComponent, canActivate: [AuthGuard, RoleGuard], data: {role: 'user'}},
  {path: 'config', component: ConfigComponent, canActivate: [AuthGuard], data: {role: 'user'}},
  {path: 'newpost', component: NewpostComponent, canActivate: [AuthGuard], data: {role: 'user'}}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
