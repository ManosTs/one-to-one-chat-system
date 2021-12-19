import {Component, Directive, Input, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {HttpClientService} from "../../services/client/http-client.service";
import {FileUploadService} from "../../services/client/file-upload.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ClientSettingsPageService} from "../../services/page/clientSettingsPage/client-settings-page.service";


@Component({
  selector: 'app-client-account-settings',
  templateUrl: './client-account-settings.component.html',
  styleUrls: ['./client-account-settings.component.sass']
})

export class ClientAccountSettingsComponent implements OnInit {

  public loading:boolean = false;
  public message: string = '';
  public ProfPicFileError: string = '';
  public prof_modal_opened: boolean = false;

  public backpic_modal_opened: boolean = false;

  public getNewProfPicData:any;

  public getNewBackpicData:any;

  isFileChosen:any;

  private decodedToken : any
  public picUrl: any;

  public backpicUrl = "https://images.pexels.com/photos/1301585/pexels-photo-1301585.jpeg?cs=srgb&dl=pexels-suzy-hazelwood-1301585.jpg&fm=jpg"

  public fullName: string = "";
  private clientID: any;
  private email: string = "";
  private token: any;

  constructor(private httpFile:FileUploadService,
              private cookieService : CookieService,
              private httpClientService: HttpClientService,
              private route: ActivatedRoute,
              private router: Router,
              private httpClientSettings: ClientSettingsPageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.clientID = params['id'];
    });
    this.httpClientSettings.verifyAccess().subscribe(
      res =>{
        this.token = res.body;
        this.getClaimsFromToken(this.token);
      },error => {
        if(error){
          this.router.navigate(["/login"])
          console.clear();
        }
      })
  }



  getClientProfilePhoto(clientID:any){
    this.httpFile.getFile(clientID).subscribe(
      data =>{
        this.picUrl = "data:image/png;base64,"+data.body;
      },
      error => {
        console.log(error)
      }
    )
  }

  openProfModal() {
    this.prof_modal_opened = !this.prof_modal_opened
  }

  openBackpicModal(){
    this.backpic_modal_opened = !this.backpic_modal_opened
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
      this.ProfPicFileError = "ONLY IMAGES ARE SUPPORTED!";
      return;
    }

    //read data from file
    reader.readAsDataURL(this.file);
    reader.onload = (_event) => {
      this.getNewProfPicData = reader.result as string
    }
  }

  uploadFile(){
    this.loading = !this.loading
    this.httpFile.uploadFile(this.file, this.clientID).subscribe(
      (event:any) =>{
        if(typeof (event) === "object"){
          this.loading = false
        }
        this.prof_modal_opened = false
        window.location.reload();

      },
      error => {
        this.ProfPicFileError = error.body
        this.loading = false
        console.log(error)
      }
    );
  }

}
