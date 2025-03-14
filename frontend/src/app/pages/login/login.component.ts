import { CommonModule, NgIf } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { AuthButtonComponent } from '../../components/auth-button/auth-button.component';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { AuthService } from '../../services/auth.service';


@Component({
  selector: 'app-login',
  imports: [FormsModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatButtonModule, 
    HttpClientModule, 
    MatProgressSpinnerModule, 
    MatIconModule, 
    CommonModule, 
    NgIf,
    AuthCardComponent,
    AuthButtonComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';
  hide: boolean = true;
  imagePath: String = "images/logo.png"

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.errorMessage = '';
    if (!this.email || !this.password) {
      this.errorMessage = 'Email dan password harus diisi!';
      return;
    }

    this.isLoading = true;

    const credentials = { email: this.email, password: this.password };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        console.log('Login berhasil:', response);
        localStorage.setItem('token', response.token);
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error('Login gagal:', err);
        this.errorMessage = 'Login gagal. Periksa kembali email dan password Anda.';
        this.isLoading = false;
      },
      complete: () => {
      }
    });
  }
}
