import { Injectable } from '@angular/core';
import {JwtHelperService} from "@auth0/angular-jwt";
import {CookieService} from "ngx-cookie-service";
import {ActivatedRoute} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private jwtHelper: JwtHelperService) {}

  private access_token: any ;

  public isAuthenticated(param_token:any): boolean {
    this.access_token = param_token;

    return !this.jwtHelper.isTokenExpired(this.access_token);
  }
}
