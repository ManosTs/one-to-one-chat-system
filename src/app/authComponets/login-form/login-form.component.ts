import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild, ViewRef} from '@angular/core';
import {HttpClientService, LoginUser} from "../../services/client/http-client.service";
import {HttpResponse} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {check, wind} from "ngx-bootstrap-icons";
import {map, tap} from "rxjs/operators";
import {JwtHelperService} from "@auth0/angular-jwt";
import {FileUploadService} from "../../services/client/file-upload.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.sass']
})
export class LoginFormComponent implements OnInit {

  public url: any


  public loading: boolean = false;

  public showFirstError: boolean = false;
  public showSecondError: boolean = false;

  public remember: boolean = false;

  private token: any;

  public emailError: string = '';

  public passwordError: string = '';

  @ViewChild("passwordInput", {static: false}) passwordInput: ElementRef<HTMLInputElement> = {} as ElementRef;

  @Input()
  emailPattern: string | RegExp = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

  user: LoginUser = new LoginUser("", "");

  constructor(private httpClientService: HttpClientService, private cookie: CookieService,
              private jwtHelper: JwtHelperService,
              private httpFile: FileUploadService,
              private router: Router) {

  }

  hideFirstError() {
    this.showFirstError = !this.showFirstError;
  }

  hideSecondError() {
    this.showSecondError = !this.showSecondError;
  }

  validationForm(): boolean {
    if (this.user.email === "" && this.user.password === "") {
      this.emailError = "EMAIL FIELD IS REQUIRED";
      this.passwordError = "PASSWORD FIELD IS REQUIRED";
      return false;
    } else {
      this.emailError = "";
      this.passwordError = "";
    }

    if (this.user.email === "") {
      this.emailError = "EMAIL FIELD IS REQUIRED";
      return false;
    } else {
      this.emailError = "";
    }

    if (this.user.password === "") {
      this.passwordError = "PASSWORD FIELD IS REQUIRED";
      return false;
    } else {
      this.passwordError = "";
    }


    if (!this.user.email.match(this.emailPattern)) {
      this.emailError = "EMAIL IS NOT VALID";
      return false;
    } else {
      this.emailError = ""
    }

    return true;

  }

  loginUser(isFormValid: boolean) {

    if (isFormValid) {
      this.loading = !this.loading
      this.httpClientService.loginUser(this.user).subscribe(
        (res) => {
          this.user = res;
          this.token = res.headers.get("Authorization")
          if (this.token === null) {
            return;
          }
          this.cookie.set("sessionID",this.token);

          // this.rememberMe()
          this.router.navigate([""],
            {
              queryParams:
                {
                  access_token: this.token
                },
              queryParamsHandling: "merge"
            }).then(res => {
                this.router.navigate(["/home"])
                console.log("Logged in successfully" + res)
          })
        },
        error => {
          if (error.status === 404) {
            this.emailError = "EMAIL NOT FOUND"
          }
          if (error.status === 403) {
            this.emailError = "ACCESS DENIED, CHECK PASSWORD"
          }
          console.error(error)
        })
    }
  }

  rememberValue(event: any) {
    this.remember = event.target.checked
  }

  rememberMe() {
    if (this.remember) {
      window.localStorage.setItem("token", this.token)
      return;
    }
    window.localStorage.removeItem("token");
  }

  ngOnInit(): void {
    this.router.navigate(['login'])
    this.autoCompleteCred()
    this.url = "https://az-pe.com/wp-content/uploads/2018/05/blank-profile-picture-973460_960_720-200x200.png"
  }

  autoCompleteCred() {
    let token = window.localStorage.getItem("token")
    if (token != null) {
      this.user.email = (this.jwtHelper.decodeToken(token))['sub']
      this.remember = true
      this.getClientProfile(token);
    }
  }

  getClientProfile(token: any) {
    token = window.localStorage.getItem("token")
    this.httpFile.getFile((this.jwtHelper.decodeToken(token))['profile_picture']).subscribe(
      data => {
        this.url = "data:image/png;base64," + data.headers.get("File-Data");
      },
      error => {
        console.log(error)
      }
    )
  }

  onSubmit() {
    let isFormValid = this.validationForm();
    this.loginUser(isFormValid);
  }

}
