import {AfterViewInit, Component, Input, OnInit, Sanitizer} from '@angular/core';
import {HomePageService} from "../../services/page/homePage/home-page.service";
import {CookieService} from "ngx-cookie-service";
import {ActivatedRoute, Router} from "@angular/router";
import {FileUploadService} from "../../services/client/file-upload.service";
import {WebSocketService} from "../../services/message/web-socket.service";
import {HttpClientService} from "../../services/client/http-client.service";
import {DatePipe} from "@angular/common";

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
  public lastSeen: any;

  public token: any;

  public keyword = "";

  colors = [{status: true, color: "green"}, {status: false, color: "grey"}]
  public clientsList: any = [];

  private decodedToken: any;
  private clientID: any;
  public foundClients: string = "";

  @Input() searchedWord: string = "";

  constructor(private httpHomePageService: HomePageService,
              private cookieService: CookieService,
              private router: Router,
              private route: ActivatedRoute,
              private http: FileUploadService,
              public webSocket: WebSocketService,
              private httpClient: HttpClientService,
              private datePipe: DatePipe) {
  }


  //---------------------------------------------------------------------------------------//

  ngOnInit(): void {
    this.httpHomePageService.verifyAccess().subscribe(res => {
      this.token = res.body;
      this.getClaimsFromToken(this.token);
      this.connect()
      this.getAllClients()
    }, error => {
      if (error) {
        this.router.navigate(["/login"])
        console.clear()
      }
    })
  }

  lastSeenGetter(clientID: any) {
    this.httpClient.lastSeen(clientID).subscribe(
      res => {
        if (res.body == 0) {
          this.lastSeen = "now";
          return;
        }
        this.lastSeen = res.body + " min(s) ago";
      }
    )
  }

  changeColorOnStatus(status: any) {
    return this.colors.filter(item => item.status === status)[0]?.color;
  }

  ngAfterViewInit(): void {

  }

  getClaimsFromToken(encryptedToken: any) {
    this.httpClient.getClaimsFromToken(encryptedToken).subscribe(
      data => {
        this.decodedToken = data.body;
        this.clientID = `${(this.decodedToken)["claims"]["client_id"]}`
        this.fullName = `${(this.decodedToken)["claims"]['first_name']}` + " "
          + `${(this.decodedToken)["claims"]['last_name']}`

        this.isUserActive(this.clientID);
        this.lastLogon(this.clientID);
        this.getClientProfilePhoto(this.clientID);
        this.lastSeenGetter(this.clientID);
      },
      error => {
        console.log(error);
      }
    )

  }

  isUserActive(clientID: any) {
    this.httpClient.isUserActive(clientID).subscribe(
      data => {
        this.isActive = data.body;

      },
      error => {
        console.log(error)
      }
    );
  }

  changeStatus(clientID: any, status: boolean) {
    this.httpClient.changeStatus(clientID, status).subscribe(
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
    this.changeStatus(this.clientID, this.isActive)
    if (!this.isActive) {

    }
  }

  lastLogon(clientID: any) {
    this.httpClient.lastLogon(clientID).subscribe(
      data => {
        let date = data.body;
        this.lastLogOn = this.datePipe.transform(date, 'yyyy-MM-dd hh:mm a')
      },
      error => {
        console.log(error)
      }
    );
  }

  //sign out user
  signOut() {
    this.httpClient.logoutUser(this.clientID).subscribe(
      res => {
        this.disconnect();
        this.cookieService.delete("enc_token")
        this.router.navigate(["/login"], {queryParams: {id: this.clientID}, queryParamsHandling: "merge"}).then(res => {
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
    this.router.navigate(['/settings'], {
      queryParams: {
        id: this.clientID

      }, queryParamsHandling: ""
    });
  }

  //----------------------------------------------------------------------------------------//

  //method to retrieve the data of user pic in order to show to
  getClientProfilePhoto(clientID: any) {
    this.http.getFile(clientID).subscribe(
      data => {
        this.imageUrl = "data:image/png;base64," + data.body;
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
    }
  }

  //-------------------------------------------------------------------------------------------//


  getAllClients() {
    this.httpClient.getAllClients().subscribe(
      data => {
        this.clientsList = data.body;
      }, error => {
        console.log(error)
      }
    )
  }

  getClientByKeyword() {
    this.httpClient.getClientByKeyword(this.searchedWord).subscribe(res => {
      this.foundClients = res.body
    }, error => {
      console.log(error)
    })
  }
}
