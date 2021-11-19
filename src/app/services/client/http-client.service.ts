import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";


export class LoginUser {
  constructor(
    public email: string,
    public password: string
  ) {
  }
}

export class RegisterUser {
  constructor(
    public email: string,
    public firstName: string,
    public lastName: string,
    public password: string
  ) {
  }
}

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {

  private url = new URL("http://localhost:8080/clients/")

  constructor(private httpClient: HttpClient) {
  }

  public registerUser(user: RegisterUser){
    return this.httpClient.post<RegisterUser>(this.url + 'register',user);
  }

  public loginUser(user: LoginUser): Observable<any> {
    return this.httpClient.post(this.url + 'login', user, {observe: 'response'})
  }

  public logoutUser(id: string): Observable<any>{
    return this.httpClient.get(this.url + 'logout/' + id, {observe: 'response'})
  }

  public isUserActive(id: string): Observable<any>{
    return this.httpClient.get(this.url + 'isActive/' + id, {observe: 'response'})
  }

  public lastSeen(id: string): Observable<any>{
    return  this.httpClient.get(this.url + id + "/lastSeen" ,{observe: 'response'})
  }


  public lastLogon(id: string): Observable<any>{
    return  this.httpClient.get(this.url +id+"/lastLogon" ,{observe: 'response'})
  }

  public changeStatus(id: string, status:boolean): Observable<any> {
    return this.httpClient.get(this.url + id+'/status='+ status, {observe: 'response'})
  }

}