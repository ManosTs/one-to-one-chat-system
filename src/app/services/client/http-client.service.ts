import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
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
    public password: string,
    public confirmPassword: string
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

  public registerUser(user: RegisterUser) {
    return this.httpClient.post<RegisterUser>(this.url + 'register', user);
  }

  public loginUser(user: LoginUser): Observable<any> {
    return this.httpClient.post(this.url + 'login', user, {observe: 'response',responseType:'text', withCredentials:true})
  }

  public logoutUser(id: string): Observable<any> {
    return this.httpClient.get(this.url + 'logout', {
      params: {
        id: id
      },
      observe: 'response'
    })
  }

  public isUserActive(id: string): Observable<any> {
    return this.httpClient.get(this.url + 'isActive/' + id, {observe: 'response'})
  }

  public lastSeen(id: string): Observable<any> {
    return this.httpClient.get(this.url + "lastSeen",{
      params: {
        id: id
      },
      observe: 'response'
    })
  }


  public lastLogon(id: string): Observable<any> {
    return this.httpClient.get(this.url + id + "/lastLogon", {observe: 'response'})
  }

  public changeStatus(id: string, status: boolean): Observable<any> {
    return this.httpClient.get(this.url + id + '/status=' + status, {observe: 'response'})
  }

  public getAllClients(): Observable<any> {
    return this.httpClient.get(this.url + "all", {observe: 'response'})
  }

  public getClaimsFromToken(encryptedToken:any): Observable<any>{
    return this.httpClient.get(this.url + "encrypted-token",{
      params: {
        access_token: encryptedToken
      },
      observe: 'response'
    })
  }

}
