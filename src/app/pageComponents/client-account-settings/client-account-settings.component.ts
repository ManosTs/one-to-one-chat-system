import {Component, Directive, Input, OnInit} from '@angular/core';
import {JwtHelperService} from "@auth0/angular-jwt";
import {CookieService} from "ngx-cookie-service";
import {HttpClient} from "@angular/common/http";
import {HttpClientService} from "../../services/client/http-client.service";
import {FileUploadService} from "../../services/client/file-upload.service";
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ActivatedRoute, Router} from "@angular/router";
import {query} from "@angular/animations";
import {HomePageComponent} from "../home-page/home-page.component";


@Component({
  selector: 'app-client-account-settings',
  templateUrl: './client-account-settings.component.html',
  styleUrls: ['./client-account-settings.component.sass']
})

export class ClientAccountSettingsComponent implements OnInit {

  public loading:boolean = false;
  public message: string = '';
  public fileError: string = '';
  public opened: boolean = false;

  public getNewImageData:any

  isFileChosen:any;

  private decodedToken : any
  public url: any;

  public fullName: string = "";
  private clientID: any;
  private email: string = "";
  private token: any;

  constructor(private httpFile:FileUploadService,
              private jwtHelper: JwtHelperService,private cookieService : CookieService,
              private httpClientService: HttpClientService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.clientID = params['id'];
      this.token = params['access_token']
    });
    this.getClaimsFromToken(this.token);
    this.url = "https://az-pe.com/wp-content/uploads/2018/05/blank-profile-picture-973460_960_720-200x200.png"
  }



  getClientProfilePhoto(clientID:any){
    this.httpFile.getFile(clientID).subscribe(
      data =>{
        this.url = "data:image/png;base64," + data.headers.get("File-Data");
      },
      error => {
        console.log(error)
      }
    )
  }

  open() {
    this.opened = !this.opened
  }

  file:any

  getClaimsFromToken(encryptedToken:any){
    this.httpClientService.getClaimsFromToken(encryptedToken).subscribe(
      data => {
        this.decodedToken = data.body;
        this.email = `${(this.decodedToken)["claims"]['sub']}`
        this.fullName = `${(this.decodedToken)["claims"]['first_name']}` + " " + `${(this.decodedToken)["claims"]['last_name']}`

        this.getClientProfilePhoto(this.clientID)
      },
      error => {
        console.log(error);
      }
    )
  }

  onFileChanged(event: any) {
    this.file = event.target.files[0]
    const reader = new FileReader();

    if (this.file.length === 0) {
      this.isFileChosen = false;
      return;
    }

    // this.isFileChosen = true
    const mimeType = this.file.type;
    if (mimeType.match(/image\/*/) == null) {
      this.fileError = "ONLY IMAGES ARE SUPPORTED!";
      return;
    }

    //read data from file
    reader.readAsDataURL(this.file);
    reader.onload = (_event) => {
      this.getNewImageData = reader.result as string
    }
  }

  uploadFile(){
    this.loading = !this.loading
    this.httpFile.uploadFile(this.file, this.clientID).subscribe(
      (event:any) =>{
        if(typeof (event) === "object"){
          this.loading = false
          this.opened = false
        }
      },
      error => {
        console.log(error)
      }
    );
  }

}
