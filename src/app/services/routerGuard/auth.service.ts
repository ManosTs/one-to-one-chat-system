import {Injectable} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {HttpClientService} from "../client/http-client.service";
import {HomePageService} from "../page/homePage/home-page.service";
import {first} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private enc_token: any;
  isAuth: any;

  constructor(private httpHome: HomePageService,
              private cookieHandler: CookieService,
              private httpClient: HttpClientService) {
  }

  public isAuthenticated(): any {
    return true
  }
}
