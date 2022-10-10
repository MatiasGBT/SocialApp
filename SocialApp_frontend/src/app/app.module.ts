import { HttpClient, HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FooterComponent } from './pages/layout/footer/footer.component';
import { NavbarComponent } from './pages/layout/navbar/navbar.component';
import { IndexComponent } from './pages/index/index.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NotifComponent } from './pages/notif/notif.component';
import { ConfigComponent } from './pages/config/config.component';
import { NewpostComponent } from './pages/newpost/newpost.component';
import { NgxDropzoneModule } from 'ngx-dropzone';
import { EditProfileComponent } from './pages/profile/edit-profile/edit-profile.component';
import { NgxPhotoEditorModule } from 'ngx-photo-editor';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { SearchUsersComponent } from './pages/search-users/search-users.component';
import { LanguageInterceptor } from './interceptors/language';
import { PostComponent } from './components/post/post.component';
import { FullPostComponent } from './pages/full-post/full-post.component';
import { UserComponent } from './components/user/user.component';
import { FriendsComponent } from './pages/friends/friends.component';
import { PostsListComponent } from './components/posts-list/posts-list.component';
import { CommentComponent } from './components/comment/comment.component';
import { CommentPageComponent } from './pages/comment-page/comment-page.component';
import { ChatComponent } from './pages/profile/chat/chat.component';
import { MessageComponent } from './components/message/message.component';

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'socialapprealm',
        clientId: 'angularfront'
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html'
      }
    });
}

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    NavbarComponent,
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
    MessageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    KeycloakAngularModule,
    NgxDropzoneModule,
    TranslateModule.forRoot({
      defaultLanguage: 'en',
      loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient]
      }
    }),
    NgxPhotoEditorModule,
    BrowserAnimationsModule,
    MatAutocompleteModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    },
    {provide: HTTP_INTERCEPTORS, useClass: LanguageInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
