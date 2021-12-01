import {InjectionToken, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginFormComponent } from './authComponets/login-form/login-form.component';
import { RegisterFormComponent } from './authComponets/register-form/register-form.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {ActivatedRouteSnapshot, RouterModule, Routes} from "@angular/router";
import {
  circleFill,
  chat,
  xOctagonFill,
  arrowReturnRight,
  chevronDoubleRight,
  personCircle,
  xSquare,
  person,
  eye,
  eyeSlash,
  envelope,
  key,
  check2,
  list,
  NgxBootstrapIconsModule,
  xCircle
} from 'ngx-bootstrap-icons';
import {FormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { HomePageComponent } from './pageComponents/home-page/home-page.component';
import {AuthGuardService} from "./services/routerGuard/auth-guard.service";
import {AuthService} from "./services/routerGuard/auth.service";
import {JWT_OPTIONS, JwtHelperService} from "@auth0/angular-jwt";
import { ClientAccountSettingsComponent } from './pageComponents/client-account-settings/client-account-settings.component';
import {DatePipe} from "@angular/common";

const icons = {
  eye,
  eyeSlash,
  envelope,
  key,
  check2,
  list,
  person,
  xSquare,
  personCircle,
  chevronDoubleRight,
  arrowReturnRight,
  xOctagonFill,
  chat,
  circleFill,
  xCircle
};

const appRoutes: Routes = [
  {
    path:"",
    redirectTo:"/login",
    pathMatch:"full"
  },
  {
    path: 'register',
    component: RegisterFormComponent
  },
  {
    path: 'login',
    component: LoginFormComponent
  },
  {
    path: 'logout',
    redirectTo: "/login",
    pathMatch:"full"
  },

  {
    path: 'home',
    component: HomePageComponent,
    canActivate: [AuthGuardService]
  },
  {
    path: 'settings',
    component: ClientAccountSettingsComponent,
    canActivate: [AuthGuardService]
  }

];

@NgModule({
  declarations: [
    AppComponent,
    LoginFormComponent,
    RegisterFormComponent,
    HomePageComponent,
    ClientAccountSettingsComponent
  ],
  imports: [
    RouterModule.forRoot(
      appRoutes,
      {enableTracing: false} // <-- debugging purposes only
    ),
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    NgxBootstrapIconsModule.pick(icons),
    FormsModule,
    HttpClientModule,
  ],
  providers: [AuthGuardService,AuthService,
    { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    JwtHelperService,
    DatePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
