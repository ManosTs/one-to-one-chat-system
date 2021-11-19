import {Component, Input, OnInit} from '@angular/core';
import {HttpClientService, RegisterUser} from "../../services/client/http-client.service";
import {FileUploadService} from "../../services/client/file-upload.service";
import * as uuid from "uuid";

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.sass']
})
export class RegisterFormComponent implements OnInit {

  public emailError: string = '';

  public firstNameError: string = '';

  public lastNameError: string = '';

  public passwordError: string = '';

  public confirmPassword: string = ''

  public confirmPasswordError: string = '';
  @Input()
  emailPattern: string | RegExp = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

  user: RegisterUser = new RegisterUser("","", "", "");

  public loading: boolean = false;

  constructor(private httpClientService: HttpClientService) {
  }

  validationForm(): boolean {
    if((this.user.email &&
      this.user.firstName &&
      this.user.lastName &&
      this.user.password) == "") {
      this.emailError = "EMAIL FIELD IS REQUIRED";
      this.passwordError = "PASSWORD FIELD IS REQUIRED";
      this.firstNameError = "FIRST NAME FIELD IS REQUIRED"
      this.lastNameError = "LAST NAME FIELD IS REQUIRED"
      this.confirmPasswordError = "CONFIRM PASSWORD FIELD IS REQUIRED"
      return false;
    } else {
      this.emailError = "";
      this.passwordError = "";
      this.firstNameError = ""
      this.lastNameError = ""
      this.confirmPasswordError = ""
    }

    if (this.user.email === "") {
      this.emailError = "EMAIL FIELD IS REQUIRED";
      return false;
    } else {
      this.emailError = "";
    }

    if (this.user.firstName === "") {
      this.firstNameError = "FIRST NAME FIELD IS REQUIRED";
      return false;
    } else {
      this.firstNameError = "";
    }

    if (this.user.lastName === "") {
      this.lastNameError = "LAST NAME FIELD IS REQUIRED";
      return false;
    } else {
      this.lastNameError = "";
    }

    if (this.user.password === "") {
      this.passwordError = "PASSWORD FIELD IS REQUIRED";
      return false;
    } else {
      this.passwordError = "";
    }

    if (this.confirmPassword === "") {
      this.confirmPasswordError = "CONFIRM PASSWORD FIELD IS REQUIRED";
      return false;
    } else {
      this.confirmPasswordError = "";
    }

    if (!this.user.email.match(this.emailPattern)) {
      this.emailError = "EMAIL IS NOT VALID";
      return false;
    } else {
      this.emailError = "";
    }

    if (this.confirmPassword != this.user.password) {
      this.confirmPasswordError = "PASSWORDS DO NOT MATCH"
      return false;
    } else {
      this.confirmPasswordError = "";
    }

    return true;
  }



  registerUser(isFormValid: boolean) {
    if (isFormValid) {
      this.loading = !this.loading;
      this.httpClientService.registerUser(this.user).subscribe(
        data => {
          this.user = data
          window.location.href = "/login"
        },
        error => {
          if (error.status === 304) {
            this.emailError = "EMAIL ALREADY EXISTS"
            return;
          }
        }
      )
    }
  }

  ngOnInit(): void {
  }

  onSubmit() {
    let isFormValid = this.validationForm();
    this.registerUser(isFormValid);
  }

}
