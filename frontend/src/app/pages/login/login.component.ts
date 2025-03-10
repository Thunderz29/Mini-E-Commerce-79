import { HttpClientModule } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, HttpClientModule], // Tambahkan ini
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  private authService = inject(AuthService); // Inject AuthService
  private router = inject(Router);

  onLogin() {
    const credentials = { email: this.email, password: this.password };
    
    this.authService.login(credentials).subscribe({
      next: (response) => {
        console.log('Login berhasil:', response);
        localStorage.setItem('token', response.token); // Simpan token di localStorage
        this.router.navigate(['/dashboard']); // Redirect ke dashboard
      },
      error: (err) => {
        console.error('Login gagal:', err);
        alert('Login gagal. Periksa kembali email dan password Anda.');
      }
    });
  }}
