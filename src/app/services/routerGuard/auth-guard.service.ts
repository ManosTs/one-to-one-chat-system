import { Injectable } from '@angular/core';
import {AuthService} from "./auth.service";
import {ActivatedRoute, ActivatedRouteSnapshot, Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService {

  constructor(private auth: AuthService, private router: Router) {}
  private token_param:any;


  canActivate(route: ActivatedRouteSnapshot): boolean {
    this.token_param = route.queryParams.access_token

    if (!this.auth.isAuthenticated(this.token_param)) {
      this.router.navigate(['login']);
      return false;
    }
    return true;
  }
}
