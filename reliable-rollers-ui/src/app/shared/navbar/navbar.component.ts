// src/app/shared/navbar/navbar.component.ts
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isAdmin: boolean = false; // ✅ Track admin status

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin(); // ✅ Check admin role
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}



