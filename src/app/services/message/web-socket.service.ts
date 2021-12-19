import {Injectable} from '@angular/core';
import {webSocket} from "rxjs/webSocket";
import {Stomp} from "@stomp/stompjs";
import * as SockJS from "sockjs-client";
import {DatePipe} from "@angular/common";
import {FileUploadService} from "../client/file-upload.service";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  stompClient: any;

  public message: string[] = [];

  constructor(private fileService:FileUploadService,private datePipe: DatePipe) {
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

    this.stompClient = Stomp.over(function(){
      return new SockJS("http://localhost:8080/ws"); //reconnecting purpose
    });

    this.stompClient.connect({}, (frame:any) => {
      this.stompClient.subscribe('/topic/public', (message: any) => {

        if(message.body) {
          let messageOutPut = JSON.parse(message.body)
          this.message.push(messageOutPut.time+" | "+messageOutPut.sender+": "+messageOutPut.content);
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
