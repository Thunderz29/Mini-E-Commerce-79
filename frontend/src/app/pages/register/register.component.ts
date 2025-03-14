import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthButtonComponent } from '../../components/auth-button/auth-button.component';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { RegisterDTO } from '../../models/register.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    HttpClientModule,
    AuthCardComponent,
    AuthButtonComponent
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  user: RegisterDTO = {
    username: '',
    email: '',
    password: '',
    phone: ''
  };

  imagePath: string = "images/logo.png";
  isLoading: boolean = false;
  errorMessage: string = '';
  emailError: string = '';
  passwordError: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  validateEmail() {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    this.emailError = emailRegex.test(this.user.email) ? '' : 'Format email tidak valid';
  }

  validatePassword() {
    this.passwordError = this.user.password.length >= 6 ? '' : 'Password harus minimal 6 karakter';
  }

  onRegister() {
    this.errorMessage = '';

    // Validasi sebelum mengirim request
    if (!this.user.username || !this.user.email || !this.user.password || !this.user.phone) {
      this.errorMessage = 'Semua bidang harus diisi!';
      return;
    }
    
    if (this.emailError || this.passwordError) {
      return; // Jangan lanjut jika ada error validasi
    }

    this.isLoading = true;

    this.authService.register(this.user).subscribe({
      next: (response) => {
        alert('Registrasi berhasil!');
        console.log(response);
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.errorMessage = `Registrasi gagal! ${error.error?.message || 'Silakan coba lagi.'}`;
        console.error(error);
        this.isLoading = false;
      },
      complete: () => {
      }
    });
  }
}
