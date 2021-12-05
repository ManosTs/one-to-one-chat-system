import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class HomePageService {
  private url = "http://localhost:8080/home"
  constructor(private httpHomePage: HttpClient) { }


  public verifyAccess() : Observable<any>{
    return this.httpHomePage.get(this.url,{
      observe: 'response',
      responseType: "text" ,
      withCredentials: true
    })
  }
}
