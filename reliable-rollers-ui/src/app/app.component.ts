// src/app/app.component.ts
import { Component } from '@angular/core';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'reliable-rollers-ui';
  constructor(public authService: AuthService) {}
}

