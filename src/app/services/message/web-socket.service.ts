import {Injectable} from '@angular/core';
import {webSocket} from "rxjs/webSocket";
import {Stomp} from "@stomp/stompjs";
import * as SockJS from "sockjs-client";
import {DatePipe} from "@angular/common";
import {Timestamp} from "rxjs";
import {HomePageService} from "../page/homePage/home-page.service";
import {HomePageComponent} from "../../pageComponents/home-page/home-page.component";
import {FileUploadService} from "../client/file-upload.service";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  stompClient: any;

  public message: string[] = [];
  public time: string[] = [];
  public sender: string[] = [];

  constructor(private fileService:FileUploadService) {
  }
  //--------------------------------------------------------------------------------//

  //send message to server
  sendMessage(from: any,  message: any) {
    let chatMessage = {
      content: message,
      sender: from,
      messageType:'RECEIVED'
    }
    this.stompClient.send("/app/chat.sendMessage", {},
      JSON.stringify(chatMessage));
  }
  //----------------------------------------------------------//

  //connect user to websocket session and get message objects
  connect() {
    let socket = new SockJS("http://localhost:8080/ws");
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, (frame:any) => {
      this.stompClient.subscribe('/topic/public', (message: any) => {
        if(message.body) {
          let messageOutPut = JSON.parse(message.body)
          this.message.push(messageOutPut.content);
          this.time.push(messageOutPut.time);
          this.sender.push(messageOutPut.sender);
        }
      })
    })
  }
  //---------------------------------------------------------------------------//

  //disconnect user from websocket session
  disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log("Disconnected");
  }
  //-----------------------------------------//
}
