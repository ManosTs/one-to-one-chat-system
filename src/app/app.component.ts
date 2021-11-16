import {Component, ElementRef} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  title = 'THE OUTLINE VISION';
  router: string;

  constructor(private elementRef: ElementRef,private _router: Router){

    this.router = _router.url;
  }

  ngAfterViewInit() {
    this.elementRef.nativeElement.ownerDocument
      .body.style.backgroundColor = 'white';
  }
}
