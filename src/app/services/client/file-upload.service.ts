import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private url = new URL("http://localhost:8080/files/")

  constructor(private httpClient: HttpClient) {
  }

  public uploadFile(file: File) : Observable<any>{
    // Create form data
    const formData = new FormData();

    // Store form name as "file" with file data
    formData.append("file", file,file.name);

    // Make http post request over api
    // with formData as req
    return this.httpClient.post(this.url + "upload", formData)
  }

  public getFile(id:string) : Observable<any>{
    return this.httpClient.get(this.url + id, {observe:'response'})
  }
}
