import {AfterViewInit, Component, OnInit} from '@angular/core';
import {HomePageService} from "../../services/page/homePage/home-page.service";
import {CookieService} from "ngx-cookie-service";
import {JwtHelperService} from "@auth0/angular-jwt";
import {ActivatedRoute, ActivatedRouteSnapshot, Router} from "@angular/router";
import {FileUploadService} from "../../services/client/file-upload.service";
import {WebSocketService} from "../../services/message/web-socket.service";
import {HttpClientService} from "../../services/client/http-client.service";
import {DatePipe} from "@angular/common";
import {LoginFormComponent} from "../../authComponets/login-form/login-form.component";
import {HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.sass']
})
export class HomePageComponent implements OnInit, AfterViewInit {
  public imageUrl: any;
  public friends: boolean = false;
  public fullName: any;
  public message: any;
  public time: any;
  public isActive: boolean = false;
  public lastLogOn: any;

  public token:any;

  public searchPeople = "";

  colors = [{status: true, color: "green"}, {status: false, color: "grey"}]
  public clientsList:any = [];

  constructor(private httpHomePageService: HomePageService,
              private cookieService: CookieService,
              private jwtHelper: JwtHelperService,
              private router: Router,
              private route: ActivatedRoute,
              private http: FileUploadService,
              public webSocket: WebSocketService,
              private httpClient: HttpClientService,
              private datePipe: DatePipe) {
  }

  //decode Token to retrieve info from user
  private decodedToken: any;

  //---------------------------------------------------------------------------------------//

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params["access_token"];
      this.httpHomePageService.getAccessToHomePage(this.token).subscribe(res => {
        console.log(res)
      },
        error => {
        console.log(error)
        }
      );
      if(this.token === null){
        this.disconnect()
      }
    })
    this.decodedToken = this.jwtHelper.decodeToken(this.token);
    this.router.navigate(['/home'])
    this.connect()

    this.lastLogon()

    this.isUserActive()

    this.fullName = (this.decodedToken)["first_name"] + " " + (this.decodedToken)["last_name"]

    this.imageUrl = "https://az-pe.com/wp-content/uploads/2018/05/blank-profile-picture-973460_960_720-200x200.png"

    this.getClientProfilePhoto()

    console.log(this.getAllClientsByFirstName())
  }

  changeColorOnStatus(status: any) {
    return this.colors.filter(item => item.status === status)[0]?.color;
  }

  ngAfterViewInit(): void {

  }

  isUserActive() {
    this.httpClient.isUserActive((this.decodedToken)["client_id"]).subscribe(
      data => {
        this.isActive = data.body;
      },
      error => {
        console.log(error)
      }
    );
  }

  changeStatus(status: boolean) {
    this.httpClient.changeStatus((this.decodedToken)["client_id"], status).subscribe(
      data => {
        this.isActive = data.body
      },
      error => {
        console.log(error)
      }
    )
  }

  onActiveStatusChange() {
    this.isActive = !this.isActive
    this.changeStatus(this.isActive)
  }

  lastLogon() {
    this.httpClient.lastLogon((this.decodedToken)["client_id"]).subscribe(
      data => {
        let date = data.body;
        this.lastLogOn = this.datePipe.transform(date, 'yyyy-MM-dd hh:mm')
      },
      error => {
        console.log(error)
      }
    );
  }
  //sign out user
  signOut() {
    this.httpClient.logoutUser((this.decodedToken)["client_id"]).subscribe(
      res => {
        this.cookieService.delete("token");
        window.localStorage.removeItem("token");
        this.disconnect();
        this.router.navigate(["/logout"], {queryParams: {id: (this.decodedToken)["client_id"]}, queryParamsHandling: "merge"}).then(res =>{
        })
      },
      error => {
        console.log(error);
      }
    );

  }

  //-----------------------------------------------//

  //navigate user to settings page
  getClientToSettings() {
    this.router.navigate(['/settings'],{queryParams: {id: (this.decodedToken)['client_id']},queryParamsHandling: ""});
  }

  //----------------------------------------------------------------------------------------//

  //method to retrieve the data of user pic in order to show to
  getClientProfilePhoto() {
    this.http.getFile((this.decodedToken)['client_id']).subscribe(
      data => {
        this.imageUrl = "data:image/png;base64," + data.headers.get("File-Data");
      },
      error => {
        console.log(error)
      }
    )
  }

  //---------------------------------------------------------------------------------//

  //connect to webSocket session
  connect() {
    this.webSocket.connect();
  }

  //----------------------------//

  //disconnect from websocket session
  disconnect() {
    this.webSocket.disconnect();
  }

  //---------------------------------//

  //submit message by pressing enter
  onEnterSendMsg(event: any) {
    if (event.keyCode === 13) {
      this.handleSubmit(event);
    }
  }

  handleSubmit(event: any) {
    event.preventDefault();
  }

  //----------------------------------//


  //send message by clicking "SEND" button
  sendMessage() {
    if (this.message) {
      this.webSocket.sendMessage(this.fullName, this.message);
      this.message = "";
      this.time = this.webSocket.time;
    }
  }

  //-------------------------------------------------------------------------------------------//


  getAllClientsByFirstName() :any{
    this.httpClient.getAllClients().subscribe(
      data=>{
        if(data){
          this.clientsList = data.body;
        }
      },error => {
        console.log(error)
      }
    )
  }
}
