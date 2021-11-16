import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class HomePageService {
  private url = "http://localhost:8080/home/"
  constructor(private httpHomePage: HttpClient) { }


  public getSettings(id:String) : Observable<any>{
    return this.httpHomePage.get<String>(this.url + "settings/" + id );
  }

}
