import {Component, Directive, OnInit} from '@angular/core';
import {JwtHelperService} from "@auth0/angular-jwt";
import {CookieService} from "ngx-cookie-service";
import {HttpClient} from "@angular/common/http";
import {HttpClientService} from "../../services/client/http-client.service";
import {FileUploadService} from "../../services/client/file-upload.service";



@Component({
  selector: 'app-client-account-settings',
  templateUrl: './client-account-settings.component.html',
  styleUrls: ['./client-account-settings.component.sass']
})
export class ClientAccountSettingsComponent implements OnInit {

  public first_name: string = "";
  public last_name: string = "";
  public email:string = "";

  private decodedToken : any = this.jwtHelper.decodeToken(this.cookieService.get("token"))
  public url: any;

  constructor(private httpFile:FileUploadService,private jwtHelper: JwtHelperService,private cookieService : CookieService,private http:HttpClientService) { }

  ngOnInit(): void {
    this.first_name = `${(this.decodedToken)['first_name']}`
    this.last_name = `${(this.decodedToken)['last_name']}`
    this.email = `${(this.decodedToken)['sub']}`
    this.getClientProfile()
  }

  getClientProfile(){
    this.httpFile.getFile((this.decodedToken)['profile_picture']).subscribe(
      data =>{
        this.url = "data:image/png;base64," + data.headers.get("File-Data");
      },
      error => {
        console.log(error)
      }
    )
  }

}
