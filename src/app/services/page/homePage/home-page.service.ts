import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class HomePageService {
  private url = "http://localhost:8080/home"
  constructor(private httpHomePage: HttpClient) { }


  public getAccessToHomePage(token:string) : Observable<any>{
    return this.httpHomePage.get(this.url,{
      params: {
        token: token
      },
      observe: 'response'
    })
  }
}
