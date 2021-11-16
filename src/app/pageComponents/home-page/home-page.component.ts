import {Component, OnInit} from '@angular/core';
import {HomePageService} from "../../services/page/homePage/home-page.service";
import {CookieService} from "ngx-cookie-service";
import {JwtHelperService} from "@auth0/angular-jwt";
import {Router} from "@angular/router";
import {FileUploadService} from "../../services/client/file-upload.service";
import {WebSocketService} from "../../services/message/web-socket.service";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.sass']
})
export class HomePageComponent implements OnInit {
  public imageUrl: any;
  public friends:boolean = false;
  public fullName:any;
  public message: any;
  public time:any;

  constructor(private httpHomePageService: HomePageService,
              private cookieService: CookieService,
              private jwtHelper: JwtHelperService,
              private router: Router,
              private http: FileUploadService,
              public webSocket: WebSocketService) {
  }

  //decode Token to retrieve info from user
  private decodedToken: any = this.jwtHelper.decodeToken(this.cookieService.get("token"));
  //---------------------------------------------------------------------------------------//

  ngOnInit(): void {
    this.getClientProfilePic()
    this.connect()
    this.fullName = (this.decodedToken)["first_name"] + " " + (this.decodedToken)["last_name"]
  }

  //sign out user
  signOut() {
    this.cookieService.delete("token");
    this.disconnect();
    window.localStorage.removeItem("token");
    window.location.reload();
  }
  //-----------------------------------------------//

  //navigate user to settings page
  getClientToSettings() {
    this.router.navigate(['/settings/id/', `${(this.decodedToken)['client_id']}`]);
  }
  //----------------------------------------------------------------------------------------//

  //method to retrieve the data of user pic in order to show to
  getClientProfilePic() : any {
    this.http.getFile((this.decodedToken)['profile_picture']).subscribe(
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
  onEnterSendMsg(event:any){
    if(event.keyCode === 13){
      this.handleSubmit(event);
    }
  }
  handleSubmit(event:any){
    event.preventDefault();
  }
  //----------------------------------//


  //send message by clicking "SEND" button
  sendMessage() {
    if(this.message) {
      this.webSocket.sendMessage(this.fullName,this.message);
      this.message = "";
      this.time = this.webSocket.time;
    }
  }
  //-------------------------------------------------------------------------------------------//


}
