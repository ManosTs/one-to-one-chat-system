import { Injectable } from '@angular/core';
import {AuthService} from "./auth.service";
import {ActivatedRoute, ActivatedRouteSnapshot, Router, RouterStateSnapshot} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService {

  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {

    if (!this.auth.isAuthenticated) {
      this.router.navigate(['login']);
      return false;
    }
    return true;
  }
}
